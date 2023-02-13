package com.zerobase.wishmarket.domain.auth.components;

import static com.zerobase.wishmarket.domain.auth.exception.AuthErrorCode.CANNOT_FIND_MAIL_TEMPLATE;

import com.zerobase.wishmarket.domain.auth.exception.AuthErrorCode;
import com.zerobase.wishmarket.domain.auth.exception.AuthException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailComponents {

    @Value(value = "${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    private void sendMail(String address, String subject, String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(address);
            mailSender.send(mimeMessage);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new AuthException(AuthErrorCode.MAIL_SEND_FAIL);
        }
    }


    public void sendAuthCodeMail(String email, String authCode) {
        ClassPathResource resource = (ClassPathResource) resourceLoader
            .getResource("classpath:static/mailTemplate/AuthCodeMail.html");

        try {
            File file = resource.getFile();
            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = "";
            StringBuilder html = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                html.append(line);
            }

            bufferedReader.close();
            reader.close();

            html = new StringBuilder(html.toString().replace("${authcode}", authCode));

            this.sendMail(email, "회원 인증 메일", html.toString());
        } catch (IOException ex) {
            throw new AuthException(CANNOT_FIND_MAIL_TEMPLATE);
        }
    }

}
