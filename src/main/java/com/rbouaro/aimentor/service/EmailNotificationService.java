package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.event.UserDeletedEvent;
import org.springframework.mail.MailException;

public interface EmailNotificationService {

    void sendDeletionConfirmation(UserDeletedEvent event, Long id);

    void recover(Exception e, UserDeletedEvent event, Long id);

}
