package main.controllers.companies;

import jakarta.validation.Valid;
import main.dtos.requests.companyFaceRequest.ChangePasswordRequest;
import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.requests.companyFaceRequest.UpdateCompanyRequest;
import main.dtos.responses.companyFaceResponse.*;
import main.service.interfaces.companySide.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CompanySignUpResponse>> registerNewCompany(@Valid @RequestBody CompanySignUpRequest companySignUpRequest) {
        CompanySignUpResponse registeredCompany = companyService.registerCompany(companySignUpRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", registeredCompany));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loggedIn = companyService.signIn(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", loggedIn));
    }

    @GetMapping("/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyDetailsResponse>> getCompanyById(@PathVariable("companyId") String companyId) {
        CompanyDetailsResponse companyDetailsResponse = companyService.findCompanyById(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", companyDetailsResponse));
    }

    @PatchMapping("/{companyId}/password-reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> resetPassword(@PathVariable String companyId, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse changePasswordResponse = companyService.resetPassword(companyId,changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", changePasswordResponse));
    }

    @PatchMapping("/{company_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UpdateCompanyResponse>> updateCompanyDetails(@PathVariable String company_id, @Valid @RequestBody UpdateCompanyRequest updateCompanyRequest) {
        UpdateCompanyResponse updateCompanyResponse = companyService.updateCompanyDetails(company_id, updateCompanyRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", updateCompanyResponse));
    }

    @PatchMapping("/{company_id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeleteCompanyAccountResponse>> closeAccount(@PathVariable String company_id) {
        DeleteCompanyAccountResponse deleteCompanyAccountResponse = companyService.deactivateCompany(company_id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", deleteCompanyAccountResponse));
    }

    @PatchMapping("/{company_id}/logout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(@PathVariable String company_id) {
        LogoutResponse logoutResponse = companyService.logOut(company_id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", logoutResponse));
    }
}
