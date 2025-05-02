package main.controllers.companies;

import jakarta.validation.Valid;
import main.dtos.requests.companyFaceRequest.ChangePasswordRequest;
import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.responses.companyFaceResponse.*;
import main.service.interfaces.CompanyService;
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyDetailsResponse>> getCompanyDetails() {

        CompanyDetailsResponse companyDetailsResponse = companyService.findCompanyById();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", companyDetailsResponse));
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> resetPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        ChangePasswordResponse changePasswordResponse = companyService.resetPassword(changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", changePasswordResponse));
    }
}
