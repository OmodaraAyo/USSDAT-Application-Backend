package main.service.interfaces;

import main.dtos.requests.DeleteMenuOptionRequest;
import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.models.companies.Menu;

public interface MenuService {
    Menu createDefaultMenu();
    CreatedOptionResponse addNewOption(String companyId, CreateOptionRequest register);
    MenuOptionResponse getMenuOptionByTitle(String companyId, String title);
    DeleteMenuOptionResponse deleteMenuOptionById(String companyId, String optionId);
    MenuOptionResponse getMenuOptionById(String companyId, String optionId);
    UpdateOptionResponse updateMenuOption(String companyId, String optionId, UpdateOptionRequest updateOptionRequest);
    CompanyMenuOptionsResponse getMenuOptionsForCompany(String companyId);
}
