package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.exceptions.InvalidRequest;
import main.exceptions.MenuOptionNotFoundException;
import main.models.companies.Company;
import main.models.companies.Menu;
import main.models.companies.Option;
import main.models.users.UserSession;
import main.repositories.CompanyRepo;
import main.repositories.MenuRepo;
import main.service.interfaces.custmerSide.CustomerServiceInterface;
import main.utils.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerServiceInterface {

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private UserSessionStore userSessionStore;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public FetchMenuResponse fetchMainMenu(FetchMenuRequest fetchMenuRequest) {
        try {
            validateRequest(fetchMenuRequest);
            FetchMenuResponse response = new FetchMenuResponse();

            // Get or create user session
            UserSession userSession = userSessionStore.getSession(fetchMenuRequest.getSessionId());
            if (userSession.getSubCode() == null || userSession.getSubCode().isEmpty()) {
                userSession.setSubCode(fetchMenuRequest.getSubCode());
                userSession.setContext("main");
            }

            // Handle user response (e.g., "99" for Next, "88" for Previous, or "0" to quit)
            if (fetchMenuRequest.getResponse() != null) {
                if ("0".equals(fetchMenuRequest.getResponse())) {
                    userSessionStore.removeSession(fetchMenuRequest.getSessionId());
                    response.setMessage("END Goodbye!");
                    response.setHasMore(false);
                    response.setIsSuccess(true);
                    return response;
                }
                int updatedPage = getUpdatedPage(fetchMenuRequest, userSession.getCurrentPage());
                userSession.setCurrentPage(updatedPage);
                userSession.setLastResponse(fetchMenuRequest.getResponse());
            }

            // If context is "main", fetch the main menu
            if ("main".equals(userSession.getContext())) {
                Company company = companyRepo.findBySubCode(fetchMenuRequest.getSubCode());
                if (company == null) {
                    throw new MenuOptionNotFoundException("Company not found for subCode: " + fetchMenuRequest.getSubCode());
                }
                Menu mainMenu = company.getMenu() != null ? company.getMenu() : fetchMenu(fetchMenuRequest.getSubCode());
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
                response.setContext("main");

                userSessionStore.saveSession(userSession);
            } else {
                // If context is not "main", fetch from company
                return fetchMenuFrmCompany(fetchMenuRequest);
            }

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
        try {
            validateRequest(fetchMenuRequest);
            FetchMenuResponse response = new FetchMenuResponse();

            // Get user session
            UserSession userSession = userSessionStore.getSession(fetchMenuRequest.getSessionId());
            if (userSession.getSubCode() == null || userSession.getSubCode().isEmpty()) {
                userSession.setSubCode(fetchMenuRequest.getSubCode());
                userSession.setContext("main");
            }

            // If user wants to quit
            if ("0".equals(fetchMenuRequest.getResponse())) {
                userSessionStore.removeSession(fetchMenuRequest.getSessionId());
                response.setMessage("END Goodbye!");
                response.setHasMore(false);
                response.setIsSuccess(true);
                return response;
            }

            // Fetch company to get baseUrl
            Company company = companyRepo.findBySubCode(fetchMenuRequest.getSubCode());
            if (company == null) {
                throw new MenuOptionNotFoundException("Company not found for subCode: " + fetchMenuRequest.getSubCode());
            }

            // Send user response to company endpoint
            String url = company.getBaseUrl() + "/menu";
            Map<String, String> requestBody = Map.of(
                    "context", userSession.getContext(),
                    "response", fetchMenuRequest.getResponse() != null ? fetchMenuRequest.getResponse() : userSession.getLastResponse()
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> companyResponse = restTemplate.postForObject(url, requestBody, Map.class);

            if (companyResponse == null) {
                throw new RuntimeException("Failed to fetch response from company");
            }

            // Process company response
            String message = (String) companyResponse.get("message");
            List<Map<String, String>> companyOptions = (List<Map<String, String>>) companyResponse.get("options");
            Boolean isEnd = (Boolean) companyResponse.get("isEnd");

            // If session should end
            if (Boolean.TRUE.equals(isEnd)) {
                userSessionStore.removeSession(fetchMenuRequest.getSessionId());
                response.setMessage(message);
                response.setHasMore(false);
                response.setIsSuccess(true);
                return response;
            }

            // Convert company options to our format
            List<Option> options = new ArrayList<>();
            for (int i = 0; i < companyOptions.size(); i++) {
                Map<String, String> companyOption = companyOptions.get(i);
                Option option = new Option();
                option.setTitle(companyOption.get("text"));
                option.setContext(companyOption.get("context"));
                options.add(option);
            }

            // Update session context based on user response
            if (fetchMenuRequest.getResponse() != null && !fetchMenuRequest.getResponse().isEmpty()) {
                try {
                    int selectedIndex = Integer.parseInt(fetchMenuRequest.getResponse()) - 1;
                    if (selectedIndex >= 0 && selectedIndex < options.size()) {
                        userSession.setContext(options.get(selectedIndex).getContext());
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid response
                }
            }

            // Handle pagination
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
            response.setContext(userSession.getContext());

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
        StringBuilder menuDetails = new StringBuilder("CON Select an option:\n");

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
        menuDetails.append("0. Quit");

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