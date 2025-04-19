package main.service.interfaces;

import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;

public interface CompanyService {

    CompanyResponse registerCompany(CompanyRequest companyRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyById(String id);
    CompanyDetailsResponse findCompanyByEmail(String companyEmail);
}
