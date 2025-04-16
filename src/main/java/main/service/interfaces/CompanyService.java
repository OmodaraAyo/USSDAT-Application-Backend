package main.service.interfaces;

import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest companyRequest);
}
