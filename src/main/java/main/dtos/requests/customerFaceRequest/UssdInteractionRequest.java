package main.dtos.requests.customerFaceRequest;

import lombok.Data;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;

@Data
public class UssdInteractionRequest {
    private UserInteractionRequest userRequest;
    private UserInteractionResponse companyResponse;
}
