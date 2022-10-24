package com.swp.hr_backend.service;

import com.swp.hr_backend.model.request.DataMailRequest;
import javax.mail.MessagingException;

public interface MailService {
    void sendHtmlMail(DataMailRequest dataMail, String body) throws MessagingException;
}
