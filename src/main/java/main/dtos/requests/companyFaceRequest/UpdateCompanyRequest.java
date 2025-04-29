package main.dtos.requests.companyFaceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompanyRequest {
    private CompanyRequest companyRequest = new CompanyRequest();
    private String lastLoginDate;
    private String updatedAt;
}

