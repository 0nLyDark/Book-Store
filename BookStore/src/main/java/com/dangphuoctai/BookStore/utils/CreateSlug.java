package com.dangphuoctai.BookStore.utils;

import java.text.Normalizer;

public class CreateSlug {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Chuyển thành chữ thường và chuẩn hóa chuỗi
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD);
        slug = slug.replaceAll("đ|Đ", "d");
        // Loại bỏ dấu tiếng Việt và các ký tự đặc biệt
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay khoảng trắng và các ký tự đặc biệt thành dấu "-"
        slug = slug.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "-").toLowerCase().trim();

        // Xóa dấu "-" dư thừa ở đầu/cuối
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
}
