package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.enums.PromotionType;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.response.PromotionResponse;
import com.dangphuoctai.BookStore.service.PromotionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping("/public/promotions/{promotionId}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long promotionId) {

        PromotionDTO promotionDTO = promotionService.getPromotionById(promotionId);

        return new ResponseEntity<PromotionDTO>(promotionDTO, HttpStatus.OK);
    }

    @GetMapping("/public/promotions/ids")
    public ResponseEntity<List<PromotionDTO>> getManyAuthorByIds(@RequestParam(value = "id") List<Long> promotionIds) {

        List<PromotionDTO> promotionDTOs = promotionService.getAllPromotionByIds(promotionIds);

        return new ResponseEntity<List<PromotionDTO>>(promotionDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/promotions")
    public ResponseEntity<PromotionResponse> getAllPublisher(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "type", defaultValue = "", required = false) String typeParam,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PROMOTION_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        PromotionType type = typeParam.isBlank() ? null : PromotionType.valueOf(typeParam);
        PromotionResponse promotionResponse = promotionService.getAllPromotion(status, type,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "promotionId" : sortBy,
                sortOrder);

        return new ResponseEntity<PromotionResponse>(promotionResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/promotions")
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionDTO promotionDTO) {
        PromotionDTO promotion = promotionService.createPromotion(promotionDTO);

        return new ResponseEntity<PromotionDTO>(promotion, HttpStatus.OK);
    }

    @PutMapping("/admin/promotions")
    public ResponseEntity<PromotionDTO> updatePromotion(@RequestBody PromotionDTO promotionDTO) {
        PromotionDTO promotion = promotionService.updatePromotion(promotionDTO);

        return new ResponseEntity<PromotionDTO>(promotion, HttpStatus.OK);
    }

    @DeleteMapping("/admin/promotions/{promotionId}")
    public ResponseEntity<String> updatePromotion(@PathVariable Long promotionId) {
        String message = promotionService.deletePromotion(promotionId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}
