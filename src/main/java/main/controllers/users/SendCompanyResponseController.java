package main.controllers.users;

import main.dtos.requests.customerFaceRequest.UserInteractionRequest;
import main.dtos.responses.customerFaceResponse.UserInteractionResponse;
import main.service.implementations.customerSide.DisplayAndEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/companyResponse")
public class SendCompanyResponseController {

    @Autowired
    private DisplayAndEntryService displayAndEntryService;

    @PostMapping("/respond")
    public ResponseEntity<String> sendCompanyResponseToUser(
            @RequestBody UserInteractionRequest userRequest,
            @RequestBody UserInteractionResponse companyResponse) {

        try {
            UserInteractionResponse response = displayAndEntryService.processCompanyResponse(userRequest, companyResponse);
            return ResponseEntity.ok(response.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("END An error occurred: " + e.getMessage());
        }
    }
}
