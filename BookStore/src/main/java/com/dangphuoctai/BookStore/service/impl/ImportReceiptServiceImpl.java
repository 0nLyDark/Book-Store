package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.ImportReceipt;
import com.dangphuoctai.BookStore.entity.ImportReceiptItem;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.entity.Supplier;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.ImportReceiptSpecification;
import com.dangphuoctai.BookStore.payloads.dto.ImportReceiptDTO;
import com.dangphuoctai.BookStore.payloads.response.ImportReceiptResponse;
import com.dangphuoctai.BookStore.repository.ImportReceiptRepo;
import com.dangphuoctai.BookStore.repository.ImprortReceiptItemRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.SupplierRepo;
import com.dangphuoctai.BookStore.service.ImportReceiptService;

@Service
@Transactional
public class ImportReceiptServiceImpl implements ImportReceiptService {

    @Autowired
    private ImportReceiptRepo importReceiptRepo;

    @Autowired
    private ImprortReceiptItemRepo importReceiptItemRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ImportReceiptDTO createImportReceipt(ImportReceiptDTO importReceiptDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Long supplierId = importReceiptDTO.getSupplier().getSupplierId();
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierId", supplierId));
        ImportReceipt importReceipt = new ImportReceipt();
        importReceipt.setSupplier(supplier);
        importReceipt.setStatus(true);
        importReceipt.setCreatedBy(userId);

        Double totalAmount = importReceiptDTO.getImportReceiptItems().stream().mapToDouble(importReceiptItemDTO -> {
            Product product = productRepo.findById(importReceiptItemDTO.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId",
                            importReceiptItemDTO.getProduct().getProductId()));
            ImportReceiptItem importReceiptItem = new ImportReceiptItem();
            importReceiptItem.setQuantity(importReceiptItemDTO.getQuantity());
            importReceiptItem.setImportReceipt(importReceipt);
            importReceiptItem.setProduct(product);
            importReceiptItem.setDiscount(importReceiptItemDTO.getDiscount());
            importReceiptItem.setPrice(importReceiptItemDTO.getPrice());
            Double totalPrice = importReceiptItem.getPrice() * importReceiptItem.getQuantity()
                    * (100 - importReceiptItem.getDiscount()) / 100;
            importReceiptItem.setTotalPrice(totalPrice);
            // Set and Save new quantity for product
            product.setQuantity(product.getQuantity() + importReceiptItem.getQuantity());
            productRepo.save(product);
            // Add the import receipt item to the import receipt
            importReceipt.getImportReceiptItems().add(importReceiptItem);

            return totalPrice;
        }).sum();

        importReceipt.setTotalAmount(totalAmount);
        importReceipt.setImportDate(LocalDateTime.now());

        importReceiptRepo.save(importReceipt);

        return modelMapper.map(importReceipt, ImportReceiptDTO.class);
    }

    @Override
    public ImportReceiptDTO getImportReceiptById(Long importReceiptId) {
        ImportReceipt importReceipt = importReceiptRepo.findById(importReceiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Import Receipt", "importReceiptId",
                        importReceiptId));

        return modelMapper.map(importReceipt, ImportReceiptDTO.class);
    }

    @Override
    public ImportReceiptResponse getAllImportReceipts(Boolean status, Long userId, Long supplierId, Integer pageNumber,
            Integer pageSize,
            String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Specification<ImportReceipt> specification = ImportReceiptSpecification
                .filter(userId, supplierId, status);

        Page<ImportReceipt> pageImportReceipt = importReceiptRepo.findAll(specification, pageDetails);

        List<ImportReceiptDTO> importReceiptDTOs = pageImportReceipt.getContent().stream()
                .map(importReceipt -> modelMapper.map(importReceipt, ImportReceiptDTO.class))
                .collect(Collectors.toList());

        ImportReceiptResponse importReceiptResponse = new ImportReceiptResponse();
        importReceiptResponse.setContent(importReceiptDTOs);
        importReceiptResponse.setPageNumber(pageImportReceipt.getNumber());
        importReceiptResponse.setPageSize(pageImportReceipt.getSize());
        importReceiptResponse.setTotalElements(pageImportReceipt.getTotalElements());
        importReceiptResponse.setTotalPages(pageImportReceipt.getTotalPages());
        importReceiptResponse.setLastPage(pageImportReceipt.isLast());

        return importReceiptResponse;
    }

    @Override
    public ImportReceiptDTO updateImportReceiptStatus(Long importReceiptId, Boolean status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");
        ImportReceipt importReceipt = importReceiptRepo.findById(importReceiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Import Receipt", "importReceiptId",
                        importReceiptId));
        if (!importReceipt.getCreatedBy().equals(userId) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("You are not authorized to update this import receipt");
        }
        importReceipt.setStatus(status);

        importReceiptRepo.save(importReceipt);

        return modelMapper.map(importReceipt, ImportReceiptDTO.class);
    }

    @Override
    public String deleteImportReceipt(Long importReceiptId) {
        ImportReceipt importReceipt = importReceiptRepo.findById(importReceiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Import Receipt", "importReceiptId",
                        importReceiptId));

        importReceiptRepo.delete(importReceipt);

        return "Import receipt deleted successfully";
    }
}
