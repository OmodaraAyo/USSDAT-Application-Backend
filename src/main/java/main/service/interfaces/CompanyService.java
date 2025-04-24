package main.service.interfaces;

import main.dtos.responses.companyFaceResponse.DeleteResponse;
import main.dtos.responses.companyFaceResponse.CompanyDetailsResponse;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.responses.companyFaceResponse.LoginResponse;
import main.dtos.responses.companyFaceResponse.LogoutResponse;
import main.dtos.requests.companyFaceRequest.CompanyRequest;
import main.dtos.responses.companyFaceResponse.CompanyResponse;
import main.dtos.requests.companyFaceRequest.ChangePasswordRequest;
import main.dtos.responses.companyFaceResponse.ChangePasswordResponse;
import main.dtos.requests.companyFaceRequest.UpdateCompanyRequest;
import main.dtos.responses.companyFaceResponse.UpdateCompanyResponse;
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
