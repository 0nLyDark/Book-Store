package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.entity.PromotionSnapshot;
import com.dangphuoctai.BookStore.enums.PromotionType;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.response.PromotionResponse;

public interface PromotionService {

    PromotionDTO getPromotionById(Long promotionId);

    PromotionDTO getPromotionByCode(String code);

    List<PromotionDTO> getAllPromotionByIds(List<Long> promotionIds);

    PromotionResponse getAllPromotion(Boolean status, PromotionType type, Integer pageNumber, Integer pageSize,
            String sortBy,
            String sortOrder);

    PromotionDTO createPromotion(PromotionDTO promotionDTO);

    PromotionDTO updatePromotion(PromotionDTO promotionDTO);

    String deletePromotion(Long promotionId);

    PromotionSnapshot createPromotionSnapshot(Promotion promotion);

}
