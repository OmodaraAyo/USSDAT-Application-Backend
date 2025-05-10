package main.dtos.requests.companyFaceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CompanyRequest {
    private String companyName;
    private List<String> companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private String category;
    private String companyApiKey;
    private String baseUrl;
}
