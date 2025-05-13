package com.dangphuoctai.BookStore.config;

public class AppConstants {
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "5";
    public static final String SORT_CATEGORIES_BY = "categoryId";
    public static final String SORT_PRODUCTS_BY = "productId";
    public static final String SORT_USERS_BY = "userId";
    public static final String SORT_TOPICS_BY = "topicId";
    public static final String SORT_POSTS_BY = "postId";
    public static final String SORT_AUTHORS_BY = "authorId";
    public static final String SORT_LANGUAGES_BY = "languageId";
    public static final String SORT_CONTACTS_BY = "contactId";
    public static final String SORT_PUBLISHERS_BY = "publisherId";
    public static final String SORT_SUPPLIERS_BY = "supplierId";
    public static final String SORT_BANNERS_BY = "bannerId";
    public static final String SORT_MENUS_BY = "menuId";
    public static final String SORT_CARTS_BY = "cartId";
    public static final String SORT_IMPORTRECEIPT_BY = "importReceiptId";
    public static final String SORT_PROMOTION_BY = "promotionId";

    public static final String SORT_ORDERS_BY = "totalAmount";
    public static final String SORT_DIR = "asc";
    public static final Long ADMIN_ID = 101L;
    public static final Long USER_ID = 102L;
    public static final Long STAFF_ID = 103L;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    public static final String[] PUBLIC_URLS = { "/v3/api-docs/**", "/swagger-ui/**", "/api/register/**",
            "/api/auth/login",
            "/api/auth/google",
            "/api/auth/refresh-token",
            "/api/auth/verify/**",
            "/api/public/banners/**",
            "/api/public/menus/**",
            "/api/public/topics/**",
            "/api/public/posts/**",
            "/api/public/authors/**",
            "/api/public/languages/**",
            "/api/public/contacts",
            "/api/public/publishers",
            "/api/public/suppliers",
            "/api/public/categories/**",
            "/api/public/products/**",
            "/api/public/file/**",
            "api/public/orders/customer",
            "api/public/orders/otp",
            "api//public/promotions/**",
            "api/public/payment/**" };
    public static final String[] USER_URLS = { "/api/public/**" };
    public static final String[] STAFF_URLS = { "/api/staff/**" };
    public static final String[] ADMIN_URLS = { "/api/admin/**" };

}