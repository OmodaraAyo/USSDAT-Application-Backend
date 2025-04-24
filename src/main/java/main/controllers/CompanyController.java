package main.controllers;

import jakarta.validation.Valid;
import main.dtos.requests.CompanyRequest;
import main.dtos.responses.ApiResponse;
import main.dtos.responses.CompanyResponse;
import main.service.interfaces.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CompanyResponse>> registerNewCompany(@Valid @RequestBody CompanyRequest companyRequest) {

        CompanyResponse registeredCompany = companyService.registerCompany(companyRequest);
        return ResponseEntity.ok(
          new ApiResponse<>(
                  "success",
                  registeredCompany
          )
        );
    }
}
