package main.service.implementations.customerSide;


import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyApiRequest;
import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.requests.customerFaceRequest.UserInteractionRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyApiResponse;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;
import main.exceptions.CustomUssdException;
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
        String sessionId = request.getSessionId();
        UserSession userSession = userSessionStore.getSession(sessionId);
        if (userSession == null) {
            companyResponse.setMessage("END Session not found");
            companyResponse.setIsEnd(true);
            return companyResponse;
        }

        // Ensure proper prefix if not provided
        if (!companyResponse.getMessage().startsWith("CON ") && !companyResponse.getMessage().startsWith("END ")) {
            if (companyResponse.getIsEnd()) {
                companyResponse.setMessage("END " + companyResponse.getMessage());
            } else {
                companyResponse.setMessage("CON " + companyResponse.getMessage());
            }
        }

        // Handle options if provided
        if (companyResponse.getOptions() != null && !companyResponse.getOptions().isEmpty()) {
            StringBuilder menuText = new StringBuilder(companyResponse.getMessage() + "\n");
            for (int i = 0; i < companyResponse.getOptions().size(); i++) {
                menuText.append(i + 1).append(". ").append(companyResponse.getOptions().get(i).getTitle()).append("\n");
            }
            menuText.append("0. Quit");
            companyResponse.setMessage(menuText.toString());
        }

        // Update session context if provided by company (assuming context is in companyResponse)
        if (companyResponse.getMessage().contains("context:")) {
            String[] parts = companyResponse.getMessage().split("context:");
            if (parts.length > 1) {
                userSession.setContext(parts[1].trim().split("\\s")[0]); // Extract context (simplified)
            }
        }

        if (companyResponse.getIsEnd()) {
            userSessionStore.removeSession(sessionId);
        }

        userSessionStore.saveSession(userSession);
        return companyResponse;
    }
    @Override
    public UserInteractionResponse processUserInput(UserInteractionRequest request) throws CustomUssdException {
        UserInteractionResponse response = new UserInteractionResponse();
        String sessionId = request.getSessionId();
        String serviceCode = request.getServiceCode();
        String text = request.getText();
        String phoneNumber = request.getPhoneNumber();

        System.out.println("Debug - ServiceCode: " + serviceCode + ", Text: " + text + ", PhoneNumber: " + phoneNumber);

        UserSession userSession = userSessionStore.getSession(sessionId);
        if (userSession == null) {
            userSession = new UserSession();
            userSession.setSessionId(sessionId);
            userSession.setPhoneNumber(phoneNumber);
            System.out.println("Debug - New session created for sessionId: " + sessionId);
        } else {
            System.out.println("Debug - Existing session found, SubCode: " + userSession.getSubCode() + ", Context: " + userSession.getContext());
        }

        String cleanedServiceCode = serviceCode.replace("#", "").replace("*", " ");
        String[] serviceCodeParts = cleanedServiceCode.trim().split("\\s+");
        String subCode = serviceCodeParts.length > 1 ? serviceCodeParts[1] : null;
        String storedSubCode = userSession.getSubCode();

        System.out.println("Debug - CleanedServiceCode: " + cleanedServiceCode + ", SubCode: " + subCode + ", StoredSubCode: " + storedSubCode);

        // Handle base USSD (*384#) to prompt for subcode
        if (serviceCode.equals("*384#") && (subCode == null || subCode.trim().isEmpty()) && (storedSubCode == null || storedSubCode.trim().isEmpty()) && text.isEmpty()) {
            System.out.println("Debug - Base USSD (*384#) matched, returning prompt");
            response.setMessage("CON Enter company subcode:");
            response.setIsEnd(false);
            return response;
        }

        // If serviceCode is empty and text is provided, parse text to extract subcode
        if (serviceCode.isEmpty() && !text.isEmpty() && storedSubCode == null) {
            // Check if text contains a full USSD code like *384*104#
            if (text.startsWith("*384*") && text.endsWith("#")) {
                String cleanedText = text.replace("#", "").replace("*", " ");
                String[] textParts = cleanedText.trim().split("\\s+");
                if (textParts.length > 1 && textParts[0].equals("384")) {
                    subCode = textParts[1]; // Extract subcode (e.g., 104)
                    System.out.println("Debug - Extracted subcode from text: " + subCode);
                } else {
                    throw new CustomUssdException("Invalid USSD format. Expected *384*<subcode>#.", true);
                }
            } else {
                // Treat text as the subcode directly (e.g., 104)
                subCode = text.trim();
                System.out.println("Debug - Treating text as subcode input: " + subCode);
            }
            userSession.setSubCode(subCode);
            userSession.setContext("main menu");
        } else if (!serviceCode.isEmpty() && !serviceCode.equals("*384#") && subCode == null && (storedSubCode == null || storedSubCode.trim().isEmpty()) && text.isEmpty()) {
            System.out.println("Debug - Invalid USSD code detected");
            throw new CustomUssdException("Invalid USSD code. Use *384#.", true);
        }

        // Set subcode only if explicitly provided via serviceCode with subcode
        if (storedSubCode == null) {
            if (subCode != null && !subCode.trim().isEmpty()) {
                userSession.setSubCode(subCode);
                userSession.setContext("main menu");
                System.out.println("Debug - SubCode set from serviceCode: " + subCode);
            }
        } else {
            subCode = storedSubCode;
            System.out.println("Debug - Using stored SubCode: " + subCode);
        }

        if (subCode == null) {
            System.out.println("Debug - No valid subCode, throwing exception");
            throw new CustomUssdException("Invalid subcode. Please try again.", true);
        }

        // Handle quit
        if ("0".equals(text)) {
            System.out.println("Debug - Quit requested");
            userSessionStore.removeSession(sessionId);
            response.setMessage("END Goodbye!");
            response.setIsEnd(true);
            return response;
        }

        // Check user intent based on session context
        if ("main menu".equals(userSession.getContext())) {
            System.out.println("Debug - Entering main menu logic for subCode: " + subCode);
            FetchMenuFromCompanyDBRequest dbRequest = new FetchMenuFromCompanyDBRequest();
            dbRequest.setSessionId(sessionId);
            dbRequest.setSubCode(subCode);
            FetchMenuFromCompanyDBResponse dbResponse;
            try {
                dbResponse = fetchMenuService.fetchMainMenu(dbRequest);
            } catch (Exception e) {
                System.out.println("Debug - Fetch menu failed: " + e.getMessage());
                throw new CustomUssdException("Failed to fetch main menu: " + e.getMessage(), true);
            }

            response.setMessage("CON " + dbResponse.getMessage() + "\n");
            if (dbResponse.getOptions() != null) {
                response.setOptions(dbResponse.getOptions());
                for (int i = 0; i < dbResponse.getOptions().size(); i++) {
                    response.setMessage(response.getMessage() + (i + 1) + ". " + dbResponse.getOptions().get(i).getTitle() + "\n");
                }
                response.setMessage(response.getMessage() + "0. Quit");
            }
            response.setIsEnd(false);
            userSession.setContext("company_api");
        } else {
            System.out.println("Debug - Entering company API logic for subCode: " + subCode + ", Text: " + text);
            FetchMenuFromCompanyApiRequest apiRequest = new FetchMenuFromCompanyApiRequest();
            apiRequest.setSessionId(sessionId);
            apiRequest.setSubCode(subCode);
            apiRequest.setResponse(text);
            FetchMenuFromCompanyApiResponse apiResponse;
            try {
                apiResponse = fetchMenuService.fetchMenuFromCompanyApi(apiRequest);
            } catch (Exception e) {
                System.out.println("Debug - Fetch API failed: " + e.getMessage());
                throw new CustomUssdException("Failed to fetch company menu: " + e.getMessage(), true);
            }

            response.setMessage("CON " + apiResponse.getMessage());
            response.setIsEnd(false);
            userSession.setContext(apiResponse.getContext());
        }

        userSessionStore.saveSession(userSession);
        return response;
    }
}