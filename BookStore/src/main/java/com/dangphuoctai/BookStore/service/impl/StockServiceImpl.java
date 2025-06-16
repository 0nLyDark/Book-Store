package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.payloads.Specification.ProductSpecification;
import com.dangphuoctai.BookStore.payloads.Statistic.ProductImportCost;
import com.dangphuoctai.BookStore.payloads.Stock.ProductStock;
import com.dangphuoctai.BookStore.payloads.Stock.StockDTO;
import com.dangphuoctai.BookStore.payloads.Stock.StockItemDTO;
import com.dangphuoctai.BookStore.payloads.Stock.StockOverview;
import com.dangphuoctai.BookStore.payloads.response.StockResponse;
import com.dangphuoctai.BookStore.repository.ImportReceiptRepo;
import com.dangphuoctai.BookStore.repository.ImprortReceiptItemRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.StockService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class StockServiceImpl implements StockService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ImportReceiptRepo importReceiptRepo;

    @Autowired
    private ImprortReceiptItemRepo importReceiptItemRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseRedisService<String, String, Integer> stockRedisService;

    private static final String qtyWarning = "stock:quantity:warning";

    public int setQuantityWarning(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng phải là số không âm");
        }
        stockRedisService.set(qtyWarning, quantity);

        return quantity;
    }

    public int getQuantityWarning() {
        Integer quantity = stockRedisService.get(qtyWarning);
        if (quantity == null) {
            quantity = 100;
        }

        return quantity;
    }

    @Override
    public StockResponse getStock(String keyword, String isbn, Boolean status, Boolean isWarning, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {
        int quantityWarning = getQuantityWarning();
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Product> productSpecification = ProductSpecification.filterStock(isbn, status,
                isWarning ? quantityWarning : null);
        Page<Product> pageProducts;
        if (keyword == null || keyword.trim().isEmpty()) {
            pageProducts = productRepo.findAll(productSpecification, pageDetails);
        } else {
            pageProducts = productRepo.fullTextSearchWithFilters(keyword,
                    productSpecification,
                    pageDetails);
        }
        List<Long> productIds = pageProducts.getContent().stream()
                .map(Product::getProductId)
                .toList();
        List<ProductImportCost> productImportCosts = importReceiptItemRepo.findProductImportCosts(productIds);
        Map<Long, LinkedList<ProductImportCost>> importCostMap = new HashMap<Long, LinkedList<ProductImportCost>>();
        for (ProductImportCost importCost : productImportCosts) {
            if (!importCostMap.containsKey(importCost.getProductId())) {
                importCostMap.put(importCost.getProductId(), new LinkedList<>());
            }
            importCostMap.get(importCost.getProductId()).add(importCost);
        }
        List<StockDTO> stockDTOs = pageProducts.getContent().stream()
                .map(product -> {
                    StockDTO stockDTO = modelMapper.map(product, StockDTO.class);
                    int qty = stockDTO.getQuantity();
                    LinkedList<ProductImportCost> importCosts = importCostMap.getOrDefault(
                            product.getProductId(),
                            new LinkedList<>());
                    List<StockItemDTO> stockItems = new ArrayList<>();
                    double totalCost = 0.0;
                    while (qty > 0 && !importCosts.isEmpty()) {
                        ProductImportCost importBatch = importCosts.peek();

                        long availableQty = importBatch.getQuantity();
                        double cost = importBatch.getCost();
                        String supplierName = importBatch.getSupplierName();
                        LocalDateTime importDate = importBatch.getImportDate();

                        StockItemDTO stockItemDTO = new StockItemDTO();
                        stockItemDTO.setCost(cost);
                        stockItemDTO.setSupplierName(supplierName);
                        stockItemDTO.setImportDate(importDate);

                        if (availableQty <= qty) {
                            // Dùng hết đợt nhập này
                            stockItemDTO.setQuantity(availableQty);
                            stockItemDTO.setTotalCost(availableQty * cost);
                            totalCost += stockItemDTO.getTotalCost();
                            qty -= availableQty;
                            importCosts.poll(); // remove khỏi queue
                        } else {
                            // Chỉ dùng một phần của đợt nhập này
                            stockItemDTO.setQuantity(qty);
                            stockItemDTO.setTotalCost(qty * cost);
                            totalCost += stockItemDTO.getTotalCost();
                            importBatch.setQuantity((int) (availableQty - qty));
                            qty = 0;
                        }
                        stockItems.add(stockItemDTO);
                    }
                    log.info("Product: " + product.getProductName() + ", Stock Items: " + stockItems);
                    stockDTO.setStockItems(stockItems);
                    stockDTO.setTotalCost(totalCost);
                    return stockDTO;
                })
                .collect(Collectors.toList());

        StockResponse stockResponse = new StockResponse();
        stockResponse.setContent(stockDTOs);
        stockResponse.setPageNumber(pageProducts.getNumber());
        stockResponse.setPageSize(pageProducts.getSize());
        stockResponse.setTotalElements(pageProducts.getTotalElements());
        stockResponse.setTotalPages(pageProducts.getTotalPages());
        stockResponse.setLastPage(pageProducts.isLast());

        return stockResponse;
    }

    @Override
    public StockOverview getStockOverview() {
        int qtyWarning = getQuantityWarning();
        // Get Database
        Long totalProductWarning = productRepo.countProductWarning(qtyWarning, true);
        List<ProductStock> productStocks = productRepo.getProductStocks();
        List<Long> productIds = productStocks.stream().map(pro -> pro.getProductId()).collect(Collectors.toList());
        List<ProductImportCost> productImportCosts = importReceiptItemRepo.findProductImportCosts(productIds);
        // Tính Total ...
        long totalProduct = productIds.size();
        long totalStock = 0L;
        double totalCost = 0.0;
        Map<Long, LinkedList<ProductImportCost>> importCostMap = new HashMap<Long, LinkedList<ProductImportCost>>();
        for (ProductImportCost importCost : productImportCosts) {
            if (!importCostMap.containsKey(importCost.getProductId())) {
                importCostMap.put(importCost.getProductId(), new LinkedList<>());
            }
            importCostMap.get(importCost.getProductId()).add(importCost);
        }
        for (ProductStock proStock : productStocks) {
            Long productId = proStock.getProductId();
            int quantity = proStock.getStockQuantity();
            LinkedList<ProductImportCost> importQueue = importCostMap.get(productId);
            if (importQueue == null || importQueue.isEmpty())
                continue;
            while (quantity > 0 && !importQueue.isEmpty()) {
                ProductImportCost importBatch = importQueue.peek();

                int availableQty = importBatch.getQuantity();
                double cost = importBatch.getCost();
                if (availableQty <= quantity) {
                    // Dùng hết đợt nhập này
                    totalCost += availableQty * cost;
                    totalStock += availableQty;
                    quantity -= availableQty;
                    importQueue.poll(); // remove khỏi queue
                } else {
                    // Chỉ dùng một phần của đợt nhập này
                    totalCost += quantity * cost;
                    totalStock += quantity;
                    importBatch.setQuantity((int) (availableQty - quantity));
                    quantity = 0;
                }

            }
        }

        StockOverview stockOverview = new StockOverview();
        stockOverview.setTotalProductWarning(totalProductWarning);
        stockOverview.setTotalProduct(totalProduct);
        stockOverview.setTotalCost(totalCost);
        stockOverview.setTotalStock(totalStock);

        return stockOverview;
    }

}
