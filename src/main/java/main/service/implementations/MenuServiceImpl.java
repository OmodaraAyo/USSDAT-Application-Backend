package main.service.implementations;

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
import main.service.interfaces.MenuService;
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
        menuRepo.save(new Menu());
        return null;
    }

    @Override
    public CreatedOptionResponse addNewOption(CreateOptionRequest optionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateOptionRequest(optionRequest.getTitle());
        ValidatorException.validateDuplicateTitle(activeCompanySession, optionRequest);
        String generatedOptionId = generateOptionId();
        Company savedCompany = createNewOption(activeCompanySession, optionRequest, generatedOptionId);

        CreatedOptionResponse createdOptionResponse = new CreatedOptionResponse();
        createdOptionResponse.setCompanyId(savedCompany.getCompanyId());
        createdOptionResponse.setMenuId(savedCompany.getMenu().getId());
        createdOptionResponse.setOptionId(generatedOptionId);
        createdOptionResponse.setTitle(optionRequest.getTitle());
        createdOptionResponse.setResponse("Awesome! Your menu is now live.");
        createdOptionResponse.setSuccess(true);
        return createdOptionResponse;
//        return new CreatedOptionResponse(savedCompany.getCompanyId(), savedCompany.getMenu().getId(), generatedOptionId, "Awesome! Your menu is now live.", true);
    }

    @Override
    public MenuOptionResponse getMenuOptionByTitle(MenuOptionRequest menuOptionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return getAuthenticatedCompanyOptionByTitle(activeCompanySession, menuOptionRequest.getMenuTitle());
    }

    @Override
    public DeleteMenuOptionResponse deleteMenuOptionById(String menuId) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return deleteMenu(activeCompanySession, menuId);
    }

    @Override
    public MenuOptionResponse getMenuOptionById(FindMenuOptionByIdRequest request) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return getAuthenticatedCompanyMenuOptionById(activeCompanySession, request.getOptionId());
    }

    @Override
    public UpdateOptionResponse updateMenuOption(UpdateOptionRequest updateOptionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        checkIfActiveCompanySessionHaveAMenu(activeCompanySession.getMenu().getOptions());
        return updatedOptionResponse(activeCompanySession, updateOptionRequest);
    }

    @Override
    public CompanyMenuOptionResponse getMenuOptionsForCompany(CompanyMenuOptionRequest companyMenuOptionRequest) {
        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        checkIfActiveCompanySessionHaveAMenu(company.getMenu().getOptions());
        CompanyMenuOptionResponse companyMenuOptionResponse = new CompanyMenuOptionResponse();
        for(Option option : company.getMenu().getOptions()){
            companyMenuOptionResponse.getMenuOptions().add(option.getTitle());
        }
        return companyMenuOptionResponse;
    }

    private UpdateOptionResponse updatedOptionResponse(Company activeCompanySession, UpdateOptionRequest updateOptionRequest) {
        Option thisOption = fetchMenuById(activeCompanySession, updateOptionRequest.getOptionId());
        assert thisOption != null;
        thisOption.setTitle(updateOptionRequest.getNewOptionName());
        thisOption.setUpdatedAt(DateUtil.getCurrentDate());
        updateOption(thisOption, activeCompanySession.getMenu().getOptions(), updateOptionRequest.getOptionId());
        menuRepo.save(activeCompanySession.getMenu());
        companyUpdateSaver.saveUpdatedCompany(activeCompanySession);
        return new UpdateOptionResponse(thisOption.getOptionId(), true, DateUtil.getCurrentDate());
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
                return new MenuOptionResponse(option.getTitle(), true);
            }
        }
        throw new MenuOptionNotFoundException(String.format("No menu option found with this id: \"%s\".", optionId));
    }

    private DeleteMenuOptionResponse deleteMenu(Company company, String menuId) {
        Option getMenuOption = fetchMenuById(company, menuId);
        if(getMenuOption != null) {
            company.getMenu().getOptions().remove(getMenuOption);
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
                    return new MenuOptionResponse(option.getTitle(), true);
                }
            }
        throw new MenuOptionNotFoundException(String.format("No menu option found with the title \"%s\".", titleRequest));
    }

}
