package main.service.interfaces;

import main.dtos.requests.*;
import main.dtos.responses.*;

public interface MenuService {

    CreatedOptionResponse addNewOption(CreateOptionRequest register);
    MenuOptionResponse getMenuOptionByTitle(MenuOptionRequest menuOptionRequest);
    DeleteMenuOptionResponse deleteMenuOptionById(String menuId);
    MenuOptionResponse getMenuOptionById(FindMenuOptionByIdRequest checkBalance);
    UpdateOptionResponse updateMenuOption(UpdateOptionRequest updateOptionRequest);
    CompanyMenuOptionResponse getMenuOptionsForCompany(CompanyMenuOptionRequest companyMenuOptionRequest);
}
