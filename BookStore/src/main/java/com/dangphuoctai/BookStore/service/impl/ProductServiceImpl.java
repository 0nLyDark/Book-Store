package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.Category;
import com.dangphuoctai.BookStore.entity.File;
import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.entity.Publisher;
import com.dangphuoctai.BookStore.entity.Supplier;
import com.dangphuoctai.BookStore.enums.FileType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.ProductSpecification;
import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;
import com.dangphuoctai.BookStore.repository.AuthorRepo;
import com.dangphuoctai.BookStore.repository.CategoryRepo;
import com.dangphuoctai.BookStore.repository.FileRepo;
import com.dangphuoctai.BookStore.repository.LanguageRepo;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.PublisherRepo;
import com.dangphuoctai.BookStore.repository.SupplierRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.ProductService;
import com.dangphuoctai.BookStore.utils.CacheKeyGenerator;
import com.dangphuoctai.BookStore.utils.CreateSlug;

import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class ProductServiceImpl implements ProductService {

        @Autowired
        private ProductRepo productRepo;

        @Autowired
        private FileRepo fileRepo;

        @Autowired
        private CategoryRepo categoryRepo;

        @Autowired
        private AuthorRepo authorRepo;

        @Autowired
        private LanguageRepo languageRepo;

        @Autowired
        private SupplierRepo supplierRepo;

        @Autowired
        private PublisherRepo publisherRepo;

        @Autowired
        private OrderItemRepo orderItemRepo;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        private FileService fileService;

        @Value("${project.image}")
        private String path;
        @Autowired
        private BaseRedisService<String, String, Integer> productDayNewRedisService;

        @Autowired
        private BaseRedisService<String, String, ProductDTO> productRedisService;

        @Autowired
        private BaseRedisService<String, String, ProductResponse> productResponseRedisService;

        private static final String PRODUCT_CACHE_KEY = "product";
        private static final String PRODUCT_DAY_NEW_KEY = "product:dayNew";

        private static final String PRODUCT_PAGE_CACHE_KEY = "product:pages";

        @Override
        public ProductDTO getProductById(Long productId) {
                // Check in Redis cache
                // String field = "id:" + productId;
                // ProductDTO cached = (ProductDTO)
                // productRedisService.hashGet(PRODUCT_CACHE_KEY, field);
                // if (cached != null) {
                // return cached;
                // }
                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                // Save cache product to redis
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, field, productDTO);
                // productRedisService.setTimeToLiveOnce(PRODUCT_CACHE_KEY, 30,
                // TimeUnit.MINUTES);

                return productDTO;
        }

        @Override
        public ProductDTO getProductBySlug(String slug) {
                // Check in Redis cache
                // String field = "slug:" + slug;
                // ProductDTO cached = (ProductDTO)
                // productRedisService.hashGet(PRODUCT_CACHE_KEY, field);
                // if (cached != null) {
                // return cached;
                // }
                Product product = productRepo.findBySlug(slug)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                // Save cache product to redis
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, field, productDTO);
                // productRedisService.setTimeToLiveOnce(PRODUCT_CACHE_KEY, 30,
                // TimeUnit.MINUTES);

                return productDTO;
        }

        public List<ProductDTO> getManyProductById(List<Long> productIds) {
                List<Product> products = productRepo.findAllById(productIds);
                if (products.size() != productIds.size()) {
                        throw new ResourceNotFoundException("Product", "productIds", productIds);
                }
                List<ProductDTO> productDTOs = products.stream()
                                .map(product -> modelMapper.map(product, ProductDTO.class))
                                .collect(Collectors.toList());
                return productDTOs;
        }

        private void getListCategoryIds(Category category, List<Long> ids) {
                ids.add(category.getCategoryId());
                category.getChildrens().forEach(c -> getListCategoryIds(c, ids));
        }

        @Transactional
        @Override
        public ProductResponse getAllProducts(String keyword, String isbn, Double minPrice, Double maxPrice,
                        Boolean isSale, Boolean isNew, Long categoryId, String slugCategory,
                        List<Long> authorIds, List<Long> languageIds,
                        List<Long> supplierIds, List<Long> publisherIds, Boolean status,
                        Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
                // Check in Redis cache
                String field = CacheKeyGenerator.generateProductCacheKey(
                                keyword, isbn, minPrice, maxPrice,
                                isSale, isNew, categoryId, slugCategory, authorIds, languageIds,
                                supplierIds, publisherIds, status,
                                pageNumber, pageSize, sortBy, sortOrder);
                ProductResponse cached = (ProductResponse) productResponseRedisService.hashGet(PRODUCT_PAGE_CACHE_KEY,
                                field);
                if (cached != null) {
                        return cached;
                }
                Integer dayNew = productDayNewRedisService.get(PRODUCT_DAY_NEW_KEY);
                if (dayNew == null) {
                        dayNew = 14;
                }
                // Select products from database
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
                List<Long> categoryIds = new ArrayList<>();
                if (categoryId != null) {
                        Category category = categoryRepo.findById(categoryId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                                                        categoryId));
                        getListCategoryIds(category, categoryIds);
                } else if (slugCategory != null && !slugCategory.trim().isEmpty()) {
                        Category category = categoryRepo.findBySlug(slugCategory)
                                        .orElseThrow(() -> new ResourceNotFoundException("Category", "slugCategory",
                                                        slugCategory));
                        getListCategoryIds(category, categoryIds);
                }

                Specification<Product> productSpecification = ProductSpecification.filter(isbn, isNew ? dayNew : null,
                                minPrice, maxPrice, isSale, status, categoryIds,
                                authorIds, languageIds, supplierIds, publisherIds);

                Page<Product> pageProducts;
                if (keyword == null || keyword.trim().isEmpty()) {
                        pageProducts = productRepo.findAll(productSpecification, pageDetails);
                } else {
                        pageProducts = productRepo.fullTextSearchWithFilters(keyword,
                                        productSpecification,
                                        pageDetails);
                }

                List<ProductDTO> productDTOs = pageProducts.getContent().stream()
                                .map(product -> modelMapper.map(product, ProductDTO.class))
                                .collect(Collectors.toList());

                ProductResponse productResponse = new ProductResponse();
                productResponse.setContent(productDTOs);
                productResponse.setPageNumber(pageProducts.getNumber());
                productResponse.setPageSize(pageProducts.getSize());
                productResponse.setTotalElements(pageProducts.getTotalElements());
                productResponse.setTotalPages(pageProducts.getTotalPages());
                productResponse.setLastPage(pageProducts.isLast());

                // Save cache category to redis
                productResponseRedisService.hashSet(PRODUCT_PAGE_CACHE_KEY, field, productResponse);
                productResponseRedisService.setTimeToLiveOnce(PRODUCT_PAGE_CACHE_KEY, 30, TimeUnit.MINUTES);

                return productResponse;
        }

        @Override
        public ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> images, List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Product product = new Product();
                product.setProductName(productDTO.getProductName());
                product.setSearchText(CreateSlug.removeAccents(productDTO.getProductName()));
                product.setSlug(CreateSlug.toSlug(productDTO.getProductName()));
                product.setIsbn(productDTO.getIsbn());
                product.setWeight(productDTO.getWeight());
                product.setSize(productDTO.getSize());
                product.setFormat(productDTO.getFormat());
                product.setYear(productDTO.getYear());
                product.setQuantity(0);
                product.setPrice(productDTO.getPrice());
                product.setDiscount(productDTO.getDiscount());
                product.setPageNumber(productDTO.getPageNumber());
                product.setDescription(productDTO.getDescription());
                product.setStatus(false);
                product.setCreatedBy(userId);
                product.setUpdatedBy(userId);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                // Set List Image
                for (MultipartFile image : images) {
                        String fileName = fileService.uploadImage(path, image);
                        File file = new File();
                        file.setFileName(fileName);
                        file.setType(FileType.IMAGE);
                        file.setProduct(product);
                        product.getImages().add(file);
                }
                // Set Categories
                product.setCategories(categoryRepo.findAllById(categoryIds));
                // Set List Author
                product.setAuthors(new HashSet<>(authorRepo.findAllById(authorIds)));
                // Set List Language
                product.setLanguages(new HashSet<>(languageRepo.findAllById(languageIds)));
                // Set Supplier
                Supplier supplier = supplierRepo.findById(supplierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierId",
                                                supplierId));
                product.setSupplier(supplier);
                // Set Publisher
                Publisher publisher = publisherRepo.findById(publisherId)
                                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId",
                                                publisherId));
                product.setPublisher(publisher);

                // Save Product
                productRepo.save(product);
                ProductDTO productRes = modelMapper.map(product, ProductDTO.class);
                // Save cache product to redis
                // String field = "id:" + productRes.getProductId();
                // String fieldSlug = "slug:" + productRes.getSlug();
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, field, productRes);
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, fieldSlug, productRes);
                // productResponseRedisService.setTimeToLiveOnce(PRODUCT_CACHE_KEY, 30,
                // TimeUnit.MINUTES);
                productResponseRedisService.delete(PRODUCT_PAGE_CACHE_KEY);

                return modelMapper.map(product, ProductDTO.class);
        }

        @Override
        public ProductDTO updateProduct(ProductDTO productDTO, List<Long> oldImages, List<MultipartFile> images,
                        List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Product product = productRepo.findById(productDTO.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",
                                                productDTO.getProductId()));
                // Only update ISBN if product is not in any OrderItem
                if (!product.getIsbn().equals(productDTO.getIsbn())) {
                        boolean hasOrderItem = orderItemRepo.existsByProduct_ProductId(product.getProductId());
                        if (hasOrderItem) {
                                throw new APIException(
                                                "Sản phẩm không thể thay đổi mã ISBN vì đã tồn tại trong đơn hàng.");
                        }
                        product.setIsbn(productDTO.getIsbn());
                }

                product.setProductName(productDTO.getProductName());
                product.setSearchText(CreateSlug.removeAccents(productDTO.getProductName()));
                product.setSlug(CreateSlug.toSlug(productDTO.getProductName()));
                product.setWeight(productDTO.getWeight());
                product.setSize(productDTO.getSize());
                product.setFormat(productDTO.getFormat());
                product.setYear(productDTO.getYear());
                product.setPrice(productDTO.getPrice());
                product.setDiscount(productDTO.getDiscount());
                product.setPageNumber(productDTO.getPageNumber());
                product.setDescription(productDTO.getDescription());
                product.setStatus(productDTO.getStatus());
                product.setUpdatedBy(userId);
                product.setUpdatedAt(LocalDateTime.now());
                // Filter image
                product.getImages().removeIf(file -> !oldImages.contains(file.getFileId()));
                // Set List Image
                if (images != null) {
                        for (MultipartFile image : images) {
                                String fileName = fileService.uploadImage(path, image);
                                File file = new File();
                                file.setFileName(fileName);
                                file.setType(FileType.IMAGE);
                                file.setProduct(product);
                                product.getImages().add(file);
                        }
                }

                // Set Categories
                product.setCategories(categoryRepo.findAllById(categoryIds));
                // Set List Author
                product.setAuthors(new HashSet<>(authorRepo.findAllById(authorIds)));
                // Set List Language
                product.setLanguages(new HashSet<>(languageRepo.findAllById(languageIds)));
                // Set Supplier
                Supplier supplier = supplierRepo.findById(supplierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierId",
                                                supplierId));
                product.setSupplier(supplier);
                // Set Publisher
                Publisher publisher = publisherRepo.findById(publisherId)
                                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId",
                                                publisherId));
                product.setPublisher(publisher);

                // Save Product
                productRepo.save(product);
                ProductDTO productRes = modelMapper.map(product, ProductDTO.class);
                // Save cache product to redis
                // String field = "id:" + productRes.getProductId();
                // String fieldSlug = "slug:" + productRes.getSlug();
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, field, productRes);
                // productRedisService.hashSet(PRODUCT_CACHE_KEY, fieldSlug, productRes);
                // productResponseRedisService.setTimeToLiveOnce(PRODUCT_CACHE_KEY, 30,
                // TimeUnit.MINUTES);
                productResponseRedisService.delete(PRODUCT_PAGE_CACHE_KEY);

                return modelMapper.map(product, ProductDTO.class);
        }

        public String deleteProduct(Long productId) {
                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",
                                                productId));
                // Only Delete if product is not in any OrderItem
                boolean hasOrderItem = orderItemRepo.existsByProduct_ProductId(product.getProductId());
                if (hasOrderItem) {
                        throw new APIException("Sản phẩm không thể xóa vì đã tồn tại trong đơn hàng.");
                }
                // String field = "id:" + product.getProductId();
                // String fieldSlug = "slug:" + product.getSlug();
                // productRepo.delete(product);
                // productRedisService.delete(PRODUCT_CACHE_KEY, List.of(field, fieldSlug));
                productResponseRedisService.delete(PRODUCT_PAGE_CACHE_KEY);

                return "Xóa sản phẩm với ID: " + productId + " thành công";
        }

}
