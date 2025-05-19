package main.service.implementations.companySide;

import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.exceptions.EmptyItemException;
import main.exceptions.MenuOptionNotFoundException;
import main.exceptions.ValidatorException;
import main.helper.CompanyUpdateSaver;
import main.models.companies.Company;
import main.models.companies.Menu;
import main.models.companies.Option;
import main.repositories.MenuRepo;
import main.service.interfaces.companySide.MenuService;
import main.utils.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private CompanyUpdateSaver companyUpdateSaver;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private AuthenticatedCompanyService authenticatedCompanyService;


    @Override
    public Menu createDefaultMenu() {
        Menu menu = new Menu();
//        menu.setCompanyId(companyId);
        menu.setCreatedAt(DateUtil.getCurrentDate());
        menu.setUpdatedAt(DateUtil.getCurrentDate());
        return menuRepo.save(menu);
    }

    @Override
    public CreatedOptionResponse addNewOption(String companyId,  CreateOptionRequest optionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        ValidatorException.validateOptionRequest(optionRequest.getTitle());
        ValidatorException.validateDuplicateTitle(activeCompanySession, optionRequest.getTitle());
        String generatedOptionId = generateOptionId();
        Company savedCompany = createNewOption(activeCompanySession, optionRequest, generatedOptionId);
        return new CreatedOptionResponse(savedCompany.getCompanyId(), savedCompany.getMenu().getId(), optionRequest.getTitle(), generatedOptionId, "Awesome! Your menu is now live.", true);
    }

    @Override
    public MenuOptionResponse getMenuOptionByTitle(String companyId, String title) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return getAuthenticatedCompanyOptionByTitle(activeCompanySession, title);
    }

    @Override
    public DeleteMenuOptionResponse deleteMenuOptionById(String companyId, String optionId) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return deleteMenu(activeCompanySession, optionId);
    }

    @Override
    public MenuOptionResponse getMenuOptionById(String companyId, String optionId) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return getAuthenticatedCompanyMenuOptionById(activeCompanySession, optionId);
    }

    @Override
    public UpdateOptionResponse updateMenuOption(String companyId, String optionId, UpdateOptionRequest updateOptionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyId, activeCompanySession.getCompanyId());
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        ValidatorException.validateOptionRequest(updateOptionRequest.getNewMenuTitle());
        ValidatorException.validateDuplicateTitle(activeCompanySession, updateOptionRequest.getNewMenuTitle());
        return updatedOptionResponse(activeCompanySession, optionId, updateOptionRequest);
    }

    @Override
    public CompanyMenuOptionsResponse getMenuOptionsForCompany(String companyMenuOptionsRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateId(companyMenuOptionsRequest, activeCompanySession.getCompanyId());
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        CompanyMenuOptionsResponse companyMenuOptionsResponse = new CompanyMenuOptionsResponse();
        for(Option option : activeCompanySession.getMenu().getOptions()){
            companyMenuOptionsResponse.getMenuOptions().add(option.getTitle());
        }
        return companyMenuOptionsResponse;
    }

    private UpdateOptionResponse updatedOptionResponse(Company activeCompanySession, String optionId, UpdateOptionRequest updateOptionRequest) {
        Option thisOption = fetchMenuById(activeCompanySession, optionId);
        assert thisOption != null;
        thisOption.setTitle(updateOptionRequest.getNewMenuTitle());
        thisOption.setUpdatedAt(DateUtil.getCurrentDate());
        updateOption(thisOption, activeCompanySession.getMenu().getOptions(), optionId);
        menuRepo.save(activeCompanySession.getMenu());
        companyUpdateSaver.saveUpdatedCompany(activeCompanySession);
        return new UpdateOptionResponse(thisOption.getOptionId(), "Menu updated successfully." ,true, DateUtil.getCurrentDate());
    }

    private void updateOption(Option thisOption, List<Option> options, String optionId) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getOptionId().equals(optionId)) {
                options.set(i, thisOption);
                break;
            }
        }
    }

    private String generateOptionId(){
        return ObjectId.get().toString();
    }


    private Company createNewOption(Company activeCompanySession, CreateOptionRequest optionRequest, String generatedOptionId) {
        try{
            Option option = new Option();
            option.setMenuId(activeCompanySession.getMenu().getId());
            option.setOptionId(generatedOptionId);
            option.setTitle(optionRequest.getTitle().toLowerCase());
            option.setCreatedAt(DateUtil.getCurrentDate());
            option.setUpdatedAt(DateUtil.getCurrentDate());
            activeCompanySession.getMenu().getOptions().add(option);
            menuRepo.save(activeCompanySession.getMenu());
            return companyUpdateSaver.saveUpdatedCompany(activeCompanySession);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new option: "+e+ " Please try again!");
        }
    }

    private MenuOptionResponse getAuthenticatedCompanyMenuOptionById(Company activeCompanySession, String optionId) {
        for (Option option : activeCompanySession.getMenu().getOptions()) {
            if (option.getOptionId().equals(optionId)) {
                return new MenuOptionResponse(option.getOptionId() ,option.getTitle(), true);
            }
        }
        throw new MenuOptionNotFoundException(String.format("No menu option found with this id: \"%s\".", optionId));
    }

    private DeleteMenuOptionResponse deleteMenu(Company activeCompanySession, String menuId) {
        Option getMenuOption = fetchMenuById(activeCompanySession, menuId);
        if(getMenuOption != null) {
            activeCompanySession.getMenu().getOptions().remove(getMenuOption);
            companyUpdateSaver.saveUpdatedCompany(activeCompanySession);
            return new DeleteMenuOptionResponse("Deleted successfully.", true);
        }
        return new DeleteMenuOptionResponse("Menu not found.", false);
    }

    private Option fetchMenuById(Company company, String menuId) {
        for(Option option : company.getMenu().getOptions()){
            if(option.getOptionId().equalsIgnoreCase(menuId)){
                return option;
            }
        }
        return null;
    }

    private void checkIfActiveCompanySessionHaveAMenu(List<Option> menuOptions) {
        if(menuOptions.isEmpty()){
            throw new EmptyItemException("Looks like there’s nothing here yet. Add a menu to get started!");
        }
    }

    private MenuOptionResponse getAuthenticatedCompanyOptionByTitle(Company company, String titleRequest) {
        for(Option option : company.getMenu().getOptions()){
                if (option.getTitle().equalsIgnoreCase(titleRequest)){
                    return new MenuOptionResponse(option.getOptionId(), option.getTitle(), true);
                }
            }
        throw new MenuOptionNotFoundException(String.format("No menu option found with the title \"%s\".", titleRequest));
    }

}
