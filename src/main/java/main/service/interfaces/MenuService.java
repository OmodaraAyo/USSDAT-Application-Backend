package main.service.interfaces;

import main.dtos.requests.companyFaceRequest.MenuRequest;
import main.dtos.responses.companyFaceResponse.MenuResponse;

public interface MenuService {

    MenuResponse addNewMenu(MenuRequest request);
}
