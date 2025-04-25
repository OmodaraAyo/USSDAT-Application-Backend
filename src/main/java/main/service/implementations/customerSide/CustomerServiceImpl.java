package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.exceptions.MenuOptionNotFoundException;
import main.models.companies.Menu;
import main.repositories.MenuRepo;
import main.service.interfaces.custmerSide.CustomerServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerServiceInterface{
    @Autowired
    MenuRepo menuRepo;

    @Override
    public FetchMenuResponse fetchMainMenu(FetchMenuRequest fetchMenuRequest) {
        Menu MainMenu = menuRepo.findById(fetchMenuRequest.getSubCode())
                .orElseThrow(() -> new MenuOptionNotFoundException("No menu available for this company"));
        StringBuilder menuDetails = new StringBuilder("Select an option from the menu below:\n");
        for (int index = 0; index < MainMenu.getOptions().size(); index++) {
            menuDetails.append(index + 1)
                    .append(". ")
                    .append(MainMenu.getOptions().get(index).getTitle())
                    .append("\n");
        }
        return new FetchMenuResponse(menuDetails.toString(), true);
    }

    @Override
    public FetchMenuResponse fetchMenuFrmCompany(FetchMenuRequest fetchMenuRequest) {
        return null;
    }
}
