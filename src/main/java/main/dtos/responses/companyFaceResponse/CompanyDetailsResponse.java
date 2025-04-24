package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.models.enums.Category;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyDetailsResponse {
    private String companyId;
    private String ussdShortCode;
    private String companyName;
    private List<String> companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private Category category;
    private String companyApiKey;
    private String apiKey;
    private String baseUrl;
    private boolean isActive;
    private boolean isLoggedIn;
    private String role;
    private boolean isFirstLogin;
    private String lastLoginDate;
    private String createAt;
    private String updateAt;
}
