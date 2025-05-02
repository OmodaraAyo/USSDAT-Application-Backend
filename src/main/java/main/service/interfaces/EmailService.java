package main.service.interfaces;

public interface EmailService {

    void sendEmail(String companyName, String registeredCompanyEmail, String generatedPassword);
}
