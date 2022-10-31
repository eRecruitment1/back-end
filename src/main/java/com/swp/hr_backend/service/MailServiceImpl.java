package com.swp.hr_backend.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.DataMailRequest;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.utils.MailBody;
import com.swp.hr_backend.utils.MailSubjectConstant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private final JavaMailSender javaMailSender;
    private final AccountRepository accRepo;

    @Override
    public void sendHtmlMail(DataMailRequest dataMail, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setTo(dataMail.getTo());
        helper.setSubject(dataMail.getSubject());
        helper.setText(body, true);

        javaMailSender.send(message);

    }
    
    @Override
	public void sendMailForgetPassword(String email) throws MessagingException, BaseCustomException {
		DataMailRequest dataMailRequest = new DataMailRequest();
		dataMailRequest.setTo(email);
		dataMailRequest.setSubject(MailSubjectConstant.CHANGE_PASSWORD);
		Account acc = accRepo.findByEmail(email);
		if(acc != null && acc.isEnabled() && acc.isStatus()) {
			String link = "" + acc.getAccountID();
			sendHtmlMail(dataMailRequest, MailBody.VertifyEmailForgotPassword(acc.getFirstname() + " " + acc.getLastname(), link));
		} else throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Email!").build());
    }
}
