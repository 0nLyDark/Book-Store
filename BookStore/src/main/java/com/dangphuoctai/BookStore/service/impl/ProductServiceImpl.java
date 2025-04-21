package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.entity.Publisher;
import com.dangphuoctai.BookStore.entity.Supplier;
import com.dangphuoctai.BookStore.enums.FileType;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.ProductSpecification;
import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;
import com.dangphuoctai.BookStore.repository.AuthorRepo;
import com.dangphuoctai.BookStore.repository.CategoryRepo;
import com.dangphuoctai.BookStore.repository.FileRepo;
import com.dangphuoctai.BookStore.repository.LanguageRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.PublisherRepo;
import com.dangphuoctai.BookStore.repository.SupplierRepo;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.ProductService;
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
        private ModelMapper modelMapper;

        @Autowired
        private FileService fileService;

        @Value("${project.image}")
        private String path;

        @Override
        public ProductDTO getProductById(Long productId) {
                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

                return modelMapper.map(product, ProductDTO.class);
        }

        @Override
        public ProductDTO getProductBySlug(String slug) {
                Product product = productRepo.findBySlug(slug)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

                return modelMapper.map(product, ProductDTO.class);
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

        // @Override
        // public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize,
        // String sortBy, String sortOrder) {
        // Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
        // Sort.by(sortBy).ascending()
        // : Sort.by(sortBy).descending();
        // Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        // Page<Product> pageProducts = productRepo.findAll(pageDetails);
        // List<ProductDTO> productDTOs = pageProducts.getContent().stream()
        // .map(product -> modelMapper.map(product, ProductDTO.class))
        // .collect(Collectors.toList());
        // ProductResponse productResponse = new ProductResponse();
        // productResponse.setContent(productDTOs);
        // productResponse.setPageNumber(pageProducts.getNumber());
        // productResponse.setPageSize(pageProducts.getSize());
        // productResponse.setTotalElements(pageProducts.getTotalElements());
        // productResponse.setTotalPages(pageProducts.getTotalPages());
        // productResponse.setLastPage(pageProducts.isLast());
        // return productResponse;
        // }

        private void getListCategoryIds(Category category, List<Long> ids) {
                ids.add(category.getCategoryId());
                category.getChildrens().forEach(c -> getListCategoryIds(c, ids));
        }

        // private List<Long> getListCategoryIds(Category category) {
        // List<Long> list = new ArrayList<>();
        // list.add(category.getCategoryId());
        // category.getChildrens().forEach(c -> list.addAll(getListCategoryIds(c)));

        // return list;
        // }

        @Transactional
        @Override
        public ProductResponse getAllProducts(String keyword, String isbn, Double minPrice, Double maxPrice,
                        Boolean isSale, Long categoryId,
                        List<Long> authorIds, List<Long> languageIds,
                        Long supplierId, Long publisherId, Boolean status,
                        Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
                List<Long> categoryIds = new ArrayList<>();
                if (categoryId != null) {
                        Category category = categoryRepo.findById(categoryId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                                                        categoryId));
                        getListCategoryIds(category, categoryIds);
                }
                Specification<Product> productSpecification = ProductSpecification.filter(keyword, isbn,
                                minPrice, maxPrice, isSale, status, categoryIds, null,
                                authorIds, languageIds, supplierId, publisherId);

                Page<Product> pageProducts = productRepo.findAll(productSpecification, pageDetails);
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
                product.setSlug(CreateSlug.toSlug(productDTO.getProductName()));
                product.setIsbn(productDTO.getIsbn());
                product.setWeight(productDTO.getWeight());
                product.setSize(productDTO.getSize());
                product.setYear(productDTO.getYear());
                product.setQuantity(productDTO.getQuantity());
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

                return modelMapper.map(product, ProductDTO.class);
        }

        @Override
        public ProductDTO updateProduct(ProductDTO productDTO, List<MultipartFile> images, List<Long> removeImage,
                        List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Product product = productRepo.findById(productDTO.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",
                                                productDTO.getProductId()));
                product.setProductName(productDTO.getProductName());
                product.setSlug(productDTO.getSlug());
                product.setIsbn(productDTO.getIsbn());
                product.setWeight(productDTO.getWeight());
                product.setSize(productDTO.getSize());
                product.setYear(productDTO.getYear());
                product.setQuantity(productDTO.getQuantity());
                product.setPrice(productDTO.getPrice());
                product.setDiscount(productDTO.getDiscount());
                product.setPageNumber(productDTO.getPageNumber());
                product.setDescription(productDTO.getDescription());
                product.setStatus(productDTO.getStatus());
                product.setUpdatedBy(userId);
                product.setUpdatedAt(LocalDateTime.now());
                // remove image
                Set<Long> safeRemoveImage = removeImage == null ? Set.of() : new HashSet<>(removeImage);
                List<File> updatedImages = product.getImages().stream()
                                .filter(file -> !safeRemoveImage.contains(file.getFileId()))
                                .collect(Collectors.toList());
                // Set List Image
                if (images != null) {
                        for (MultipartFile image : images) {
                                String fileName = fileService.uploadImage(path, image);
                                File file = new File();
                                file.setFileName(fileName);
                                file.setType(FileType.IMAGE);
                                file.setProduct(product);
                                updatedImages.add(file);
                        }
                }

                product.setImages(updatedImages);
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

                return modelMapper.map(product, ProductDTO.class);
        }

        public String deleteProduct(Long productId) {
                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",
                                                productId));
                productRepo.delete(product);

                return "Product with ID: " + productId + " deleted successfully";
        }

}
