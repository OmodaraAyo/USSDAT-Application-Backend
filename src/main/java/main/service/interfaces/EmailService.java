package main.service.interfaces;

import main.models.users.Company;

public interface EmailService {

    void sendEmail(String registeredCompanyEmail, String generatedPassword);
}
