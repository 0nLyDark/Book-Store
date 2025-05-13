package com.dangphuoctai.BookStore.utils;

import java.util.regex.Pattern;

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
}
