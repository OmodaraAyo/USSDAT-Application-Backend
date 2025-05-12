package main.dtos.requests.customerFaceRequest;

import lombok.Data;

@Data
public class FetchMenuFromCompanyApiRequest {
    private String subCode;
    private String context;
    private String response;
    private String sessionId;

}
