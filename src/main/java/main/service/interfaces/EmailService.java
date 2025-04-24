package main.service.interfaces;

public interface EmailService {

    void sendEmail(String registeredCompanyEmail, String generatedPassword);
}
