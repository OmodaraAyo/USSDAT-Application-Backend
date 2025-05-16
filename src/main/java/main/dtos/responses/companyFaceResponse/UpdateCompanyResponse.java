package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompanyResponse {
    private String message;
    private String companyId;
//    private CompanyDetailsResponse companyDetails = new CompanyDetailsResponse();
}

