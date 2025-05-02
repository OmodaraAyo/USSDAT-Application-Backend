package main.controllers;

import jakarta.validation.Valid;
import main.dtos.requests.SignUpRequest;
import main.dtos.requests.LoginRequest;
import main.dtos.responses.ApiResponse;
import main.dtos.responses.CompanyDetailsResponse;
import main.dtos.responses.SignUpResponse;
import main.dtos.responses.LoginResponse;
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
    public ResponseEntity<ApiResponse<SignUpResponse>> registerNewCompany(@Valid @RequestBody SignUpRequest signUpRequest) {

        SignUpResponse registeredCompany = companyService.registerCompany(signUpRequest);
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
