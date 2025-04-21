package main.service.implementations;

import main.dtos.requests.MenuRequest;
import main.dtos.responses.MenuResponse;
import main.exceptions.ValidatorException;
import main.models.users.Company;
import main.models.users.Menu;
import main.repositories.MenuRepo;
import main.service.interfaces.CompanyService;
import main.service.interfaces.MenuService;
import main.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private AuthenticatedCompanyService authenticatedCompanyService;

    @Override
    public MenuResponse addNewMenu(MenuRequest request) {
        Company activeCompanySession = authenticatedCompanyService.getCurrentAuthenticatedCompany();

        Menu savedMenu = saveMenuForCompany(activeCompanySession, request);
        addMenuToActiveCompany(activeCompanySession, savedMenu);
        return new MenuResponse(savedMenu.getCompanyId(), savedMenu.getId(), "Awesome! Your menu is now live.");
    }

    private Menu saveMenuForCompany(Company activeCompanySession, MenuRequest request) {
        validateCompanyRequest(activeCompanySession,request);
        Menu menu = new Menu();
        menu.setCompanyId(activeCompanySession.getCompanyId());
        menu.setTitle(request.getTitle());
        menu.setCreatedAt(DateUtil.getCurrentDate());
        menu.setUpdatedAt(DateUtil.getCurrentDate());
        return menuRepo.save(menu);
    }

    private void addMenuToActiveCompany(Company activeCompanySession, Menu savedMenu) {
        activeCompanySession.getDefaultMenus().add(savedMenu);
        companyService.saveCompany(activeCompanySession);
    }

    private void validateCompanyRequest(Company activeCompanySession, MenuRequest request) {
        ValidatorException.validateMenuRequest(request);
        checkIfMenuTitleExistAlready(activeCompanySession, request);
    }

    private void checkIfMenuTitleExistAlready(Company activeCompanySession, MenuRequest request) {
        for (Menu menu : activeCompanySession.getDefaultMenus()) {
            if (menu.getTitle().equalsIgnoreCase(request.getTitle())) {
                throw new ValidatorException("Oops! A menu titled 'Register' already exists. Please try another name.");
            }
        }
    }

}
