package main.service.interfaces;

import main.dtos.responses.DeleteResponse;
import main.dtos.responses.CompanyDetailsResponse;
import main.dtos.requests.LoginRequest;
import main.dtos.responses.LoginResponse;
import main.dtos.responses.LogoutResponse;
import main.dtos.requests.CompanyRequest;
import main.dtos.responses.CompanyResponse;
import main.dtos.requests.ChangePasswordRequest;
import main.dtos.responses.ChangePasswordResponse;
import main.dtos.requests.UpdateCompanyRequest;
import main.dtos.responses.UpdateCompanyResponse;
import main.models.users.Company;

import java.util.List;

public interface CompanyService {

    CompanyResponse registerCompany(CompanyRequest companyRequest);
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
