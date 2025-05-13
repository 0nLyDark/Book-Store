package com.dangphuoctai.BookStore.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
import com.dangphuoctai.BookStore.entity.Cart;
import com.dangphuoctai.BookStore.entity.CartItem;
import com.dangphuoctai.BookStore.entity.OTP;
import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.entity.Payment;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.entity.PromotionSnapshot;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.OTPType;
import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.enums.OrderType;
import com.dangphuoctai.BookStore.enums.PaymentMethod;
import com.dangphuoctai.BookStore.enums.PromotionType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.payloads.response.OrderResponse;
import com.dangphuoctai.BookStore.repository.AddressRepo;
import com.dangphuoctai.BookStore.repository.CartRepo;
import com.dangphuoctai.BookStore.repository.OTPRepo;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
import com.dangphuoctai.BookStore.repository.OrderRepo;
import com.dangphuoctai.BookStore.repository.PaymentRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.PromotionRepo;
import com.dangphuoctai.BookStore.repository.PromotionSnapshotRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.service.EmailService;
import com.dangphuoctai.BookStore.service.GHNService;
import com.dangphuoctai.BookStore.service.OrderService;
import com.dangphuoctai.BookStore.utils.Email;
import com.dangphuoctai.BookStore.utils.HashUtil;

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
    private CartRepo cartRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private OTPRepo otpRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GHNService gHNService;

    @Autowired
    private EmailService emailService;

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        if (userId != order.getUserId() && !"STAFF".contains(roles)) {
            throw new AccessDeniedException("You do not have permission to access this order.");
        }

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllOrderByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long Id = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        if (Id != userId && !"STAFF".contains(roles)) {
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

    @Transactional
    @Override
    public OrderDTO createCustomerOrder(OrderDTO orderDTO, List<ProductQuantity> productQuantities) {
        Order order = new Order();
        order.setDeliveryName(orderDTO.getDeliveryName());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());
        order.setEmail(orderDTO.getEmail());
        order.setOrderType(OrderType.ONLINE);
        // Set payment method
        Payment payment = new Payment();
        payment.setPaymentMethod(orderDTO.getPayment().getPaymentMethod());
        paymentRepo.save(payment);
        order.setPayment(payment);

        order.setOrderStatus(OrderStatus.PENDING);
        // Set OrderItem
        double total = productQuantities.stream().mapToDouble(p -> {
            Product product = productRepo.findById(p.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", p.getProductId()));
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
        // Set Promotion
        if (orderDTO.getCoupon() != null && orderDTO.getCoupon().getPromotionCode() != null) {
            String code = orderDTO.getCoupon().getPromotionCode();
            Promotion voucher = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(voucher, PromotionType.VOUCHER, total);
            String hash = HashUtil.generatePromotionHash(voucher);
            PromotionSnapshot voucherSnapshot = promotionSnapshotRepo.findByHash(hash);
            if (voucherSnapshot == null) {
                voucherSnapshot = modelMapper.map(voucher, PromotionSnapshot.class);
                voucherSnapshot.setPromotionId(null);
                voucherSnapshot.setHash(hash);
                promotionSnapshotRepo.save(voucherSnapshot);
            }
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
            String hash = HashUtil.generatePromotionHash(freeShip);
            PromotionSnapshot freeShipSnapshot = promotionSnapshotRepo.findByHash(hash);
            if (freeShipSnapshot == null) {
                freeShipSnapshot = modelMapper.map(freeShip, PromotionSnapshot.class);
                freeShipSnapshot.setPromotionId(null);
                freeShipSnapshot.setHash(hash);
                promotionSnapshotRepo.save(freeShipSnapshot);
            }
            order.setFreeship(freeShipSnapshot);
            priceShip = !freeShip.getValueType() ? priceShip - freeShip.getValue()
                    : priceShip * (100 - freeShip.getValue()) / 100;
            priceShip = Math.max(priceShip, 0);
        }
        // Set Total
        order.setPriceShip(priceShip);
        order.setTotalAmount(totalAmount + priceShip);
        // Set address
        String country = orderDTO.getAddress().getCountry();
        String district = orderDTO.getAddress().getDistrict();
        String city = orderDTO.getAddress().getCity();
        String pincode = orderDTO.getAddress().getPincode();
        String ward = orderDTO.getAddress().getWard();
        String buildingName = orderDTO.getAddress().getBuildingName();
        Address address = addressRepo
                .findByCountryAndDistrictAndCityAndPincodeAndWardAndBuildingName(
                        country, district,
                        city, pincode, ward, buildingName);
        if (address == null) {
            address = new Address(country, district, city, pincode, ward, buildingName);
            address = addressRepo.save(address);
        }
        order.setAddress(address);
        // Set time
        order.setOrderDateTime(LocalDateTime.now());
        // Save order
        orderRepo.save(order);
        SendVerifyOrderEmail(order.getOrderId());

        return modelMapper.map(order, OrderDTO.class);
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO, List<Long> productId) {
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
        order.setOrderType(OrderType.ONLINE);
        // Set payment method
        Payment payment = new Payment();
        payment.setPaymentMethod(orderDTO.getPayment().getPaymentMethod());
        paymentRepo.save(payment);
        order.setPayment(payment);
        if (payment.getPaymentMethod().equals(PaymentMethod.COD)) {
            order.setOrderStatus(OrderStatus.PAID);
        } else {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        // Get cart
        Cart cart = user.getCart();
        List<CartItem> cartItems = cart.getCartItems().stream()
                .filter(cI -> productId.contains(cI.getProduct().getProductId()))
                .collect(Collectors.toList());
        if (cartItems.size() != productId.size()) {
            throw new APIException("Sản phẩm không có trong giỏ hàng");
        }
        // Set OrderItem
        double total = cartItems.stream().mapToDouble(ci -> {
            Product product = ci.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setDiscount(product.getDiscount());
            int newQuantity = product.getQuantity() - ci.getQuantity();
            if (newQuantity < 0) {
                throw new APIException("Số lượng tồn kho sản phẩm :" + product.getProductName() + " không đủ");
            }
            product.setQuantity(newQuantity);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            return orderItem.getPrice() * orderItem.getQuantity() * (100 - orderItem.getDiscount()) / 100;
        }).sum();
        cart.getCartItems().removeAll(cartItems);
        order.setSubTotal(total);
        double totalAmount = total;
        // Set Price ship
        double priceShip = gHNService.calculateShippingFee(total < 20000000 ? 2 : 5,
                orderDTO.getAddress().getDistrictCode(), orderDTO.getAddress().getWardCode(),
                Long.valueOf((long) total),
                order.getOrderItems()); // Set Promotion
        if (orderDTO.getCoupon() != null && orderDTO.getCoupon().getPromotionCode() != null) {
            String code = orderDTO.getCoupon().getPromotionCode();
            Promotion voucher = promotionRepo.findByPromotionCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", code));
            checkPromotion(voucher, PromotionType.VOUCHER, total);
            String hash = HashUtil.generatePromotionHash(voucher);
            PromotionSnapshot voucherSnapshot = promotionSnapshotRepo.findByHash(hash);
            if (voucherSnapshot == null) {
                voucherSnapshot = modelMapper.map(voucher, PromotionSnapshot.class);
                voucherSnapshot.setPromotionId(null);
                voucherSnapshot.setHash(hash);
                promotionSnapshotRepo.save(voucherSnapshot);
            }
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
            String hash = HashUtil.generatePromotionHash(freeShip);
            PromotionSnapshot freeShipSnapshot = promotionSnapshotRepo.findByHash(hash);
            if (freeShipSnapshot == null) {
                freeShipSnapshot = modelMapper.map(freeShip, PromotionSnapshot.class);
                freeShipSnapshot.setPromotionId(null);
                freeShipSnapshot.setHash(hash);
                promotionSnapshotRepo.save(freeShipSnapshot);
            }
            order.setFreeship(freeShipSnapshot);
            priceShip = !freeShip.getValueType() ? priceShip - freeShip.getValue()
                    : priceShip * (100 - freeShip.getValue()) / 100;
            priceShip = Math.max(priceShip, 0);
        }
        // Set Total
        order.setPriceShip(priceShip);
        order.setTotalAmount(totalAmount + priceShip);
        // Set address
        String country = orderDTO.getAddress().getCountry();
        String district = orderDTO.getAddress().getDistrict();
        String city = orderDTO.getAddress().getCity();
        String pincode = orderDTO.getAddress().getPincode();
        String ward = orderDTO.getAddress().getWard();
        String buildingName = orderDTO.getAddress().getBuildingName();
        Address address = addressRepo
                .findByCountryAndDistrictAndCityAndPincodeAndWardAndBuildingName(
                        country, district,
                        city, pincode, ward, buildingName);
        if (address == null) {
            address = new Address(country, district, city, pincode, ward, buildingName);
            address = addressRepo.save(address);
        }
        order.setAddress(address);
        // Set time
        order.setOrderDateTime(LocalDateTime.now());
        // Save order
        orderRepo.save(order);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    @Transactional
    public OrderDTO createOrderOffline(OrderDTO orderDTO, List<ProductQuantity> productQuantities) {
        Order order = new Order();
        order.setDeliveryName(orderDTO.getDeliveryName());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());
        order.setEmail(orderDTO.getEmail());
        order.setOrderType(OrderType.ONLINE);
        // Set payment method
        Payment payment = new Payment();
        payment.setPaymentMethod(orderDTO.getPayment().getPaymentMethod());
        paymentRepo.save(payment);
        order.setPayment(payment);
        if (payment.getPaymentMethod().equals(PaymentMethod.COD)) {
            order.setOrderStatus(OrderStatus.PAID);
        } else {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        // Set OrderItem
        double total = productQuantities.stream().mapToDouble(p -> {
            Product product = productRepo.findById(p.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", p.getProductId()));
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
            String hash = HashUtil.generatePromotionHash(voucher);
            PromotionSnapshot voucherSnapshot = promotionSnapshotRepo.findByHash(hash);
            if (voucherSnapshot == null) {
                voucherSnapshot = modelMapper.map(voucher, PromotionSnapshot.class);
                voucherSnapshot.setPromotionId(null);
                voucherSnapshot.setHash(hash);
                promotionSnapshotRepo.save(voucherSnapshot);
            }
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
        // Save order
        orderRepo.save(order);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public String SendVerifyOrderEmail(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        if (!order.getPayment().getPaymentMethod().equals(PaymentMethod.COD)
                || !order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new APIException(
                    "Đơn hàng không hợp lệ. Đơn hàng phải ở trạng thái đang chờ xử lý và sử dụng phương thức thanh toán COD.");
        }
        String otp = generateOTPOrder(order);
        String htmlContent = Email.getFormOTPVerifyOrderSendEmail(otp, String.valueOf(orderId),
                order.getDeliveryName());
        EmailDetails emailDetails = new EmailDetails(order.getEmail(), htmlContent, "BookStore - Xác thực đơn hàng",
                null);
        emailService.sendMailWithAttachment(emailDetails);

        return "Gửi mã xác thực thành công đến email: " + order.getEmail();
    }

    @Override
    public String generateOTPOrder(Order order) {
        Optional<OTP> optionalOtp = otpRepo.findByOrderOrderIdAndType(order.getOrderId(), OTPType.ORDER_VERIFICATION);
        OTP otp;
        if (optionalOtp.isPresent()) {
            otp = optionalOtp.get();
        } else {
            otp = new OTP();
            otp.setOrder(order);
            otp.setType(OTPType.ORDER_VERIFICATION);
        }
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000;
        String strOTP = String.valueOf(code);
        otp.setCode(strOTP);
        otp.setExpiryDate(Instant.now().plus(5, ChronoUnit.MINUTES));
        otpRepo.save(otp);

        return strOTP;
    }

    @Override
    public Boolean verityOTPEmail(OtpDTO otpDTO) {
        Long orderId = otpDTO.getOrderId();
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        if (order.getOrderStatus() != OrderStatus.PENDING
                || order.getPayment().getPaymentMethod() != PaymentMethod.COD) {
            throw new APIException("Đơn hàng không hợp lệ");
        }
        OTP otp = otpRepo.findByOrderOrderIdAndType(orderId, OTPType.ORDER_VERIFICATION)
                .orElseThrow(() -> new ResourceNotFoundException("OTP", "orderId", orderId));

        if (!otp.getCode().equals(otpDTO.getCode())) {
            return false;
        }
        if (Instant.now().isAfter(otp.getExpiryDate())) {
            return false;
        }
        order.setOrderStatus(OrderStatus.PAID);
        orderRepo.save(order);
        otpRepo.delete(otp);

        return true;
    }

    private void checkPromotion(Promotion promotion, PromotionType type, double subTotal) {
        boolean checkType = promotion.getPromotionType().equals(type);
        LocalDateTime now = LocalDateTime.now();
        boolean checkDate = promotion.getStartDate().isBefore(now) && promotion.getEndDate().isAfter(now);
        boolean checkValue = subTotal >= promotion.getValueApply();
        if (!checkType || !checkDate || !checkValue || !promotion.getStatus()) {
            throw new APIException(
                    "Mã giảm giá " + (type.equals(PromotionType.VOUCHER) ? "đơn hàng" : "vận chuyển")
                            + " không hợp lệ");
        }
    }
}
