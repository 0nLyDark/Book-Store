package com.dangphuoctai.BookStore.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.BannerDTO;
import com.dangphuoctai.BookStore.payloads.response.BannerResponse;
import com.dangphuoctai.BookStore.service.BannerService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/public/banners/{bannerId}")
    public ResponseEntity<BannerDTO> getBannerById(@PathVariable Long bannerId) {
        BannerDTO bannerDTO = bannerService.getBannerById(bannerId);

        return new ResponseEntity<BannerDTO>(bannerDTO, HttpStatus.OK);
    }

    @GetMapping("/public/banners")
    public ResponseEntity<BannerResponse> getAllBanners(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BANNERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        BannerResponse bannerResponse = bannerService.getAllBanners(status,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "bannerId" : sortBy,
                sortOrder);

        return new ResponseEntity<BannerResponse>(bannerResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/banners")
    public ResponseEntity<BannerDTO> createBanner(@RequestParam("file") MultipartFile image,
            @ModelAttribute BannerDTO bannerDTO) throws IOException {
        BannerDTO createdBanner = bannerService.createBanner(bannerDTO, image);

        return new ResponseEntity<BannerDTO>(createdBanner, HttpStatus.CREATED);
    }

    @PutMapping("/staff/banners")
    public ResponseEntity<BannerDTO> updateBanner(@RequestParam(value = "file", required = false) MultipartFile image,
            @ModelAttribute BannerDTO bannerDTO) throws IOException {
        BannerDTO createdBanner = bannerService.updateBanner(bannerDTO, image);

        return new ResponseEntity<BannerDTO>(createdBanner, HttpStatus.OK);
    }

    @DeleteMapping("/admin/banners/{bannerId}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long bannerId) {
        String message = bannerService.deleteBanner(bannerId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}