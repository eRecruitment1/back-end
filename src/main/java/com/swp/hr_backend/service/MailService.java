package com.swp.hr_backend.service;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.model.request.DataMailRequest;
import javax.mail.MessagingException;

public interface MailService {
    void sendHtmlMail(DataMailRequest dataMail, String body) throws MessagingException;
    void sendMailForgetPassword(String email) throws MessagingException, BaseCustomException;
}
