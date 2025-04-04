package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.SupplierDTO;
import com.dangphuoctai.BookStore.payloads.response.SupplierResponse;

public interface SupplierService {
    SupplierDTO getSupplierById(Long supplierId);

    SupplierDTO getSupplierBySlug(String slug);

    SupplierResponse getAllSupplier(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    SupplierDTO createSupplier(SupplierDTO supplierDTO);

    SupplierDTO updateSupplier(SupplierDTO supplierDTO);

    String deleteSupplier(Long supplierId);
}
