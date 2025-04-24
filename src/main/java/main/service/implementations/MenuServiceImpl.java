package main.service.implementations;

import main.dtos.requests.companyFaceRequest.CreateOptionRequest;
import main.dtos.requests.companyFaceRequest.FindMenuOptionByIdRequest;
import main.dtos.requests.companyFaceRequest.MenuOptionRequest;
import main.dtos.requests.companyFaceRequest.UpdateOptionRequest;
import main.dtos.responses.companyFaceResponse.CreatedOptionResponse;
import main.dtos.responses.companyFaceResponse.DeleteMenuOptionResponse;
import main.dtos.responses.companyFaceResponse.MenuOptionResponse;
import main.dtos.responses.companyFaceResponse.UpdateOptionResponse;
import main.exceptions.EmptyItemException;
import main.exceptions.MenuOptionNotFoundException;
import main.exceptions.ValidatorException;
import main.models.companies.Company;
import main.models.companies.Option;
import main.repositories.MenuRepo;
import main.service.interfaces.CompanyService;
import main.service.interfaces.MenuService;
import main.utils.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private AuthenticatedCompanyService authenticatedCompanyService;


    @Override
    public CreatedOptionResponse addNewOption(CreateOptionRequest optionRequest) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        ValidatorException.validateOptionRequest(optionRequest.getTitle());
        ValidatorException.validateDuplicateTitle(activeCompanySession, optionRequest);
        String generatedOptionId = generateOptionId();
        Company savedCompany = createNewOption(activeCompanySession, optionRequest, generatedOptionId);
        return new CreatedOptionResponse(savedCompany.getCompanyId(), savedCompany.getMenu().getId(), generatedOptionId, "Awesome! Your menu is now live.", true);
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

    private UpdateOptionResponse updatedOptionResponse(Company activeCompanySession, UpdateOptionRequest updateOptionRequest) {
        Option thisOption = fetchMenuById(activeCompanySession, updateOptionRequest.getOptionId());
        assert thisOption != null;
        thisOption.setTitle(updateOptionRequest.getNewOptionName());
        thisOption.setUpdatedAt(DateUtil.getCurrentDate());
//        activeCompanySession.getMenu().getOptions().set(, thisOption); this is where you are currently
        companyService.saveCompany(activeCompanySession);
        return new UpdateOptionResponse(thisOption.getOptionId(), true, DateUtil.getCurrentDate());
    }

    private String generateOptionId(){
        return ObjectId.get().toString();
    }

    private Company createNewOption(Company activeCompanySession, CreateOptionRequest optionRequest, String generatedOptionId) {
        Option option = new Option();
        option.setMenuId(activeCompanySession.getMenu().getId());
        option.setOptionId(generatedOptionId);
        option.setTitle(optionRequest.getTitle());
        option.setCreatedAt(DateUtil.getCurrentDate());
        option.setUpdatedAt(DateUtil.getCurrentDate());
        activeCompanySession.getMenu().getOptions().add(option);
        return companyService.saveCompany(activeCompanySession);
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
