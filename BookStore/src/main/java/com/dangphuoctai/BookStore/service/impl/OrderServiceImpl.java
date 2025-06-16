package com.dangphuoctai.BookStore.service.impl;

import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.Address;
import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.entity.Payment;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.entity.PromotionSnapshot;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.enums.OrderType;
import com.dangphuoctai.BookStore.enums.PaymentMethod;
import com.dangphuoctai.BookStore.enums.PromotionType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderItemDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderStatusDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.PaymentDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.payloads.response.OrderResponse;
import com.dangphuoctai.BookStore.repository.AddressRepo;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
import com.dangphuoctai.BookStore.repository.OrderRepo;
import com.dangphuoctai.BookStore.repository.PaymentRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.PromotionRepo;
import com.dangphuoctai.BookStore.repository.PromotionSnapshotRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.EmailService;
import com.dangphuoctai.BookStore.service.GHNService;
import com.dangphuoctai.BookStore.service.OrderService;
import com.dangphuoctai.BookStore.service.PaymentService;
import com.dangphuoctai.BookStore.utils.Email;
import com.dangphuoctai.BookStore.utils.HashUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private PromotionRepo promotionRepo;

    @Autowired
    private PromotionSnapshotRepo promotionSnapshotRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GHNService gHNService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BaseRedisService<String, Long, Integer> cartRedisService;

    @Autowired
    private BaseRedisService<String, String, String> otpRedisService;

    @Autowired
    private BaseRedisService<String, String, OrderDTO> orderRedisService;

    @Autowired
    private BaseRedisService<String, String, List<OrderItemDTO>> orderRestoreRedisService;

    private static final String OTP_ORDER_CACHE_KEY = "otp:order:";
    private static final String ORDER_CACHE_KEY = "order:code:";
    private static final String ORDER_RESTORE_CACHE_KEY = "order_restore:code:";

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        if (userId != order.getUserId() && !roles.contains("STAFF") && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn hàng này.");
        }

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO getOrderByOrderCode(String orderCode) {
        Order order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderCode", orderCode));

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllOrderByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long Id = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        if (Id != userId && !roles.contains("STAFF") && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("You do not have permission to access this order.");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepo.findAllByUserId(userId, pageDetails);
        List<OrderDTO> orderDTOs = pageOrders.getContent().stream().map(o -> modelMapper.map(o, OrderDTO.class))
                .collect(Collectors.toList());

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    public OrderResponse getAllOrder(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepo.findAll(pageDetails);
        List<OrderDTO> orderDTOs = pageOrders.getContent().stream().map(o -> modelMapper.map(o, OrderDTO.class))
                .collect(Collectors.toList());

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    public OrderDTO changeOrderStatus(OrderStatusDTO orderStatus) {
        Order order = orderRepo.findById(orderStatus.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderStatus.getOrderId()));
        OrderStatus status = orderStatus.getOrderStatus();
        if (status.equals(order.getOrderStatus())) {
            return modelMapper.map(order, OrderDTO.class);
        }
        if (OrderStatus.PENDING.equals(status) || OrderStatus.CANCELLED.equals(status)) {
            throw new APIException("Không thể chuyển đơn hàng sang trạng thái chờ xử lý hoặc đã hủy.");
        }
        if (OrderStatus.FAILED.equals(order.getOrderStatus()) || OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new APIException("Không thể chuyển trạng thái đơn hàng khi đơn đã thất bại hoặc đã hủy.");
        }
        if (OrderStatus.COMPLETED.equals(status)) {
            if (!OrderStatus.PAID.equals(order.getOrderStatus())
                    && !OrderStatus.SHIPPED.equals(order.getOrderStatus())) {
                throw new APIException("Đơn hàng chưa được xác nhận và không thể chuyển sang trạng thái hoàn thành.");
            }
        }
        if (OrderStatus.SHIPPED.equals(status) && !OrderStatus.PAID.equals(order.getOrderStatus())) {
            throw new APIException("Chỉ có thể Ship đơn hàng ở trạng thái đã xác nhận.");
        }
        if (OrderStatus.PAID.equals(status) && !OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new APIException("Chỉ có thể xác nhận đơn hàng ở trạng thái chờ xử lý.");
        }
        if (OrderStatus.FAILED.equals(status)) {
            if (OrderStatus.COMPLETED.equals(order.getOrderStatus())) {
                throw new APIException("Đơn hàng đã hoàn thành và không thể chuyển sang trạng thái thất bại.");
            }
            if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
                throw new APIException("Đơn hàng đã hủy và không thể chuyển sang trạng thái thất bại.");
            }
            order.getOrderItems().forEach((orderItem) -> {
                Long productId = orderItem.getProduct().getProductId();
                Product product = productRepo.findByIdForUpdate(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
                int newQuantity = orderItem.getQuantity() + product.getQuantity();
                product.setQuantity(newQuantity);
                productRepo.save(product);
            });
        }
        order.setOrderStatus(status);
        orderRepo.save(order);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public Object createCustomerOrder(OrderDTO orderDTO, List<ProductQuantity> productQuantities, String vnp_IpAddr) {
        Order order = convertToOrder(orderDTO);
        order.setOrderType(OrderType.ONLINE);
        order.setOrderStatus(OrderStatus.PENDING);
        // Set OrderItem
        double total = productQuantities.stream().mapToDouble(p -> {
            Product product = productRepo.findByIdForUpdate(p.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", p.getProductId()));
            if (!product.getStatus()) {
                throw new APIException("Sản phẩm không còn tồn tại");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(p.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setDiscount(product.getDiscount());
            int newQuantity = product.getQuantity() - p.getQuantity();
            if (newQuantity < 0) {
                throw new APIException("Số lượng tồn kho sản phẩm :" + product.getProductName() + " không đủ");
            }
            product.setQuantity(newQuantity);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            return product.getPrice() * p.getQuantity() * (100 - product.getDiscount()) / 100;
        }).sum();
        order.setSubTotal(total);
        double totalAmount = total;
        // Set Price ship
        double priceShip = gHNService.calculateShippingFee(total < 20000000 ? 2 : 5,
                orderDTO.getAddress().getDistrictCode(), orderDTO.getAddress().getWardCode(),
                Long.valueOf((long) total),
                order.getOrderItems());
        order.setPriceShip(priceShip);
        // Set Promotion
        if (orderDTO.getCoupon() != null && orderDTO.getCoupon().getPromotionCode() != null) {
            String code = orderDTO.getCoupon().getPromotionCode();
            Promotion voucher = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(voucher, PromotionType.VOUCHER, total);
            PromotionSnapshot voucherSnapshot = convertToPromotionSnapshot(voucher);
            order.setCoupon(voucherSnapshot);
            totalAmount = !voucher.getValueType() ? totalAmount - voucher.getValue()
                    : totalAmount * (100 - voucher.getValue()) / 100;
            totalAmount = Math.max(totalAmount, 0);
        }
        if (orderDTO.getFreeship() != null && orderDTO.getFreeship().getPromotionCode() != null) {
            String code = orderDTO.getFreeship().getPromotionCode();
            Promotion freeShip = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(freeShip, PromotionType.FREESHIP, total);
            PromotionSnapshot freeShipSnapshot = convertToPromotionSnapshot(freeShip);
            order.setFreeship(freeShipSnapshot);
            priceShip = !freeShip.getValueType() ? priceShip - freeShip.getValue()
                    : priceShip * (100 - freeShip.getValue()) / 100;
            priceShip = Math.max(priceShip, 0);
        }
        // Set Total
        order.setTotalAmount(totalAmount + priceShip);
        // Set address
        Address address = convertToAddress(orderDTO.getAddress());
        order.setAddress(address);
        // Set time
        order.setOrderDateTime(LocalDateTime.now());
        // Set order code
        String orderCode = orderRedisService.generateOrderCodeWithRedis();
        order.setOrderCode(orderCode);
        // Save order to from database
        orderRepo.save(order);
        OrderDTO orderRes = modelMapper.map(order, OrderDTO.class);
        String bankCode = orderRes.getPayment().getBankCode();
        PaymentMethod paymentMethod = orderRes.getPayment().getPaymentMethod();
        if (PaymentMethod.COD.equals(paymentMethod)) {
            SendVerifyOrderEmail(orderCode);
        } else if (PaymentMethod.VNPAY.equals(paymentMethod)) {
            return paymentService.createPaymentVNPAY(vnp_IpAddr, bankCode, orderCode, (long) order.getTotalAmount());
        }

        return orderRes;
    }

    @Override
    public Object createOrder(OrderDTO orderDTO, List<Long> productIds, String vnp_IpAddr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Order order = convertToOrder(orderDTO);
        order.setUser(user);
        order.setOrderType(OrderType.ONLINE);
        // Set payment method
        PaymentMethod paymentMethod = order.getPayment().getPaymentMethod();
        if (PaymentMethod.COD.equals(paymentMethod)) {
            order.setOrderStatus(OrderStatus.PAID);
        } else {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        // Get cart
        String key = "cart:user:" + userId;
        Map<Long, Integer> cartItems = cartRedisService.getField(key).entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(((Number) entry.getKey()).longValue(), entry.getValue()))
                .filter(entry -> productIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (cartItems.size() != productIds.size()) {
            throw new APIException("Giỏ hàng không tồn tại sản phẩm trong danh sách sản phẩm đặt hàng.");
        }
        // Set Cart
        cartRedisService.delete(key, productIds);
        // Set OrderItem
        double total = cartItems.entrySet().stream()
                .mapToDouble(entry -> {
                    Long productIdKey = ((Number) entry.getKey()).longValue();
                    Integer quantityValue = entry.getValue();
                    Product product = productRepo.findById(productIdKey)
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productIdKey));
                    if (!product.getStatus()) {
                        throw new APIException("Sản phẩm không còn tồn tại");
                    }
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(quantityValue);
                    orderItem.setPrice(product.getPrice());
                    orderItem.setDiscount(product.getDiscount());
                    int newQuantity = product.getQuantity() - quantityValue;
                    if (newQuantity < 0) {
                        throw new APIException("Số lượng tồn kho sản phẩm :" + product.getProductName() + " không đủ");
                    }
                    product.setQuantity(newQuantity);
                    orderItem.setOrder(order);
                    order.getOrderItems().add(orderItem);
                    return orderItem.getPrice() * orderItem.getQuantity() * (100 - orderItem.getDiscount()) / 100;
                }).sum();
        // Set sub Total
        order.setSubTotal(total);
        double totalAmount = total;
        // Set Price ship
        double priceShip = gHNService.calculateShippingFee(total < 20000000 ? 2 : 5,
                orderDTO.getAddress().getDistrictCode(), orderDTO.getAddress().getWardCode(),
                Long.valueOf((long) total),
                order.getOrderItems());
        order.setPriceShip(priceShip);
        // Set Promotion
        if (orderDTO.getCoupon() != null && orderDTO.getCoupon().getPromotionCode() != null) {
            String code = orderDTO.getCoupon().getPromotionCode();
            Promotion voucher = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(voucher, PromotionType.VOUCHER, total);
            PromotionSnapshot voucherSnapshot = convertToPromotionSnapshot(voucher);
            order.setCoupon(voucherSnapshot);
            totalAmount = !voucher.getValueType() ? totalAmount - voucher.getValue()
                    : totalAmount * (100 - voucher.getValue()) / 100;
            totalAmount = Math.max(totalAmount, 0);
        }
        if (orderDTO.getFreeship() != null && orderDTO.getFreeship().getPromotionCode() != null) {
            String code = orderDTO.getFreeship().getPromotionCode();
            Promotion freeShip = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(freeShip, PromotionType.FREESHIP, total);
            PromotionSnapshot freeShipSnapshot = convertToPromotionSnapshot(freeShip);
            order.setFreeship(freeShipSnapshot);
            priceShip = !freeShip.getValueType() ? priceShip - freeShip.getValue()
                    : priceShip * (100 - freeShip.getValue()) / 100;
            priceShip = Math.max(priceShip, 0);
        }
        // Set Total
        order.setTotalAmount(totalAmount + priceShip);
        // Set address
        Address address = convertToAddress(orderDTO.getAddress());
        order.setAddress(address);
        // Set time
        order.setOrderDateTime(LocalDateTime.now());
        // Set order code
        String orderCode = orderRedisService.generateOrderCodeWithRedis();
        order.setOrderCode(orderCode);
        // Save order to from database
        orderRepo.save(order);
        String bankCode = order.getPayment().getBankCode();
        if (PaymentMethod.VNPAY.equals(paymentMethod)) {
            return paymentService.createPaymentVNPAY(vnp_IpAddr, bankCode, orderCode, (long) order.getTotalAmount());
        }

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public Object createOrderOffline(OrderDTO orderDTO, List<ProductQuantity> productQuantities, String vnp_IpAddr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryName(orderDTO.getDeliveryName());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());
        order.setEmail(orderDTO.getEmail());
        order.setOrderType(OrderType.OFFLINE);
        // Set payment method
        Payment payment = new Payment();
        payment.setPaymentMethod(orderDTO.getPayment().getPaymentMethod());
        payment.setBankCode(orderDTO.getPayment().getBankCode());
        order.setPayment(payment);
        if (payment.getPaymentMethod().equals(PaymentMethod.COD)) {
            order.setOrderStatus(OrderStatus.COMPLETED);
        } else {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        // Set OrderItem
        double total = productQuantities.stream().mapToDouble(p -> {
            Product product = productRepo.findByIdForUpdate(p.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", p.getProductId()));
            if (!product.getStatus()) {
                throw new APIException("Sản phẩm " + product.getProductName() + " không còn hoạt động");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(p.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setDiscount(product.getDiscount());
            int newQuantity = product.getQuantity() - p.getQuantity();
            if (newQuantity < 0) {
                throw new APIException("Số lượng tồn kho sản phẩm :" + product.getProductName() + " không đủ");
            }
            product.setQuantity(newQuantity);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            return product.getPrice() * p.getQuantity() * (100 - product.getDiscount()) / 100;
        }).sum();
        order.setSubTotal(total);
        double totalAmount = total;
        // Set Promotion
        if (orderDTO.getCoupon() != null && orderDTO.getCoupon().getPromotionCode() != null) {
            String code = orderDTO.getCoupon().getPromotionCode();
            Promotion voucher = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(voucher, PromotionType.VOUCHER, total);
            PromotionSnapshot voucherSnapshot = convertToPromotionSnapshot(voucher);
            order.setCoupon(voucherSnapshot);
            totalAmount = !voucher.getValueType() ? totalAmount - voucher.getValue()
                    : totalAmount * (100 - voucher.getValue()) / 100;
            totalAmount = Math.max(totalAmount, 0);
        }
        // Set Price ship
        order.setPriceShip(0);
        // Set Total
        order.setTotalAmount(totalAmount);
        // Set time
        order.setOrderDateTime(LocalDateTime.now());
        // Set order code
        String orderCode = orderRedisService.generateOrderCodeWithRedis();
        order.setOrderCode(orderCode);
        // Save order
        orderRepo.save(order);

        OrderDTO orderResult = modelMapper.map(order, OrderDTO.class);
        orderResult.setUserId(userId);
        String bankCode = order.getPayment().getBankCode();
        if (PaymentMethod.VNPAY.equals(orderResult.getPayment().getPaymentMethod())) {
            return paymentService.createPaymentVNPAY(vnp_IpAddr, bankCode, orderCode, (long) order.getTotalAmount());
        }
        return orderResult;
    }

    @Override
    public String SendVerifyOrderEmail(String orderCode) {
        Order order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new APIException("Đơn hàng không tồn tại"));
        if (!order.getPayment().getPaymentMethod().equals(PaymentMethod.COD)) {
            throw new APIException("Đơn hàng không hợp lệ. Đơn hàng phải sử dụng phương thức thanh toán COD.");
        }
        String otp = generateOTPOrder(orderCode);
        String htmlContent = Email.getFormOTPVerifyOrderSendEmail(otp, orderCode, order.getDeliveryName());
        EmailDetails emailDetails = new EmailDetails(order.getEmail(), htmlContent, "BookStore - Xác thực đơn hàng",
                null);
        emailService.sendMailWithAttachment(emailDetails);

        return "Gửi mã xác thực thành công đến email: " + order.getEmail();
    }

    @Override
    public String generateOTPOrder(String orderCode) {
        String key = OTP_ORDER_CACHE_KEY + orderCode;

        // Generate a 6-digit OTP
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000;
        String strOTP = String.valueOf(code);
        // Save OTP to redis
        otpRedisService.set(key, strOTP);
        otpRedisService.setTimeToLive(key, 5, TimeUnit.MINUTES);

        return strOTP;
    }

    @Override
    public Boolean verityOTPEmail(OtpDTO otpDTO) {
        String orderCode = otpDTO.getOrderCode();
        String key = OTP_ORDER_CACHE_KEY + orderCode;
        String otpCode = otpRedisService.get(key);
        Order order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new APIException("Đơn hàng không tồn tại"));
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)
                || order.getPayment().getPaymentMethod() != PaymentMethod.COD) {
            throw new APIException("Đơn hàng không hợp lệ");
        }
        if (otpCode == null || !otpCode.equals(otpDTO.getCode())) {
            return false;
        }
        order.setOrderStatus(OrderStatus.PAID);

        // Save order to database
        orderRepo.save(order);
        // Delete OTP and Order from redis
        otpRedisService.delete(key);
        // Send email info order
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        String htmlContent = Email.generateOrderEmailContent(orderDTO);
        EmailDetails emailDetails = new EmailDetails(order.getEmail(), htmlContent,
                "BookStore - Thông báo đặt hàng thành công",
                null);
        emailService.sendMailWithAttachment(emailDetails);

        return true;
    }

    @Override
    public Boolean verifyVNPay(String txnRef, String transactionDate, String bankCode, String BankTranNo) {
        Order order = orderRepo.findByOrderCode(txnRef)
                .orElseThrow(() -> new APIException("Đơn hàng không tồn tại"));
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new APIException("Đơn hàng không ở trạng thái chờ xử lý");
        }
        long totalAmount = (long) order.getTotalAmount();
        boolean result = paymentService.checkTransaction(totalAmount, txnRef, transactionDate);
        if (!result) {
            throw new APIException("Lỗi giao dịch, xác thực thất bại");
        }
        if (order.getOrderType().equals(OrderType.ONLINE)) {
            order.setOrderStatus(OrderStatus.PAID);
        } else {
            order.setOrderStatus(OrderStatus.COMPLETED);
        }
        order.getPayment().setBankCode(bankCode);
        order.getPayment().setPaymentCode(BankTranNo);
        orderRepo.save(order);
        // Send email info order
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        String htmlContent = Email.generateOrderEmailContent(orderDTO);
        EmailDetails emailDetails = new EmailDetails(order.getEmail(), htmlContent,
                "BookStore - Thông báo đặt hàng thành công",
                null);
        emailService.sendMailWithAttachment(emailDetails);
        return true;
    }

    // Check Promotion
    private void checkPromotion(Promotion promotion, PromotionType type, double subTotal) {
        boolean checkType = promotion.getPromotionType().equals(type);
        LocalDateTime now = LocalDateTime.now();
        boolean checkDate = promotion.getStartDate().isBefore(now) && promotion.getEndDate().isAfter(now);
        boolean checkValue = subTotal >= promotion.getValueApply();
        if (!checkType || !checkDate || !checkValue || !promotion.getStatus()) {
            throw new APIException(
                    "Mã giảm giá " + (type.equals(PromotionType.VOUCHER) ? "đơn hàng" : "vận chuyển")
                            + " không hợp lệ" + checkType + "" + checkDate + "" + checkValue + ""
                            + promotion.getStatus());
        }
    }

    // Hàm chuyển đổi OrderDTO -> Order
    private Order convertToOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }
        Order order = new Order();

        order.setEmail(orderDTO.getEmail());
        order.setDeliveryName(orderDTO.getDeliveryName());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());

        // Map AddressDTO -> Address (cần tự tạo hàm chuyển đổi tương tự)
        if (orderDTO.getAddress() != null) {
            order.setAddress(convertToAddress(orderDTO.getAddress()));
        }
        // Map PaymentDTO -> Payment
        if (orderDTO.getPayment() != null) {
            order.setPayment(convertToPayment(orderDTO.getPayment()));
        }

        return order;
    }

    // Hàm chuyển đổi AddressDTO -> Address
    private Address convertToAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        String country = dto.getCountry();
        String district = dto.getDistrict();
        String city = dto.getCity();
        String ward = dto.getWard();
        String buildingName = dto.getBuildingName();
        Address address = addressRepo
                .findByCountryAndDistrictAndCityAndWardAndBuildingName(
                        country, district,
                        city, ward, buildingName);
        if (address == null) {
            address = new Address(country, district, city, ward, buildingName);
            address = addressRepo.save(address);
        }
        return address;
    }

    // Hàm chuyển đổi PaymentDTO -> Payment
    private Payment convertToPayment(PaymentDTO dto) {
        if (dto == null)
            return null;
        Payment payment = new Payment();
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setBankCode(dto.getBankCode());
        payment.setPaymentCode(dto.getPaymentCode());
        return payment;
    }

    // Hàm chuyển đổi PromotionDTO -> PromotionSnapshot
    private PromotionSnapshot convertToPromotionSnapshot(Promotion dto) {
        if (dto == null)
            return null;
        String hash = HashUtil.generatePromotionHash(dto);
        PromotionSnapshot promotionSnapshot = promotionSnapshotRepo.findByHash(hash);
        if (promotionSnapshot == null) {
            promotionSnapshot = modelMapper.map(dto, PromotionSnapshot.class);
            promotionSnapshot.setPromotionId(null);
            promotionSnapshot.setHash(hash);
            promotionSnapshotRepo.save(promotionSnapshot);
        }
        return promotionSnapshot;
    }

}
