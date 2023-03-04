package com.zerobase.wishmarket.common.component;

import static com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode.CANNOT_FIND_MAIL_TEMPLATE;

import com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode;
import com.zerobase.wishmarket.domain.authcode.exception.AuthException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

        try {
            InputStream inputStream = resourceLoader
                .getResource("classpath:static/mailTemplate/AuthCodeMail.html").getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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
