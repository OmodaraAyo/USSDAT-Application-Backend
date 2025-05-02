package main.controllers.companies;

import main.dtos.requests.companyFaceRequest.CreateOptionRequest;
import main.dtos.requests.companyFaceRequest.MenuOptionRequest;
import main.dtos.requests.companyFaceRequest.UpdateOptionRequest;
import main.dtos.responses.companyFaceResponse.*;
import main.service.interfaces.MenuService;
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

    @PostMapping("/{company_id}/addOption")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CreatedOptionResponse>> addNewOption(@PathVariable("company_id") String company_id, CreateOptionRequest createOptionRequest){
        CreatedOptionResponse createdOptionResponse = menuService.addNewOption(company_id,createOptionRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", createdOptionResponse));
    }

    @GetMapping("/{company_id}/options/title")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> getMenuOptionByTitle(@PathVariable("company_id") String companyId, @RequestParam String title){
        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionByTitle(companyId, title);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", menuOptionResponse));
    }

    @GetMapping("/{company_id}/options/{option_id}")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> getMenuOptionById(@PathVariable("company_id") String companyId, @PathVariable String option_id){
        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionById(companyId, option_id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", menuOptionResponse));
    }

    @DeleteMapping("/{company_id}/options/{option_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeleteMenuOptionResponse>> deleteMenuOptionById(@PathVariable("company_id") String company_id, @PathVariable("option_id") String option_id){
        DeleteMenuOptionResponse deletedMenuOptionResponse = menuService.deleteMenuOptionById(company_id,option_id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", deletedMenuOptionResponse));
    }

    @PatchMapping("/{company_id}/options/{option_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UpdateOptionResponse>> updateMenuOptionById(@PathVariable String company_id, @PathVariable String option_id, UpdateOptionRequest updateOptionRequest){
        UpdateOptionResponse updateOptionResponse = menuService.updateMenuOption(company_id, option_id, updateOptionRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", updateOptionResponse));
    }

    @GetMapping("/{company_id}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyMenuOptionsResponse>> getCompanyAllOptions(@PathVariable("company_id") String company_id){
        CompanyMenuOptionsResponse companyMenuOptionsResponse = menuService.getMenuOptionsForCompany(company_id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", companyMenuOptionsResponse));
    }
}
