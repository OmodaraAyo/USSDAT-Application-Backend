package main.service.interfaces;

import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.models.companies.Menu;

public interface MenuService {
    Menu createDefaultMenu();
    CreatedOptionResponse addNewOption(CreateOptionRequest register);
    MenuOptionResponse getMenuOptionByTitle(MenuOptionRequest menuOptionRequest);
    DeleteMenuOptionResponse deleteMenuOptionById(String menuId);
    MenuOptionResponse getMenuOptionById(FindMenuOptionByIdRequest checkBalance);
    UpdateOptionResponse updateMenuOption(UpdateOptionRequest updateOptionRequest);
    CompanyMenuOptionResponse getMenuOptionsForCompany(CompanyMenuOptionRequest companyMenuOptionRequest);
}
