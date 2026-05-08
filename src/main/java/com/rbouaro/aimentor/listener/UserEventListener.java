package com.rbouaro.aimentor.listener;

import com.rbouaro.aimentor.entity.Email;
import com.rbouaro.aimentor.constants.enums.EmailStatus;
import com.rbouaro.aimentor.event.UserDeletedEvent;
import com.rbouaro.aimentor.repository.EmailRepository;
import com.rbouaro.aimentor.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailRepository emailRepository;
    private final EmailNotificationService emailService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {

        Email email = Email.builder()
                        .recipient(event.email())
                        .templateName("account-deleted")
                        .status(EmailStatus.PENDING)
                        .retryCount(0)
                        .build();

        Email savedEmail = emailRepository.saveAndFlush(email);

        final Long emailId = savedEmail.getPk();

        log.info("Email log created with id {}", emailId);

        emailService.sendDeletionConfirmation(event, emailId);
    }

}