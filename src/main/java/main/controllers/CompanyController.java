package main.controllers;

import jakarta.validation.Valid;
import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.responses.companyFaceResponse.ApiResponse;
import main.dtos.responses.companyFaceResponse.CompanyDetailsResponse;
import main.dtos.responses.companyFaceResponse.LoginResponse;
import main.dtos.responses.companyFaceResponse.CompanySignUpResponse;
import main.service.interfaces.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<CompanyDetailsResponse>> getCompanyDetails() {

        CompanyDetailsResponse companyDetailsResponse = companyService.findCompanyById();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", companyDetailsResponse));
    }
}
