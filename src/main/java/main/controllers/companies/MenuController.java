package main.controllers.companies;

import main.dtos.responses.companyFaceResponse.ApiResponse;
import main.service.interfaces.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
public class MenuController {

    @Autowired
    private MenuService menuService;

//    @PostMapping
//    public ResponseEntity<ApiResponse<>>
}
