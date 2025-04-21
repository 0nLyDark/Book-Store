package com.dangphuoctai.BookStore.utils;

import org.apache.commons.codec.digest.DigestUtils;

import com.dangphuoctai.BookStore.entity.Promotion;

import java.nio.charset.StandardCharsets;

public class HashUtil {
    public static String generatePromotionHash(Promotion promo) {
        String data = promo.getPromotionName() + "|" +
                promo.getPromotionCode() + "|" +
                promo.getPromotionType().name() + "|" +
                promo.getStartDate().toString() + "|" +
                promo.getEndDate().toString() + "|" +
                promo.getValue() + "|" +
                promo.getValueApply() + "|" +
                promo.getValueType() + "|" +
                (promo.getDescription() != null ? promo.getDescription() : "");

        // Tạo SHA-256 hash bằng Apache Commons Codec
        return DigestUtils.sha256Hex(data.getBytes(StandardCharsets.UTF_8));
    }
}
