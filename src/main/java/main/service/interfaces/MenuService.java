package main.service.interfaces;

import main.dtos.requests.*;
import main.dtos.responses.CreatedOptionResponse;
import main.dtos.responses.DeleteMenuOptionResponse;
import main.dtos.responses.MenuOptionResponse;
import main.dtos.responses.UpdateOptionResponse;

public interface MenuService {

    CreatedOptionResponse addNewOption(CreateOptionRequest register);
    MenuOptionResponse getMenuOptionByTitle(MenuOptionRequest menuOptionRequest);
    DeleteMenuOptionResponse deleteMenuOptionById(String menuId);
    MenuOptionResponse getMenuOptionById(FindMenuOptionByIdRequest checkBalance);
    UpdateOptionResponse updateMenuOption(UpdateOptionRequest updateOptionRequest);
}
