//package main.service.implementations;
//
//import main.service.interfaces.EmailService;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailServiceImpl implements EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public EmailServiceImpl(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    @Async
//    @Override
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(to);
//        message.setFrom("backwith@gmail.com");
//        message.setSubject(subject);
//        message.setText(body);
//
//        mailSender.send(message);
//    }
//}
