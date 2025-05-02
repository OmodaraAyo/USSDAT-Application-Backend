package main.service.interfaces;

import main.dtos.requests.companyFaceRequest.ChangePasswordRequest;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.UpdateCompanyRequest;
import main.dtos.responses.companyFaceResponse.*;
import main.models.companies.Company;


import java.util.List;

public interface CompanyService {

    CompanySignUpResponse registerCompany(CompanySignUpRequest companySignUpRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyById();
    CompanyDetailsResponse findCompanyByEmail(String companyEmail);
    UpdateCompanyResponse updateCompanyDetails(UpdateCompanyRequest updateRequest);
    ChangePasswordResponse resetPassword(ChangePasswordRequest request);
    LogoutResponse logOut();
    DeleteResponse deleteById();
    List<Company> getAllCompanies();
    DeleteResponse deleteAllCompanies();
    Company getByCompanyId(String id);
    DeleteResponse deleteByCompanyId(String id);
    Company saveCompany(Company company);
    Company getByCompanyEmail(String email);
}
