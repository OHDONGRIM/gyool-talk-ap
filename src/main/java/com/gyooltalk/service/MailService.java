package com.gyooltalk.service;

import com.gyooltalk.entity.EmailAuth;
import com.gyooltalk.repository.EmailAuthRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private EmailAuthRepository emailAuthRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(3); // 0~2 랜덤 선택
            switch (idx) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // a~z
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // A~Z
                case 2 -> key.append(random.nextInt(10)); // 0~9
            }
        }
        return key.toString();
    }

    public MimeMessage createMessage(String email) {
        String authNum = createNumber(); // 개별 인증번호 생성
        log.info("Generated Auth Number: {}", authNum);
        Long expiredTime = System.currentTimeMillis() + 5 * 60 * 1000;

        try {
            saveAuthNum(email, authNum, expiredTime);
        }catch(Exception e) {
            e.printStackTrace();
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject("[귤톡] 이메일 인증 번호");

            String body = "<html><body style='background-color: #DCD7CB; margin: 0 auto; padding-top: 50px; color: #727272; width: 100%; height: 100%;'>";
            body += "<div style='max-width: 600px; margin: 0; text-align: left;'>";
            body += "<img class='logo' src='cid:image' style='display: inline-block; width: 55px; height: auto;'>";
            body += "<h1 style='padding-top: 10px; font-size: 20px;'>이메일 주소 인증</h1>";
            body += "<p style='padding-top: 20px; font-size: 13px; opacity: 0.6; line-height: 30px; font-weight: 400;'>안녕하세요? 귤톡입니다.<br />";
            body += "하단의 인증 번호로 이메일 인증을 완료하세요.<br />";
            body += "감사합니다.</p>";
            body += "<div class='code-box' style='margin-top: 50px; padding-top: 20px; color: #F1F1F1; padding-bottom: 20px; font-size: 15px; font-weight: bold; text-align: center; background-color: #EF7417; border-radius: 10px; width: 100%; box-sizing: border-box;'>" + authNum + "</div>";
            body += "</div>"; // div 끝
            body += "</body></html>";

            messageHelper.setText(body, true);

            // 이미지 첨부 예외 처리
            ClassPathResource image = new ClassPathResource("static/img/gyool.png");
            if (!image.exists()) {
                log.error("Email image resource not found");
            } else {
                messageHelper.addInline("image", image);
            }

        } catch (Exception e) {
            log.error("Failed to create email message", e);
            return null;
        }
        return mimeMessage;
    }

    public void sendMail(String email) {
        try {
            MimeMessage message = createMessage(email);
            if (message == null) {
                log.error("Failed to create email message for {}", email);
                return;
            }
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }

    // 이메일 인증 번호 저장
    public void saveAuthNum(String email, String authNum, Long expiredTime) {
        EmailAuth emailAuthNum = EmailAuth.builder()
                .email(email)
                .authNum(authNum)
                .expiredTime(expiredTime)
                .build();

        emailAuthRepository.save(emailAuthNum);
    }

    // 이메일 인증 번호 검증
    public boolean checkAuthNum(String email, String authNum) {
        Optional<EmailAuth> optionalEmailAuthNum = emailAuthRepository.findByEmail(email);

        if (optionalEmailAuthNum.isPresent()) {
            EmailAuth emailAuthNum = optionalEmailAuthNum.get();

            // 인증번호가 일치하는지 확인하고, 만료 시간이 지나지 않았는지 확인
            if (emailAuthNum.getAuthNum().equals(authNum) && !isExpired(emailAuthNum)) {
                return true;
            }
        }

        return false;
    }

    // 인증 번호 만료 여부 확인
    private boolean isExpired(EmailAuth emailAuthNum) {
        long currentTime = System.currentTimeMillis();
        return currentTime > emailAuthNum.getExpiredTime();
    }
}