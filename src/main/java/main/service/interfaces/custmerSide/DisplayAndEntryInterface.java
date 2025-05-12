package main.service.interfaces.custmerSide;

import main.dtos.requests.customerFaceRequest.UserInteractionRequest;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;
import main.exceptions.CustomUssdException;

public interface DisplayAndEntryInterface {
//    UserInteractionResponse processUserInput(UserInteractionRequest request);
    UserInteractionResponse processCompanyResponse(UserInteractionRequest request, UserInteractionResponse companyResponse);

    UserInteractionResponse processUserInput(UserInteractionRequest request) throws CustomUssdException;
}