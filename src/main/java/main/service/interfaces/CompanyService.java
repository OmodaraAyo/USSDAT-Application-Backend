package main.service.interfaces;

import main.dtos.requests.*;
import main.dtos.responses.*;
import main.dtos.responses.CompanyResponse;
import main.models.users.Company;

import java.util.List;

public interface CompanyService {

    SignUpResponse registerCompany(SignUpRequest signUpRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyById(String id);
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
