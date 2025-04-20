package main.service.interfaces;

import main.dtos.DeleteResponse;
import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signOut.LogoutResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.dtos.update.ChangePasswordRequest;
import main.dtos.update.ChangePasswordResponse;
import main.dtos.update.UpdateCompanyRequest;
import main.dtos.update.UpdateCompanyResponse;

public interface CompanyService {

    CompanyResponse registerCompany(CompanyRequest companyRequest);
    LoginResponse signIn(LoginRequest loginRequest);
    CompanyDetailsResponse findCompanyById(String id);
    CompanyDetailsResponse findCompanyByEmail(String companyEmail);
    UpdateCompanyResponse updateCompanyDetails(UpdateCompanyRequest updateRequest);
    ChangePasswordResponse resetPassword(ChangePasswordRequest request);
    LogoutResponse logOut();
    DeleteResponse deleteById();
}
