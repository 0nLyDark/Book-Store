package com.dangphuoctai.BookStore.utils;

import org.apache.commons.codec.digest.DigestUtils;

import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;

import java.nio.charset.StandardCharsets;

public class HashUtil {
    public static String generatePromotionHash(Object promo) {
        String data = "";
        if (promo instanceof Promotion) {
            Promotion p = (Promotion) promo;
            data = p.getPromotionName() + "|" +
                    p.getPromotionCode() + "|" +
                    p.getPromotionType().name() + "|" +
                    p.getStartDate().toString() + "|" +
                    p.getEndDate().toString() + "|" +
                    p.getValue() + "|" +
                    p.getValueApply() + "|" +
                    p.getValueType() + "|" +
                    (p.getDescription() != null ? p.getDescription() : "");
        } else if (promo instanceof PromotionDTO) {
            PromotionDTO p = (PromotionDTO) promo;
            data = p.getPromotionName() + "|" +
                    p.getPromotionCode() + "|" +
                    p.getPromotionType().name() + "|" +
                    p.getStartDate().toString() + "|" +
                    p.getEndDate().toString() + "|" +
                    p.getValue() + "|" +
                    p.getValueApply() + "|" +
                    p.getValueType() + "|" +
                    (p.getDescription() != null ? p.getDescription() : "");
        } else {
            throw new IllegalArgumentException("Unsupported object type");
        }
        return DigestUtils.sha256Hex(data.getBytes(StandardCharsets.UTF_8));
    }
}
