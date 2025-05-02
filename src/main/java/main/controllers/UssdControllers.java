package main.controllers;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.service.interfaces.custmerSide.CustomerServiceInterface;
import main.utils.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ussd")
public class UssdControllers {

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private UserSessionStore userSessionStore;

    @PostMapping("/callback")
    public ResponseEntity<String> handleUssdCallback(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam(value = "text", defaultValue = "") String text) {

        try {
            // Clean the serviceCode and split to detect subcode
            String cleanedServiceCode = serviceCode.replace("#", "").replace("*", " ");
            String[] serviceCodeParts = cleanedServiceCode.trim().split("\\s+");
            String subCode = serviceCodeParts.length > 1 ? serviceCodeParts[1] : null;

            // Check if user has a session with a subcode already
            String storedSubCode = userSessionStore.getSession(sessionId) != null ?
                    userSessionStore.getSession(sessionId).getSubCode() : null;

            // Scenario 1: No subcode in serviceCode, no session subcode, and no text (initial base code request)
            if (subCode == null && storedSubCode == null && text.isEmpty()) {
                return ResponseEntity.ok("CON Enter company subcode:");
            }

            // Scenario 2: User entered subcode after prompt, or subcode was in serviceCode, or session has subcode
            if (storedSubCode == null && subCode == null && !text.isEmpty()) {
                // User entered subcode via text
                subCode = text.trim();
            } else if (storedSubCode != null) {
                // Use subcode from session
                subCode = storedSubCode;
            }

            // Validate subCode
            if (subCode == null) {
                return ResponseEntity.ok("END Invalid subcode. Please try again.");
            }

            // Create FetchMenuRequest
            FetchMenuRequest request = new FetchMenuRequest();
            request.setSessionId(sessionId);
            request.setSubCode(subCode);
            request.setContext(text);
            request.setResponse(text.isEmpty() ? null : text);

            // Call the service to fetch the menu
            FetchMenuResponse response = customerService.fetchMainMenu(request);
            return ResponseEntity.ok(response.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("END An error occurred. Please try again later.");
        }
    }
}