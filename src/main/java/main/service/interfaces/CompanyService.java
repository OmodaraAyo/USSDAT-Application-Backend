package main.service.interfaces;

import main.dtos.company.CompanyDetailsRequest;
import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest companyRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyByEmail(CompanyDetailsRequest companyDetailsRequest);
}
