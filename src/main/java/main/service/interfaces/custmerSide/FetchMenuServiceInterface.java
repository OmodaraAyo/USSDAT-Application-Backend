package main.service.interfaces.custmerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyApiRequest;
import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyApiResponse;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import org.springframework.stereotype.Service;


public interface FetchMenuServiceInterface {
    FetchMenuFromCompanyDBResponse fetchMainMenu(FetchMenuFromCompanyDBRequest fetchMenuFromCompanyDBRequest);
    FetchMenuFromCompanyApiResponse fetchMenuFromCompanyApi(FetchMenuFromCompanyApiRequest fetchMenuRequest);
}
