package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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
import com.dangphuoctai.BookStore.payloads.PromotionSpecification;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.response.PromotionResponse;
import com.dangphuoctai.BookStore.repository.PromotionRepo;
import com.dangphuoctai.BookStore.repository.PromotionSnapshotRepo;
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

    @Override
    public PromotionDTO getPromotionById(Long promotionId) {
        Promotion promotion = promotionRepo.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionId", promotionId));

        return modelMapper.map(promotion, PromotionDTO.class);
    }

    @Override
    public PromotionResponse getAllPromotion(Boolean status, PromotionType type, Integer pageNumber, Integer pageSize,
            String sortBy,
            String sortOrder) {
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

        return modelMapper.map(promotion, PromotionDTO.class);
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

        return modelMapper.map(promotion, PromotionDTO.class);
    }

    public String deletePromotion(Long promotionId) {
        Promotion promotion = promotionRepo.findById(promotionId).orElseThrow(
                () -> new ResourceNotFoundException("Promotion", "promotionId", promotionId));

        promotionRepo.delete(promotion);

        return "Promotion with ID " + promotionId + " has been deleted successfully.";
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
