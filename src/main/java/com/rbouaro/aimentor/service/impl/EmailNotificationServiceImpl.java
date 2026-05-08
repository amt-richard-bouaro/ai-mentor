package com.rbouaro.aimentor.service.impl;

import com.rbouaro.aimentor.config.app.AppConfigProperties;
import com.rbouaro.aimentor.entity.Email;
import com.rbouaro.aimentor.constants.enums.EmailStatus;
import com.rbouaro.aimentor.event.UserDeletedEvent;
import com.rbouaro.aimentor.exceptions.EmailNotFoundException;
import com.rbouaro.aimentor.repository.EmailRepository;
import com.rbouaro.aimentor.service.EmailNotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl  implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailRepository emailRepository;
    private final AppConfigProperties appConfigProperties;

    @Async
    @Retryable(
            retryFor = { MailException.class, MessagingException.class },
            maxAttemptsExpression = "${app.email.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.email.retry.initial-delay}", multiplier = 2.0)
    )
    @Transactional
    public void sendDeletionConfirmation(UserDeletedEvent event, Long id) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new EmailNotFoundException("Email log not found for id: " + id));

        try {
            Context context = new Context();
            context.setVariable("username", event.username());
            String htmlContent = templateEngine.process("emails/account-deleted", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(appConfigProperties.from());
            helper.setTo(event.email());
            helper.setSubject("Account Deleted - AI Mentor");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            email.setStatus(EmailStatus.SENT);
            email.setErrorMessage(null);
        } catch (MailException | MessagingException e) {
            email.incrementRetryCount();
            email.setErrorMessage(e.getMessage());
            emailRepository.save(email);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(e);
        }
        emailRepository.save(email);
    }


    @Recover
    @Transactional
    public void recover(Exception e, UserDeletedEvent event, Long id) {
        log.error("Retries exhausted for email to {}: {}", event.email(), e.getMessage());
        if (id == null) {
            log.warn("Cannot recover email status because log id is null");
            return;
        }
        emailRepository.findById(id).ifPresent(email -> {
            email.setStatus(EmailStatus.FAILED);
            emailRepository.save(email);
        });
    }

}
