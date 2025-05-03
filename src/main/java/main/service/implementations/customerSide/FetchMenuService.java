package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyApiRequest;
import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyApiResponse;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import main.exceptions.CompanyNotFound;
import main.models.companies.Menu;
import main.repositories.CompanyRepo;
import main.repositories.MenuRepo;
import main.service.interfaces.custmerSide.FetchMenuServiceInterface;
import main.utils.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchMenuService implements FetchMenuServiceInterface {

    @Autowired
    private CompanyRepo companyRepo;


    @Override
    public FetchMenuFromCompanyDBResponse fetchMainMenu(FetchMenuFromCompanyDBRequest fetchMenuFromCompanyDBRequest) {
        FetchMenuFromCompanyDBResponse response = new FetchMenuFromCompanyDBResponse();
        Menu menu = companyRepo.findByUssdShortCode(fetchMenuFromCompanyDBRequest.getSubCode())
                .orElseThrow(() -> new CompanyNotFound("This company does not exist"))
                .getMenu();
        if (menu == null) {
            response.setMessage("This company has no menu");
            response.setContext("No menu");
        }
        response.setMessage("Menu fetched successfully");
        response.setContext("main menu");
        assert menu != null;
        response.setOptions(menu.getOptions());
        return response;
    }

    @Override
    public FetchMenuFromCompanyApiResponse fetchMenuFromCompanyApi(FetchMenuFromCompanyApiRequest fetchMenuRequest) {

        return null;
    }


}