package main.service.interfaces;

import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.models.companies.Company;


import java.util.List;

public interface CompanyService {
    CompanySignUpResponse registerCompany(CompanySignUpRequest companySignUpRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyById(String id);
    CompanyDetailsResponse findCompanyByEmail(String companyEmail);
    UpdateCompanyResponse updateCompanyDetails(String companyId, UpdateCompanyRequest updateRequest);
    ChangePasswordResponse resetPassword(String companyId, ChangePasswordRequest changePasswordRequest);
    LogoutResponse logOut(String companyId);
//    DeleteCompanyAccountResponse deleteByCompanyId(String id);
    DeleteCompanyAccountResponse deactivateCompany(String companyId);
    List<Company> getAllCompanies();
    void deleteAllCompanies();
    Company getByCompanyId(String id);
    Company saveCompany(Company company);
    Company getByCompanyEmail(String email);
}
