package com.gnimty.communityapiserver.domain.member.service.utils;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailSenderUtil {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async(value = "mailExecutor")
    public void sendEmail(String subject, String to, String code, String emailTemplate, String banner) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(subject);
            helper.setTo(to);
            Map<String, String> emailValues = new HashMap<>();
            emailValues.put("code", code);

            Context context = new Context();
            emailValues.forEach(context::setVariable);

            String html = templateEngine.process(emailTemplate, context);
            helper.setText(html, true);

            helper.addInline("banner", new ClassPathResource(banner));
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
