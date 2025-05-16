package main.dtos.responses.customerFaceResponse;

import lombok.Data;

@Data
public class FetchMenuFromCompanyApiResponse {
    private String message;
    private String context;
    private String companyApi;
}
