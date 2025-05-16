package main.dtos.requests.companyFaceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompanyRequest {
    private List<String> companyPhone;
    private String businessRegistrationNumber;
    private String category;
    private String companyApiKey;
    private String baseUrl;
}

