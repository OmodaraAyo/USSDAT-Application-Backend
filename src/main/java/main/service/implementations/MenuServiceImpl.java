package main.service.implementations;

import main.dtos.requests.MenuRequest;
import main.dtos.responses.MenuResponse;
import main.models.users.Company;
import main.models.users.Menu;
import main.repositories.MenuRepo;
import main.service.interfaces.CompanyService;
import main.service.interfaces.MenuService;
import main.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MenuRepo menuRepo;

    @Override
    public MenuResponse addNewMenu(String id, MenuRequest request) {
        Optional<Company> company = Optional.ofNullable(companyService.getCompanyById(id));

        if (company.isPresent()) {
            Menu menu = new Menu();
            menu.setTitle(request.getTitle());
            menu.setCreatedAt(DateUtil.getCurrentDate());
            menu.setUpdatedAt(DateUtil.getCurrentDate());
            Menu savedMenu = menuRepo.save(menu);
            company.get().getDefaultMenus().add(savedMenu);

            companyService.saveCompany(company.get());
            return new MenuResponse(savedMenu.getId(), "Awesome! Your menu is now live.");
//        }
        throw new RuntimeException("Company not found");
    }
}
