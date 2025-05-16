package main.controllers.companies;

import jakarta.validation.Valid;
import main.dtos.requests.companyFaceRequest.CreateOptionRequest;
import main.dtos.requests.companyFaceRequest.UpdateOptionRequest;
import main.dtos.responses.companyFaceResponse.*;
import main.service.interfaces.companySide.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/{companyId}/menus/addOption")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CreatedOptionResponse>> addNewOption(@PathVariable("companyId") String companyId, @Valid @RequestBody CreateOptionRequest payload){
        CreatedOptionResponse createdOptionResponse = menuService.addNewOption(companyId,payload);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", createdOptionResponse));
    }

    @GetMapping("/{companyId}/menus/options/title")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> getMenuOptionByTitle(@PathVariable("companyId") String companyId, @RequestParam String title){
        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionByTitle(companyId, title);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", menuOptionResponse));
    }

    @GetMapping("/{companyId}/menus/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> getMenuOptionById(@PathVariable("companyId") String companyId, @PathVariable String optionId){
        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionById(companyId, optionId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", menuOptionResponse));
    }

    @DeleteMapping("/{companyId}/menus/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeleteMenuOptionResponse>> deleteMenuOptionById(@PathVariable("companyId") String companyId, @PathVariable("optionId") String optionId){
        DeleteMenuOptionResponse deletedMenuOptionResponse = menuService.deleteMenuOptionById(companyId,optionId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", deletedMenuOptionResponse));
    }

    @PatchMapping("/{companyId}/menus/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UpdateOptionResponse>> updateMenuOptionById(@PathVariable("companyId") String companyId, @PathVariable("optionId") String optionId, @Valid @RequestBody UpdateOptionRequest payload){
        UpdateOptionResponse updateOptionResponse = menuService.updateMenuOption(companyId, optionId, payload);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", updateOptionResponse));
    }

    @GetMapping("/{companyId}/menus/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyMenuOptionsResponse>> getCompanyAllOptions(@PathVariable("companyId") String companyId){
        CompanyMenuOptionsResponse companyMenuOptionsResponse = menuService.getMenuOptionsForCompany(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", companyMenuOptionsResponse));
    }
}
