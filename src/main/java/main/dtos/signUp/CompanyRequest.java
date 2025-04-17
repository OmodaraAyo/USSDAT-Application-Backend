package main.dtos.signUp;

import lombok.*;
import main.models.enums.Category;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyRequest {
    private String companyName;
    private String companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private Category category;
    private String companyApiKey;
    private String baseUrl;
}
