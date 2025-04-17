package main.dtos.signIn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.models.enums.Category;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String adminId;
    private String companyId;
    private String ussdShortCode;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private Category category;
    private String companyApiKey;
    private String apiKey;
    private String baseUrl;
    private String createAt;
    private String updateAt;
    private boolean isActive;
}
