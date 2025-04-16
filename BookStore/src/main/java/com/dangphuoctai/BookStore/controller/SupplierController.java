package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.dto.SupplierDTO;
import com.dangphuoctai.BookStore.payloads.response.SupplierResponse;
import com.dangphuoctai.BookStore.service.SupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping("/public/suppliers/{supplierId}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long supplierId) {
        SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId);

        return new ResponseEntity<SupplierDTO>(supplierDTO, HttpStatus.OK);
    }

    @GetMapping("/public/suppliers/ids")
    public ResponseEntity<List<SupplierDTO>> getManySupplierByIds(@RequestParam(value = "id") List<Long> supplierIds) {
        List<SupplierDTO> supplierDTOs = supplierService.getManySupplierById(supplierIds);

        return new ResponseEntity<List<SupplierDTO>>(supplierDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/suppliers/slug/{slug}")
    public ResponseEntity<SupplierDTO> getSupplierBySlug(@PathVariable String slug) {
        SupplierDTO supplierDTO = supplierService.getSupplierBySlug(slug);

        return new ResponseEntity<SupplierDTO>(supplierDTO, HttpStatus.OK);
    }

    @GetMapping("/public/suppliers")
    public ResponseEntity<SupplierResponse> getAllSupplier(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_SUPPLIERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        SupplierResponse supplierResponse = supplierService.getAllSupplier(status,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "supplierId" : sortBy,
                sortOrder);

        return new ResponseEntity<SupplierResponse>(supplierResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/suppliers")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplier) {
        SupplierDTO supplierDTO = supplierService.createSupplier(supplier);

        return new ResponseEntity<SupplierDTO>(supplierDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/suppliers")
    public ResponseEntity<SupplierDTO> updateSupplier(@RequestBody SupplierDTO supplier) {
        SupplierDTO supplierDTO = supplierService.updateSupplier(supplier);

        return new ResponseEntity<SupplierDTO>(supplierDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/suppliers/{supplierId}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long supplierId) {
        String result = supplierService.deleteSupplier(supplierId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
