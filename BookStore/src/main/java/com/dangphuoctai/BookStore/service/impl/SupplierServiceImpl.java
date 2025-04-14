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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Supplier;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.SupplierDTO;
import com.dangphuoctai.BookStore.payloads.response.SupplierResponse;
import com.dangphuoctai.BookStore.repository.SupplierRepo;
import com.dangphuoctai.BookStore.service.SupplierService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SupplierDTO getSupplierById(Long supplierId) {
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierId", supplierId));

        return modelMapper.map(supplier, SupplierDTO.class);
    }

    @Override
    public List<SupplierDTO> getManySupplierById(List<Long> supplierIds) {
        List<Supplier> suppliers = supplierRepo.findAllById(supplierIds);
        if (suppliers.size() != supplierIds.size()) {
            throw new ResourceNotFoundException("Supplier", "supplierIds", supplierIds);
        }
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class)).collect(Collectors.toList());

        return supplierDTOs;
    }

    @Override
    public SupplierDTO getSupplierBySlug(String slug) {
        Supplier supplier = supplierRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "slug", slug));

        return modelMapper.map(supplier, SupplierDTO.class);
    }

    @Override
    public SupplierResponse getAllSupplier(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Supplier> pageSuppliers = supplierRepo.findAll(pageDetails);
        List<SupplierDTO> supplierDTOs = pageSuppliers.getContent().stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                .collect(Collectors.toList());

        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setContent(supplierDTOs);
        supplierResponse.setPageNumber(pageSuppliers.getNumber());
        supplierResponse.setPageSize(pageSuppliers.getSize());
        supplierResponse.setTotalElements(pageSuppliers.getTotalElements());
        supplierResponse.setTotalPages(pageSuppliers.getTotalPages());
        supplierResponse.setLastPage(pageSuppliers.isLast());

        return supplierResponse;
    }

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierDTO.getSupplierName());
        supplier.setSlug(CreateSlug.toSlug(supplierDTO.getSupplierName()));
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setMobieNumber(supplierDTO.getMobieNumber());
        supplier.setAddress(supplierDTO.getAddress());

        supplier.setCreatedBy(userId);
        supplier.setUpdatedBy(userId);
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return modelMapper.map(supplier, SupplierDTO.class);
    }

    @Override
    public SupplierDTO updateSupplier(SupplierDTO supplierDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Supplier supplier = supplierRepo.findById(supplierDTO.getSupplierId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Supplier", "supplierId", supplierDTO.getSupplierId()));
        supplier.setSupplierName(supplierDTO.getSupplierName());
        supplier.setSlug(CreateSlug.toSlug(supplierDTO.getSupplierName()));
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setMobieNumber(supplierDTO.getMobieNumber());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setStatus(supplierDTO.getStatus());

        supplier.setUpdatedBy(userId);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return modelMapper.map(supplier, SupplierDTO.class);
    }

    @Override
    public String deleteSupplier(Long supplierId) {
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierId", supplierId));
        supplierRepo.delete(supplier);

        return "Supplier with ID: " + supplierId + " deleted successfully";
    }

}
