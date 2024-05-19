package io.getarrays.userservice.service.impl;

import io.getarrays.userservice.service.EmailService;
import io.getarrays.userservice.utils.EmailUtils;
import jakarta.activation.DataHandler;

import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

import static io.getarrays.userservice.utils.EmailUtils.getVerificationUrl;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "new user account verification";
    public static final String UTF_8_ENCODING = "UTF_8_ENCODING";
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async                         //ferqli threadler de isledecek ve suretli response verecek
    public void sendSimpleMailMessage(String name, String to, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name,host,token));

            emailSender.send(message);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try{
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name,host,token));
            //add atachments
            FileSystemResource dip = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/test/diplom.docx"));
            FileSystemResource ph = new FileSystemResource(new File(System.getProperty("user.home") + "/Desktop/Screenshot.png"));
            helper.addAttachment(dip.getFilename(),dip);
            helper.addAttachment(ph.getFilename(),ph);
            emailSender.send(message);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String to, String token) {
        try{
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name,host,token));

            //add atachments  ve  burani htmle de elave ede bilerik
            FileSystemResource dip = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/test/diplom.docx"));
            FileSystemResource ph = new FileSystemResource(new File(System.getProperty("user.home") + "/Desktop/Screenshot.png"));
            helper.addInline(getContentId(dip.getFilename()),dip);
            helper.addInline(getContentId(ph.getFilename()),ph);

            emailSender.send(message);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try{
            Context context = new Context();
            // context.setVariable("name",name);
            //   context.setVariable("url",getVerificationUrl(host,token));
            context.setVariables(Map.of("name",name,"url",getVerificationUrl(host,token)));  //key ler k1,k2 html filen da match olmalidir
            String text = templateEngine.process("emailtemplate",context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text,true);                 //html file gonderilir

            emailSender.send(message);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try{
                MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
          //helper.setText(text,true);
            Context context = new Context();
            context.setVariables(Map.of("name",name,"url",getVerificationUrl(host,token)));
            String text = templateEngine.process("emailtemplate",context);

            // add html email body   1
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text,"text/html");
            mimeMultipart.addBodyPart(messageBodyPart);

            // add images to the email body    2
            BodyPart imageBodyPart = new MimeBodyPart();
            FileDataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Desktop/Screenshot.png");
            imageBodyPart.setDataHandler(new DataHandler((jakarta.activation.DataSource) dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart (imageBodyPart);

            message.setContent(mimeMultipart);

            emailSender.send(message);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
    return emailSender.createMimeMessage();
    }

    private String  getContentId(String fileName){ return "<"+ fileName +">";}




}
