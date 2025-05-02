package main.controllers;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.service.interfaces.custmerSide.CustomerServiceInterface;
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

    @PostMapping("/callback")
    public ResponseEntity<String> handleUssdCallback(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam("text") String text) {
        FetchMenuRequest request = new FetchMenuRequest();
        request.setSessionId(sessionId);
        request.setSubCode(serviceCode.replace("*", "").replace("#", ""));
        request.setResponse(text.isEmpty() ? null : text);

        try {
            FetchMenuResponse response = customerService.fetchMainMenu(request);
            return ResponseEntity.ok(response.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("END An error occurred. Please try again later.");
        }
    }
}
