package main.dtos.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.dtos.company.CompanyDetailsResponse;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompanyResponse {
    private String message;
    private CompanyDetailsResponse companyDetails = new CompanyDetailsResponse();
}

