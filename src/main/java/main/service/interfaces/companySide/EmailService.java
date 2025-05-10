package main.service.interfaces.companySide;

public interface EmailService {

    void sendEmail(String companyName, String registeredCompanyEmail, String generatedPassword);
}
