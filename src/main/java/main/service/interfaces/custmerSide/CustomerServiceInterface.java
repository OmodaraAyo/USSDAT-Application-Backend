package main.service.interfaces.custmerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;

public interface CustomerServiceInterface {
    FetchMenuResponse fetchMainMenu(FetchMenuRequest fetchMenuRequest);
    FetchMenuResponse fetchMenuFrmCompany(FetchMenuRequest fetchMenuRequest);
}
