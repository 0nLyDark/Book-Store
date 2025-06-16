package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.entity.PromotionSnapshot;
import com.dangphuoctai.BookStore.enums.PromotionType;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.PromotionSpecification;
import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;
import com.dangphuoctai.BookStore.payloads.response.PromotionResponse;
import com.dangphuoctai.BookStore.repository.PromotionRepo;
import com.dangphuoctai.BookStore.repository.PromotionSnapshotRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.PromotionService;
import com.dangphuoctai.BookStore.utils.HashUtil;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepo promotionRepo;

    @Autowired
    private PromotionSnapshotRepo promotionSnapshotRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseRedisService<String, String, PromotionDTO> promotionRedisService;

    @Autowired
    private BaseRedisService<String, String, PromotionResponse> promotionResponseRedisService;

    private static final String PROMOTION_CACHE_KEY = "promotion";
    private static final String PROMOTION_PAGE_CACHE_KEY = "promotion:pages";

    @Override
    public PromotionDTO getPromotionById(Long promotionId) {
        // Check in Redis cache
        String field = "id:" + promotionId;
        PromotionDTO cached = (PromotionDTO) promotionRedisService.hashGet(PROMOTION_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Promotion promotion = promotionRepo.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionId", promotionId));

        PromotionDTO promotionDTO = modelMapper.map(promotion, PromotionDTO.class);
        // Save cache product to redis
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, field, promotionDTO);
        promotionRedisService.setTimeToLiveOnce(PROMOTION_CACHE_KEY, 30, TimeUnit.MINUTES);

        return promotionDTO;
    }

    @Override
    public PromotionDTO getPromotionByCode(String code) {
        // Check in Redis cache
        String field = "code:" + code;
        PromotionDTO cached = (PromotionDTO) promotionRedisService.hashGet(PROMOTION_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Promotion promotion = promotionRepo.findByPromotionCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));

        PromotionDTO promotionDTO = modelMapper.map(promotion, PromotionDTO.class);
        // Save cache product to redis
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, field, promotionDTO);
        promotionRedisService.setTimeToLiveOnce(PROMOTION_CACHE_KEY, 30, TimeUnit.MINUTES);

        return promotionDTO;
    }

    @Override
    public List<PromotionDTO> getAllPromotionByIds(List<Long> promotionIds) {
        List<Promotion> promotions = promotionRepo.findAllById(promotionIds);
        if (promotions.size() != promotionIds.size()) {
            throw new ResourceNotFoundException("Promotion", "promotionIds", promotionIds);
        }
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(promotion -> modelMapper.map(promotion, PromotionDTO.class)).collect(Collectors.toList());

        return promotionDTOs;
    }

    @Override
    public PromotionResponse getAllPromotion(Boolean status, PromotionType type, Integer pageNumber, Integer pageSize,
            String sortBy,
            String sortOrder) {
        // Check in Redis cache
        String field = String.format("status:%s|type:%s|pageNumber:%d|pageSize:%d|sortBy:%s|sortOrder:%s",
                status, type, pageNumber, pageSize, sortBy, sortOrder);
        PromotionResponse cached = (PromotionResponse) promotionResponseRedisService.hashGet(PROMOTION_PAGE_CACHE_KEY,
                field);
        if (cached != null) {
            return cached;
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Specification<Promotion> promotionSpecification = PromotionSpecification.filter(type, status);

        Page<Promotion> pagePromotions = promotionRepo.findAll(promotionSpecification, pageDetails);

        List<Promotion> promotions = pagePromotions.getContent();

        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(promotion -> modelMapper.map(promotion, PromotionDTO.class)).collect(Collectors.toList());

        PromotionResponse promotionResponse = new PromotionResponse();
        promotionResponse.setContent(promotionDTOs);
        promotionResponse.setPageNumber(pagePromotions.getNumber());
        promotionResponse.setPageSize(pagePromotions.getSize());
        promotionResponse.setTotalElements(pagePromotions.getTotalElements());
        promotionResponse.setTotalPages(pagePromotions.getTotalPages());
        promotionResponse.setLastPage(pagePromotions.isLast());

        // Save cache promotion to redis
        promotionResponseRedisService.hashSet(PROMOTION_PAGE_CACHE_KEY, field, promotionResponse);
        promotionResponseRedisService.setTimeToLiveOnce(PROMOTION_PAGE_CACHE_KEY, 30, TimeUnit.MINUTES);

        return promotionResponse;

    }

    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Promotion promotion = new Promotion();
        promotion.setPromotionName(promotionDTO.getPromotionName());
        promotion.setPromotionCode(promotionDTO.getPromotionCode());
        promotion.setPromotionType(promotionDTO.getPromotionType());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setValue(promotionDTO.getValue());
        promotion.setValueApply(promotionDTO.getValueApply());
        promotion.setValueType(promotionDTO.getValueType());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setStatus(false);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        promotion.setCreatedBy(userId);
        promotion.setUpdatedBy(userId);

        promotionRepo.save(promotion);
        PromotionDTO promotionRes = modelMapper.map(promotion, PromotionDTO.class);
        // Save cache product to redis
        String field = "id:" + promotion.getPromotionId();
        String fieldCode = "code:" + promotion.getPromotionCode();
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, field, promotionRes);
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, fieldCode, promotionRes);
        promotionRedisService.setTimeToLiveOnce(PROMOTION_CACHE_KEY, 30, TimeUnit.MINUTES);
        promotionResponseRedisService.delete(PROMOTION_PAGE_CACHE_KEY);

        return promotionRes;
    }

    @Override
    public PromotionDTO updatePromotion(PromotionDTO promotionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        Promotion promotion = promotionRepo.findById(promotionDTO.getPromotionId()).orElseThrow(
                () -> new ResourceNotFoundException("Promotion", "promotionId", promotionDTO.getPromotionId()));
        promotion.setPromotionName(promotionDTO.getPromotionName());
        promotion.setPromotionCode(promotionDTO.getPromotionCode());
        promotion.setPromotionType(promotionDTO.getPromotionType());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setValue(promotionDTO.getValue());
        promotion.setValueApply(promotionDTO.getValueApply());
        promotion.setValueType(promotionDTO.getValueType());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setStatus(promotionDTO.getStatus());
        promotion.setUpdatedAt(LocalDateTime.now());
        promotion.setUpdatedBy(userId);
        promotionRepo.save(promotion);

        PromotionDTO promotionRes = modelMapper.map(promotion, PromotionDTO.class);
        // Save cache product to redis
        String field = "id:" + promotion.getPromotionId();
        String fieldCode = "code:" + promotion.getPromotionCode();
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, field, promotionRes);
        promotionRedisService.hashSet(PROMOTION_CACHE_KEY, fieldCode, promotionRes);
        promotionRedisService.setTimeToLiveOnce(PROMOTION_CACHE_KEY, 30, TimeUnit.MINUTES);
        promotionResponseRedisService.delete(PROMOTION_PAGE_CACHE_KEY);

        return promotionRes;
    }

    public String deletePromotion(Long promotionId) {
        Promotion promotion = promotionRepo.findById(promotionId).orElseThrow(
                () -> new ResourceNotFoundException("Promotion", "promotionId", promotionId));
        String field = "id:" + promotion.getPromotionId();
        String fieldCode = "code:" + promotion.getPromotionCode();
        promotionRepo.delete(promotion);
        // Delete cache product from redis
        promotionRedisService.delete(PROMOTION_CACHE_KEY, field);
        promotionRedisService.delete(PROMOTION_CACHE_KEY, fieldCode);
        promotionResponseRedisService.delete(PROMOTION_PAGE_CACHE_KEY);

        return "Khuyến mãi với ID " + promotionId + " đã được xóa thành công.";
    }

    @Override
    public PromotionSnapshot createPromotionSnapshot(Promotion promotion) {
        String hash = HashUtil.generatePromotionHash(promotion);
        PromotionSnapshot promotionSnapshot = promotionSnapshotRepo.findByHash(hash);
        if (promotionSnapshot == null) {
            promotionSnapshot = modelMapper.map(promotion, PromotionSnapshot.class);
            promotion.setPromotionId(null);
            promotionSnapshotRepo.save(promotionSnapshot);
        }

        return promotionSnapshot;
    }

}
