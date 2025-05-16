package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyApiRequest;
import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyApiResponse;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import main.exceptions.CompanyNotFound;
import main.models.companies.Company;
import main.models.companies.Menu;
import main.repositories.CompanyRepo;
import main.service.interfaces.custmerSide.FetchMenuServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchMenuService implements FetchMenuServiceInterface {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public FetchMenuFromCompanyDBResponse fetchMainMenu(FetchMenuFromCompanyDBRequest fetchMenuFromCompanyDBRequest) {
        FetchMenuFromCompanyDBResponse response = new FetchMenuFromCompanyDBResponse();
        Company company = companyRepo.findByUssdShortCode(fetchMenuFromCompanyDBRequest.getSubCode())
                .orElseThrow(() -> new CompanyNotFound("This company does not exist"));
        Menu menu = company.getMenu();
        if (menu == null) {
            response.setMessage("This company has no menu");
            response.setContext("No menu");
        } else {
            response.setMessage("Menu fetched successfully");
            response.setContext("main menu");
            response.setOptions(menu.getOptions());
        }
        return response;
    }

    @Override
    public FetchMenuFromCompanyApiResponse fetchMenuFromCompanyApi(FetchMenuFromCompanyApiRequest fetchMenuRequest) {
        FetchMenuFromCompanyApiResponse response = new FetchMenuFromCompanyApiResponse();
        Company company = companyRepo.findByUssdShortCode(fetchMenuRequest.getSubCode())
                .orElseThrow(() -> new CompanyNotFound("This company does not exist"));
        String companyApi = company.getBaseUrl();
        if (companyApi == null) {
            response.setMessage("This company has no API endpoint");
            response.setContext("No menu");
            response.setCompanyApi(null);
            return response;
        }

        response.setCompanyApi(companyApi);
        response.setMessage("Menu fetched successfully");
        response.setContext("company_api");

        // Call company API
        String url = companyApi + "/menu";
        try {
            String requestBody = "{\"context\":\"" + fetchMenuRequest.getContext() + "\",\"response\":\"" + fetchMenuRequest.getResponse() + "\"}";
            FetchMenuFromCompanyApiResponse apiResponse = restTemplate.postForObject(url, requestBody, FetchMenuFromCompanyApiResponse.class);
            if (apiResponse != null) {
                return apiResponse;
            } else {
                response.setMessage("Failed to fetch menu from company API");
                return response;
            }
        } catch (Exception e) {
            response.setMessage("Error communicating with company API: " + e.getMessage());
            return response;
        }
    }
}