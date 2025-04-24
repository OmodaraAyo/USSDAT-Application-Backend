package main.dtos.requests.customerFaceRequest;

import lombok.Data;

@Data
public class FetchMenuRequest {
    private String sessionId;
    private String subCode;
    private String context;
    private String response;
    private Integer page;
}
