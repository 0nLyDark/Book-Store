package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.Banner;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.BannerDTO;
import com.dangphuoctai.BookStore.payloads.response.BannerResponse;
import com.dangphuoctai.BookStore.repository.BannerRepo;
import com.dangphuoctai.BookStore.service.BannerService;
import com.dangphuoctai.BookStore.service.FileService;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepo bannerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public BannerDTO getBannerById(Long bannerId) {
        Banner banner = bannerRepo.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "bannerId", bannerId));

        return modelMapper.map(banner, BannerDTO.class);
    }

    @Override
    public BannerResponse getAllBanners(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Banner> pageBanners = bannerRepo.findAll(pageDetails);

        List<BannerDTO> bannerDTOs = pageBanners.getContent().stream()
                .map(banner -> modelMapper.map(banner, BannerDTO.class))
                .collect(Collectors.toList());

        BannerResponse bannerResponse = new BannerResponse();
        bannerResponse.setContent(bannerDTOs);
        bannerResponse.setPageNumber(pageBanners.getNumber());
        bannerResponse.setPageSize(pageBanners.getSize());
        bannerResponse.setTotalElements(pageBanners.getTotalElements());
        bannerResponse.setTotalPages(pageBanners.getTotalPages());
        bannerResponse.setLastPage(pageBanners.isLast());

        return bannerResponse;
    }

    @Transactional
    @Override
    public BannerDTO createBanner(BannerDTO bannerDTO, MultipartFile image) throws IOException {
        String fileName = fileService.uploadImage(path, image);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Banner banner = new Banner();
        banner.setBannerName(bannerDTO.getBannerName());
        banner.setImage(fileName);
        banner.setLink(bannerDTO.getLink());
        banner.setPosition(bannerDTO.getPosition());
        banner.setStatus(false);

        banner.setCreatedBy(userId);
        banner.setUpdatedBy(userId);
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());

        bannerRepo.save(banner);

        return modelMapper.map(banner, BannerDTO.class);
    }

    @Transactional
    @Override
    public BannerDTO updateBanner(BannerDTO bannerDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Banner banner = bannerRepo.findById(bannerDTO.getBannerId())
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "bannerId", bannerDTO.getBannerId()));
        banner.setBannerName(bannerDTO.getBannerName());
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            banner.setImage(fileName);
        }
        banner.setLink(bannerDTO.getLink());
        banner.setPosition(bannerDTO.getPosition());
        banner.setStatus(bannerDTO.getStatus());
        banner.setUpdatedBy(userId);
        banner.setUpdatedAt(LocalDateTime.now());
        bannerRepo.save(banner);

        return modelMapper.map(banner, BannerDTO.class);
    }

    @Override
    public String deleteBanner(Long bannerId) {
        Banner banner = bannerRepo.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "bannerId", bannerId));
        bannerRepo.delete(banner);

        return "Banner with ID: " + bannerId + " deleted successfully";
    }

}
