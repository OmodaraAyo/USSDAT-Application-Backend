package main.dtos.signUp;

import lombok.*;
import main.models.enums.Category;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyRequest {
    private String companyName;
    private List<String> companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private String category;
    private String companyApiKey;
    private String baseUrl;
}
