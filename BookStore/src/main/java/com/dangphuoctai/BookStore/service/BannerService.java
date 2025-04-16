package com.dangphuoctai.BookStore.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.BannerDTO;
import com.dangphuoctai.BookStore.payloads.response.BannerResponse;

public interface BannerService {

    BannerDTO getBannerById(Long bannerId);

    BannerResponse getAllBanners(Boolean status,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BannerDTO createBanner(BannerDTO bannerDTO, MultipartFile image) throws IOException;

    BannerDTO updateBanner(BannerDTO bannerDTO, MultipartFile image) throws IOException;

    String deleteBanner(Long bannerId);
}
