package main.controllers.users;

import main.dtos.requests.customerFaceRequest.UserInteractionRequest;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;
import main.exceptions.CustomUssdException;
import main.service.implementations.customerSide.DisplayAndEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/userResponse")
public class CollectUserInputController {

    @Autowired
    private DisplayAndEntryService displayAndEntryService;

    @PostMapping("/collect")
    public ResponseEntity<String> collectUserInput(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam("phoneNumber") String phoneNumber) {

        try {
            UserInteractionRequest request = new UserInteractionRequest();
            request.setSessionId(sessionId);
            request.setServiceCode(serviceCode);
            request.setText(text);
            request.setPhoneNumber(phoneNumber);

            UserInteractionResponse response = displayAndEntryService.processUserInput(request);
            return ResponseEntity.ok(response.getMessage());
        } catch (CustomUssdException e) {
            return ResponseEntity.ok(e.isEnd() ? "END " + e.getMessage() : "CON " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("END An unexpected error occurred: " + e.getMessage());
        }
    }

}
