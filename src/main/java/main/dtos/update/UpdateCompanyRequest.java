package main.dtos.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.dtos.signUp.CompanyRequest;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompanyRequest {
    private CompanyRequest companyRequest = new CompanyRequest();
//    private boolean isFirstLogin;
    private String lastLoginDate;
    private String updatedAt;
}

