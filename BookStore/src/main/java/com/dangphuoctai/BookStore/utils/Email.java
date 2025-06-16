package com.dangphuoctai.BookStore.utils;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.enums.PaymentMethod;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderItemDTO;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class Email {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        try {
            new InternetAddress(email).validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    public static String getFormOTPVerifyAccountSendEmail(String verifyUrl, String fullName) {

        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2>Xin chào " + fullName + "!</h2>"
                + "<p>Chào mừng bạn đến với <strong>Nhà sách trực tuyến BookStore!</strong></p>"
                + "<p>Cảm ơn bạn đã đăng ký trở thành thành viên của BookStore."
                + "<p>Để sử dụng được tài khoản này, bạn cần kích hoạt bằng cách nhấn vào liên kết bên dưới:</p>"
                + "<table role='presentation' border='0' cellpadding='0' cellspacing='0' style='margin: 20px 0;'>"
                + "  <tr>"
                + "    <td align='center'>"
                + "      <a href='" + verifyUrl + "' "
                + "         style='display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; "
                + "         text-decoration: none; font-size: 16px; border-radius: 5px;'>"
                + "         Kích hoạt tài khoản"
                + "      </a>"
                + "    </td>"
                + "  </tr>"
                + "</table>"
                + "<p>Sau khi kích hoạt thành công, bạn có thể thay đổi mật khẩu khi đăng nhập vào website của chúng tôi.</p>"
                + "<p>Nếu có thắc mắc hay cần sự hỗ trợ, quý khách vui lòng liên hệ:</p>"
                + "<ul>"
                + "  <li>☎ (028) 3820 1417</li>"
                + "  <li>📱 Hotline: 0933 141 704</li>"
                + "  <li>📧 Email: <a href='mailto:info@bookstore.vn'>info@bookstore.vn</a></li>"
                + "</ul>"
                + "<p>Một lần nữa, chúng tôi cảm ơn bạn đã chọn BookStore.</p>"
                + "<p>Trân trọng,<br/>Đội ngũ BookStore</p>"
                + "</body></html>";

        return htmlContent;
    }

    public static String getFormOTPVerifyOrderSendEmail(String otp, String orderId, String fullName) {

        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2>Xin chào " + fullName + "!</h2>"
                + "<p>Cảm ơn bạn đã đặt hàng tại <strong>Nhà sách trực tuyến BookStore</strong>.</p>"
                + "<p>Để xác nhận đơn hàng <strong>#" + orderId + "</strong>, vui lòng sử dụng mã OTP sau:</p>"
                + "<div style='font-size: 24px; font-weight: bold; margin: 20px 0; color: #007bff;'>"
                + otp + "</div>"
                + "<p>Mã OTP này có hiệu lực trong vòng 5 phút.</p>"
                + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                + "<hr/>"
                + "<p>Nếu có thắc mắc hay cần hỗ trợ, vui lòng liên hệ:</p>"
                + "<ul>"
                + "  <li>☎ (028) 3820 1417</li>"
                + "  <li>📱 Hotline: 0933 141 704</li>"
                + "  <li>📧 Email: <a href='mailto:info@bookstore.vn'>info@bookstore.vn</a></li>"
                + "</ul>"
                + "<p>Trân trọng,<br/>Đội ngũ BookStore</p>"
                + "</body></html>";

        return htmlContent;
    }

    public static String generateOrderEmailContent(OrderDTO order) {
        String statusText;
        String paymentStatus = order.getPayment().getPaymentMethod() + "( đã thanh toán )";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy mm:HH");
        String formattedDate = order.getOrderDateTime().format(formatter);
        switch (order.getOrderStatus()) {
            case PAID:
                statusText = "Đã đặt hàng thành công";
                break;
            case SHIPPED:
                statusText = "Đơn hàng đang được vận chuyển";
                break;
            case COMPLETED:
                statusText = "Đơn hàng đã được giao thành công";
                break;
            case FAILED:
                statusText = "Đơn hàng giao hàng thất bại, vui lòng liên hệ với chúng tôi để được hỗ trợ";
                break;
            case CANCELLED:
                statusText = "Đơn hàng của bạn đã bị hủy";
                break;
            default:
                statusText = "Đang xử lý";
                break;
        }
        if (PaymentMethod.COD.equals(order.getPayment().getPaymentMethod())) {
            paymentStatus = "COD ( Thanh toán khi nhận hàng )";
        }
        StringBuilder sb = new StringBuilder();

        sb.append("<h2>Xin chào ").append(order.getDeliveryName()).append(",</h2>");
        sb.append("<p>Cảm ơn bạn đã đặt hàng tại <b>BookStore</b>.</p>");
        sb.append("<p><b>Mã đơn hàng:</b> ").append(order.getOrderCode()).append("</p>");
        sb.append("<p><b>Ngày đặt hàng:</b> ").append(formattedDate).append("</p>");
        sb.append("<p><b>Trạng thái:</b> ").append(statusText).append("</p>");
        sb.append("<p>Phương thức thanh toán: ").append(paymentStatus).append("</p>");

        sb.append("<h3>Thông tin giao hàng:</h3>");
        sb.append("<p>Người nhận: ").append(order.getDeliveryName()).append("</p>");
        sb.append("<p>SĐT: ").append(order.getDeliveryPhone()).append("</p>");
        sb.append("<p>Địa chỉ: ").append(order.getAddress().toString()).append("</p>");

        sb.append("<h3>Chi tiết đơn hàng:</h3>");
        sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
        sb.append("<tr>")
                .append("<th>Sản phẩm</th>")
                .append("<th>Số lượng</th>")
                .append("<th>Đơn giá (VND)</th>")
                .append("<th>Giảm giá (%)</th>")
                .append("<th>Thành tiền (VND)</th>")
                .append("</tr>");

        for (OrderItemDTO item : order.getOrderItems()) {
            double unitPrice = item.getPrice();
            int quantity = item.getQuantity();
            double discountPercent = item.getDiscount(); // từ 0 đến 100
            double thanhTien = unitPrice * quantity * (100 - discountPercent) / 100.0;

            sb.append("<tr>")
                    .append("<td>").append(item.getProduct().getProductName()).append("</td>")
                    .append("<td style='text-align: center;'>").append(quantity).append("</td>")
                    .append("<td style='text-align: right;'>").append(String.format("%,.0f", unitPrice)).append("</td>")
                    .append("<td style='text-align: center;'>").append(String.format("%.0f", discountPercent))
                    .append("%</td>")
                    .append("<td style='text-align: right;'>").append(String.format("%,.0f", thanhTien)).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");

        sb.append("<h3>Thanh toán:</h3>");
        sb.append("<p>Tạm tính: ").append(String.format("%,.0f", order.getSubTotal())).append(" VND</p>");
        sb.append("<p>Phí vận chuyển: ").append(String.format("%,.0f", order.getPriceShip())).append(" VND</p>");

        // Mã giảm giá (coupon)
        if (order.getCoupon() != null) {
            PromotionDTO coupon = order.getCoupon();
            sb.append("<p>Mã giảm giá: ").append(coupon.getPromotionCode()).append(" - ");
            if (Boolean.TRUE.equals(coupon.getValueType())) {
                sb.append("Giảm ").append(coupon.getValue()).append("%");
            } else {
                sb.append("Giảm ").append(String.format("%,.0f", coupon.getValue())).append(" VND");
            }
            sb.append("</p>");
        }

        // Mã freeship
        if (order.getFreeship() != null) {
            PromotionDTO freeship = order.getFreeship();
            sb.append("<p>Mã freeship: ").append(freeship.getPromotionCode()).append(" - ");
            if (Boolean.TRUE.equals(freeship.getValueType())) {
                sb.append("Giảm ").append(freeship.getValue()).append("% phí ship");
            } else {
                sb.append("Giảm ").append(String.format("%,.0f", freeship.getValue())).append(" VND phí ship");
            }
            sb.append("</p>");
        }
        sb.append("<p><b>Tổng cộng: ").append(String.format("%,.0f", order.getTotalAmount())).append(" VND</b></p>");

        if (order.getOrderStatus().equals(OrderStatus.PAID)) {
            sb.append("<br/><p>Chúng tôi sẽ xử lý đơn hàng trong thời gian sớm nhất.</p>");
        }

        sb.append("<p>Trân trọng,<br/>BookStore Team</p>");

        return sb.toString();
    }

}
