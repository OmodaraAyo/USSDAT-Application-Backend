package main.service.implementations.customerSide;


import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyApiRequest;
import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.requests.customerFaceRequest.UserInteractionRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyApiResponse;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;
import main.models.users.UserSession;
import main.service.interfaces.custmerSide.DisplayAndEntryInterface;
import main.service.interfaces.custmerSide.FetchMenuServiceInterface;
import main.utils.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisplayAndEntryService implements DisplayAndEntryInterface {

    @Autowired
    private FetchMenuServiceInterface fetchMenuService;

    @Autowired
    private UserSessionStore userSessionStore;


    @Override
    public UserInteractionResponse processCompanyResponse(UserInteractionRequest request, UserInteractionResponse companyResponse) {
        UserInteractionResponse response = new UserInteractionResponse();
        String sessionId = request.getSessionId();
        String serviceCode = request.getServiceCode();
        String text = request.getText();

        // Get or create user session
        UserSession userSession = userSessionStore.getSession(sessionId);
        if (userSession == null) {
            userSession = new UserSession();
            userSession.setSessionId(sessionId);
        }

        // Parse serviceCode to detect subcode
        String cleanedServiceCode = serviceCode.replace("#", "").replace("*", " ");
        String[] serviceCodeParts = cleanedServiceCode.trim().split("\\s+");
        String subCode = serviceCodeParts.length > 1 ? serviceCodeParts[1] : null;
        String storedSubCode = userSession.getSubCode();

        // If no subcode, prompt user
        if (subCode == null && storedSubCode == null && text.isEmpty()) {
            response.setMessage("CON Enter company subcode:");
            response.setIsEnd(false);
            return response;
        }

        // Set subcode from text, serviceCode, or session
        if (storedSubCode == null && subCode == null && !text.isEmpty()) {
            subCode = text.trim();
            userSession.setSubCode(subCode);
            userSession.setContext("main menu");
        } else if (storedSubCode != null) {
            subCode = storedSubCode;
        } else if (subCode != null) {
            userSession.setSubCode(subCode);
            userSession.setContext("main menu");
        }

        if (subCode == null) {
            response.setMessage("END Invalid subcode. Please try again.");
            response.setIsEnd(true);
            return response;
        }

        // Handle quit
        if ("0".equals(text)) {
            userSessionStore.removeSession(sessionId);
            response.setMessage("END Goodbye!");
            response.setIsEnd(true);
            return response;
        }

        // Check user intent based on session context
        if ("main menu".equals(userSession.getContext())) {
            // Fetch main menu from DB
            FetchMenuFromCompanyDBRequest dbRequest = new FetchMenuFromCompanyDBRequest();
            dbRequest.setSessionId(sessionId);
            dbRequest.setSubCode(subCode);
            FetchMenuFromCompanyDBResponse dbResponse = fetchMenuService.fetchMainMenu(dbRequest);

            response.setMessage("CON " + dbResponse.getMessage() + "\n");
            if (dbResponse.getOptions() != null) {
                response.setOptions(dbResponse.getOptions());
                for (int i = 0; i < dbResponse.getOptions().size(); i++) {
                    response.setMessage(response.getMessage() + (i + 1) + ". " + dbResponse.getOptions().get(i).getTitle() + "\n");
                }
                response.setMessage(response.getMessage() + "0. Quit");
            }
            response.setIsEnd(false);
            userSession.setContext("company_api"); // Move to next interaction
        } else {
            // Send to company API
            FetchMenuFromCompanyApiRequest apiRequest = new FetchMenuFromCompanyApiRequest();
            apiRequest.setSessionId(sessionId);
            apiRequest.setSubCode(subCode);
            apiRequest.setResponse(text);
            FetchMenuFromCompanyApiResponse apiResponse = fetchMenuService.fetchMenuFromCompanyApi(apiRequest);

            response.setMessage("CON " + apiResponse.getMessage());
            response.setIsEnd(false);
            userSession.setContext(apiResponse.getContext());
        }

        userSessionStore.saveSession(userSession);
        return response;
    }

    @Override
    public UserInteractionResponse processUserInput(UserInteractionRequest request) {
        return null;
    }
}