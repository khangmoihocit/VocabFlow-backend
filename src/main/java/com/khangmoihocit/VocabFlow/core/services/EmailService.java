package com.khangmoihocit.VocabFlow.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);

        String title;
        switch (type) {
            case "REGISTER":
                title = "Xác nhận đăng ký tài khoản";
                break;
            case "FORGOT_PASSWORD":
                title = "Khôi phục mật khẩu";
                break;
            case "CHANGE_PASSWORD":
                title = "Thay đổi mật khẩu";
                break;
            default:
                title = "";
        }
        message.setSubject(title.isEmpty() ? "HEHE" : title);

        String content = "Mã OTP của bạn là: " + otpCode + "\nMã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.";
        message.setText(content);

        mailSender.send(message);
    }
}