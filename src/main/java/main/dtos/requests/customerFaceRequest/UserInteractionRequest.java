package main.dtos.requests.customerFaceRequest;

import lombok.Data;

@Data
public class UserInteractionRequest {
    private String sessionId;
    private String serviceCode;
    private String text;
}
