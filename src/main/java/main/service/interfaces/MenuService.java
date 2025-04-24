package main.service.interfaces;

import main.dtos.requests.companyFaceRequest.CreateOptionRequest;
import main.dtos.requests.companyFaceRequest.FindMenuOptionByIdRequest;
import main.dtos.requests.companyFaceRequest.MenuOptionRequest;
import main.dtos.requests.companyFaceRequest.UpdateOptionRequest;
import main.dtos.responses.companyFaceResponse.CreatedOptionResponse;
import main.dtos.responses.companyFaceResponse.DeleteMenuOptionResponse;
import main.dtos.responses.companyFaceResponse.MenuOptionResponse;
import main.dtos.responses.companyFaceResponse.UpdateOptionResponse;

public interface MenuService {

    CreatedOptionResponse addNewOption(CreateOptionRequest register);
    MenuOptionResponse getMenuOptionByTitle(MenuOptionRequest menuOptionRequest);
    DeleteMenuOptionResponse deleteMenuOptionById(String menuId);
    MenuOptionResponse getMenuOptionById(FindMenuOptionByIdRequest checkBalance);
    UpdateOptionResponse updateMenuOption(UpdateOptionRequest updateOptionRequest);
}
