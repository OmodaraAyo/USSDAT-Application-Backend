package main.service.implementations;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import main.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendEmail(String registeredCompanyEmail, String generatedPassword) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setFrom(new InternetAddress("support@backwyth.com"));
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, registeredCompanyEmail);
            mimeMessage.setSubject("Welcome to Backwyth - Your USSD Gateway Starts Here");

            String htmlContent ="<!DOCTYPE html"+
                    "<html><head><meta charset='UTF-8'><title>Welcome to Backwyth</title></head><body>"+
                                "<p>Hey there,</p>"+
                                "<p>Thanks for registering with Backwyth! We're excited to have your company onboard, Backwyth is your one-stop platform for managing USSD menus with ease, a powerful control and smooth integration. With Backwyth, your company can effortlessly build, customize, and deploy USSD menus without worrying about telecom complexity.</p>"+
                                "<p>Here's what you get access to on Backwyth:</p>"+
                                "<ul>" +
                                    "<li>A dedicated USSD path under our shared code shortcode</li>" +
                                    "<li>A flexible dashboard to manage your menu prompts and customer journey</li>" +
                                    "<li>A secure API gateway that forwards user requests directly to your server</li>" +
                                    "<li>Real-time session tracking and seamless input collection</li>" +
                                    "<li>Tools to preview, update, and deploy your USSD logic without code</li>" +
                                "</ul>"+
                                "<strong>Your Account Details</strong>"+
                                "<p>You can log in to your admin dashboard using the credentials below:</p>"+
                                "<p><strong>Username: </strong>"+ registeredCompanyEmail + "</p>"+
                                "<p><strong>Password: </strong>"+ generatedPassword +"</p>"+
                                "<p>We recommend logging in immediately and updating your password.</p>"+
                                "<p>Log in at: <a href=\"https://github.com/Backwyth/Backwyth/releases\" target=\"_blank\">https://github.com/Backwyth/Backwyth/releases</a></p>"+
                                "<p>Need help getting started? Check out our <a href=\"https://github.com/Backwyth/Backwyth/Docs\">Docs</a> or reach out to our team- we're here to support you.</p>"+
                                "<p>Welcome aboard,</p>"+
                                "<p><strong>The Backwyth Team.</strong></p>"+
                            "</body></html>";
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");
            mailSender.send(mimeMessage);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
