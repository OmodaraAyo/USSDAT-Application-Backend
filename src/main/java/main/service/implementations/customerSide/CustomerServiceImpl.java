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
public class CustomerServiceImpl implements CustomerServiceInterface {
    @Autowired
    MenuRepo menuRepo;

    @Autowired
    private UserSessionStore userSessionStore;

    @Override
    public FetchMenuResponse fetchMainMenu(FetchMenuRequest fetchMenuRequest) {
        try {
            validateRequest(fetchMenuRequest);
            FetchMenuResponse response = new FetchMenuResponse();

            UserSession userSession = userSessionStore.getSession(fetchMenuRequest.getSessionId());

            int updatedPage = getUpdatedPage(fetchMenuRequest, userSession.getCurrentPage());
            userSession.setCurrentPage(updatedPage);


            Menu mainMenu = fetchMenu(fetchMenuRequest.getSubCode());
            List<Option> options = mainMenu.getOptions();
            int pageSize = 5;
            int totalOptions = options.size();
            int startIndex = (userSession.getCurrentPage() - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalOptions);

            if (startIndex >= totalOptions) {
                response.setMessage("No more options available.");
                response.setHasMore(false);
                response.setIsSuccess(false);
                return response;
            }

            String menuText = buildMenuResponse(options, startIndex, endIndex, userSession.getCurrentPage(), totalOptions);
            response.setMessage(menuText);
            response.setHasMore(endIndex < totalOptions);
            response.setPage(userSession.getCurrentPage());
            response.setIsSuccess(true);

            userSessionStore.saveSession(userSession);

            return response;

        } catch (InvalidRequest e) {
            return createErrorResponse("Error: " + e.getMessage());
        } catch (MenuOptionNotFoundException e) {
            return createErrorResponse("Error: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred. Please try again.");
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

    private int getUpdatedPage(FetchMenuRequest request, int currentPage) {
        if ("99".equals(request.getResponse())) {
            return currentPage + 1;
        } else if ("88".equals(request.getResponse()) && currentPage > 1) {
            return currentPage - 1;
        }
        return currentPage;
    }

    private String buildMenuResponse(List<Option> options, int startIndex, int endIndex, int currentPage, int totalOptions) {
        StringBuilder menuDetails = new StringBuilder("Select an option:\n");

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

    private FetchMenuResponse createErrorResponse(String errorMessage) {
        FetchMenuResponse response = new FetchMenuResponse();
        response.setMessage(errorMessage);
        response.setIsSuccess(false);
        response.setHasMore(false);
        return response;
    }
}