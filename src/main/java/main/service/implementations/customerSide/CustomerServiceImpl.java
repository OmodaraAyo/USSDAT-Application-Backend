package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.exceptions.InvalidRequest;
import main.exceptions.MenuOptionNotFoundException;
import main.models.companies.Menu;
import main.models.companies.Option;
import main.models.users.UserSession;
import main.repositories.MenuRepo;
import main.service.interfaces.custmerSide.CustomerServiceInterface;
import main.utils.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerServiceInterface{
    @Autowired
    MenuRepo menuRepo;

    @Autowired
    private UserSessionStore userSessionStore;

    @Override
    public FetchMenuResponse fetchMainMenu(FetchMenuRequest fetchMenuRequest) {
        try {
            validateRequest(fetchMenuRequest);
            UserSession userSession = userSessionStore.getSession(fetchMenuRequest.getSessionId());

            if ("99".equals(fetchMenuRequest.getResponse())) {
                userSession.setCurrentPage(userSession.getCurrentPage() + 1);
            } else if ("88".equals(fetchMenuRequest.getResponse()) && userSession.getCurrentPage() > 1) {
                userSession.setCurrentPage(userSession.getCurrentPage() - 1);
            }

            Menu mainMenu = fetchMenu(fetchMenuRequest.getSubCode());
            List<Option> options = mainMenu.getOptions();
            int pageSize = 5;
            int totalOptions = options.size();
            int startIndex = (userSession.getCurrentPage() - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalOptions);

            if (startIndex >= totalOptions) {
                return new FetchMenuResponse("No more options available.", false);
            }

            String menuText = buildMenuResponse(options, startIndex, endIndex, userSession.getCurrentPage(), totalOptions);
            userSessionStore.saveSession(userSession);
            return new FetchMenuResponse(menuText, true);

        } catch (MenuOptionNotFoundException e) {
            return new FetchMenuResponse("Error: " + e.getMessage(), false);
        } catch (Exception e) {
            return new FetchMenuResponse("An unexpected error occurred. Please try again.", false);
        }
    }

    @Override
    public FetchMenuResponse fetchMenuFrmCompany(FetchMenuRequest fetchMenuRequest) {
        return null;
    }




    private void validateRequest(FetchMenuRequest request) {
        if (request.getSubCode() == null || request.getSessionId() == null) {
            throw new InvalidRequest("Invalid request. Please try again.");
        }
    }

    private Menu fetchMenu(String subCode) {
        return menuRepo.findById(subCode)
                .orElseThrow(() -> new MenuOptionNotFoundException("No menu available for this company"));
    }

    private int getUpdatedPage(FetchMenuRequest request, int totalOptions, int pageSize) {
        int currentPage = request.getPage() != null ? request.getPage() : 1;

        if ("99".equals(request.getResponse()) && (currentPage * pageSize < totalOptions)) {
            currentPage++;
        } else if ("88".equals(request.getResponse()) && currentPage > 1) {
            currentPage--;
        }
        return currentPage;
    }

    private String buildMenuResponse(List<Option> options, int startIndex, int endIndex, int currentPage, int totalOptions) {
        StringBuilder menuDetails = new StringBuilder(" Select an option:\n");

        for (int i = startIndex; i < endIndex; i++) {
            menuDetails.append((i - startIndex) + 1).append(". ")
                    .append(options.get(i).getTitle()).append("\n");
        }

        if (endIndex < totalOptions) {
            menuDetails.append("\n99. ➡ Next Page\n");
        }
        if (currentPage > 1) {
            menuDetails.append("88. ⬅ Previous Page\n");
        }

        return menuDetails.toString();
    }
}
