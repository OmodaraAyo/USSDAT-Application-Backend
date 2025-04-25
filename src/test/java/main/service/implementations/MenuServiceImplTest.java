package main.service.implementations;

import main.dtos.requests.*;
import main.dtos.responses.*;
import main.exceptions.EmptyItemException;
import main.exceptions.MenuOptionNotFoundException;
import main.exceptions.ValidatorException;
import main.models.security.CompanyPrincipal;
import main.models.users.Company;
import main.models.utils.UssdCounter;
import main.repositories.MenuRepo;
import main.service.interfaces.CompanyService;
import main.service.interfaces.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MenuServiceImplTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CompanyService companyService;


    @BeforeEach
    public void startAllWithThis(){
        companyService.deleteAllCompanies();
        menuRepo.deleteAll();
        Query query = new Query(Criteria.where("_id").is("ussdShortCode"));
        Update update = new Update().set("ussdCode", 1);
        mongoTemplate.upsert(query, update, UssdCounter.class);

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyName("Unius");
        companyRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
        companyRequest.setCompanyPhone(List.of("09012345678"));
        companyRequest.setCategory("finance");
        companyRequest.setBusinessRegistrationNumber("123456789");
        companyService.registerCompany(companyRequest);
    }

    @Test
    public void shouldNotAddMenuOptionWhenCompanyIsNotAuthenticated(){
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> {
            menuService.addNewOption(new CreateOptionRequest("register"));
        });
        assertEquals("Authentication required.", exception.getMessage());
    }

    @Test
    public void menuOptions_shouldBeEmptyByDefault(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company refreshCompany1 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1.isLoggedIn());
        assertTrue(refreshCompany1.getMenu().getOptions().isEmpty());
    }


    @Test
    public void givenAuthenticatedCompanyWithEmptyMenuOption_whenOptionIsAdded_thenMenuIsNotEmpty(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company refreshCompany1 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1.isLoggedIn());
        assertTrue(refreshCompany1.getMenu().getOptions().isEmpty());

        CreatedOptionResponse savedOption = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedOption.getResponse());
        Company refreshCompany2 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany2.isLoggedIn());
        assertFalse(refreshCompany2.getMenu().getOptions().isEmpty());
        assertEquals("register", refreshCompany2.getMenu().getOptions().get(0).getTitle());
        System.out.println("Saved option: "+ refreshCompany2.getMenu().getOptions().get(0).getTitle());
    }

    @Test
    public void whenMultipleCompaniesAddOptionsSimultaneously_thenMenusAreUpdatedCorrectly(){

        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company refreshCompany1 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1.isLoggedIn());
        assertTrue(refreshCompany1.getMenu().getOptions().isEmpty());

        CreatedOptionResponse savedOption = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedOption.getResponse());
        Company refreshCompany1II = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1II.isLoggedIn());
        assertFalse(refreshCompany1II.getMenu().getOptions().isEmpty());
        assertEquals("register", refreshCompany1II.getMenu().getOptions().get(0).getTitle());
        assertThat(refreshCompany1II.getMenu().getOptions(), hasSize(1));


        CompanyRequest companyRequest2 = new CompanyRequest();
        companyRequest2.setCompanyName("Sui");
        companyRequest2.setCompanyEmail("example@gmail.com");
        companyRequest2.setCompanyPhone(List.of("08022211150"));
        companyRequest2.setCategory("ECOMMERCE");
        companyRequest2.setBusinessRegistrationNumber("987654321");
        CompanyResponse companyResponse2 = companyService.registerCompany(companyRequest2);
        String secondRegisteredCompanyPassword = CompanyServiceImpl.genPass;
        System.out.println("second registration: "+secondRegisteredCompanyPassword);
        assertTrue(companyResponse2.isSuccess());

        Company company2 = companyService.getByCompanyEmail("example@gmail.com");
        assertFalse(company2.isLoggedIn());

        LoginResponse loginResponse2 = companyService.signIn(new LoginRequest("example@gmail.com", secondRegisteredCompanyPassword));
        assertTrue(loginResponse2.getIsLoggedIn());
        CompanyPrincipal principal2 = new CompanyPrincipal(company2);
        UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(principal2, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth2);
        Company refreshCompany2 = companyService.getByCompanyEmail("example@gmail.com");
        assertTrue(refreshCompany2.isLoggedIn());
        assertTrue(refreshCompany2.getMenu().getOptions().isEmpty());


        //add first menu for company 2
        CreatedOptionResponse addFirstMenuOptionFoeCompany2 = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", addFirstMenuOptionFoeCompany2.getResponse());
        Company refreshCompany2II = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany2II.isLoggedIn());
        assertFalse(refreshCompany2II.getMenu().getOptions().isEmpty());
        assertThat(refreshCompany2II.getMenu().getOptions(), hasSize(1));
        assertEquals("register", refreshCompany2II.getMenu().getOptions().get(0).getTitle());
    }

    @Test
    public void givenAuthenticatedCompany_whenAddingEmptyOrNullOption_thenThrowValidationException(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company refreshCompany1 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1.isLoggedIn());
        assertTrue(refreshCompany1.getMenu().getOptions().isEmpty());

        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            menuService.addNewOption(new CreateOptionRequest("   "));
        });
        assertEquals("Please enter a menu title.", exception.getMessage());
    }

    @Test
    public void shouldNotSaveDuplicateMenuItems(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company refreshCompany1 = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1.isLoggedIn());
        assertTrue(refreshCompany1.getMenu().getOptions().isEmpty());

        CreatedOptionResponse savedOption = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedOption.getResponse());
        Company refreshCompany1II = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany1II.isLoggedIn());
        assertFalse(refreshCompany1II.getMenu().getOptions().isEmpty());
        assertEquals("register", refreshCompany1II.getMenu().getOptions().get(0).getTitle());
        assertThat(refreshCompany1II.getMenu().getOptions(), hasSize(1));

        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            menuService.addNewOption(new CreateOptionRequest("register"));
        });

        assertEquals("Oops! A menu titled 'Register' already exists. Please try another name.", exception.getMessage());

    }

    @Test
    public void givenCurrentUserHasMenus_whenFindingByTitle_thenReturnMatchingMenu(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        //add first menu for company 3
        CreatedOptionResponse savedMenu3 = menuService.addNewOption(new CreateOptionRequest("Transfer"));
        assertEquals("Awesome! Your menu is now live.", savedMenu3.getResponse());

        MenuOptionResponse response = menuService.getMenuOptionByTitle(new MenuOptionRequest("transfer"));
        assertTrue(response.isSuccess());
        assertEquals("Transfer".toLowerCase(), response.getTitle().toLowerCase());
    }

    @Test
    public void givenCurrentUserHasNoMenus_whenFindingByTitle_thenThrowEmptyItemException(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        EmptyItemException exception = assertThrows(EmptyItemException.class, () -> {
            menuService.getMenuOptionByTitle(new MenuOptionRequest("transfer"));
        });

        assertEquals("Looks like there’s nothing here yet. Add a menu to get started!", exception.getMessage());
    }

    @Test
    public void givenCurrentUserHasMenus_whenSearchingForInvalidTitle_thenThrowMenuNotFoundException(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        MenuOptionNotFoundException exception = assertThrows(MenuOptionNotFoundException.class, () -> {
            menuService.getMenuOptionByTitle(new MenuOptionRequest("transfer"));
        });

        assertEquals("No menu option found with the title \"transfer\".", exception.getMessage());
    }


    @Test
    public void shouldNotAllowMenuSearchWhenCompanyIsNotAuthenticated(){
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> {
            menuService.getMenuOptionByTitle(new MenuOptionRequest("transfer"));
        });
        assertEquals("Authentication required.", exception.getMessage());
    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenMenuIsDeletedById_thenReturnSuccessResponse(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        DeleteMenuOptionResponse deletedMenuResponse = menuService.deleteMenuOptionById(savedMenu2.getOptionId());
        System.out.println("Menu OptionId: "+savedMenu2.getOptionId());
        assertTrue(deletedMenuResponse.isSuccess());
        assertEquals("Deleted successfully.",deletedMenuResponse.getMessage());
    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenFindingMenuById_thenReturnSuccessResponse(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionById(new FindMenuOptionByIdRequest(savedMenu2.getOptionId()));
        assertTrue(menuOptionResponse.isSuccess());
        assertEquals("Check balance".toLowerCase(), menuOptionResponse.getTitle().toLowerCase());
    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenFindingMenuWIthInvalidId_thenThrowMenuNotFoundException(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        MenuOptionNotFoundException exception = assertThrows(MenuOptionNotFoundException.class, ()-> {
            menuService.getMenuOptionById(new FindMenuOptionByIdRequest("123345669"));
        });

        assertEquals("No menu option found with this id: \"123345669\".", exception.getMessage());
    }

    @Test
    public void givenAuthenticatedUserWithMenu_canUpdateMenuOption_thenReturnSuccessResponse(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        Company refreshFirstCompany2 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertEquals("register".toLowerCase(), refreshFirstCompany2.getMenu().getOptions().get(0).getTitle().toLowerCase());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        UpdateOptionResponse updatedMenuOption = menuService.updateMenuOption(new UpdateOptionRequest(savedMenu.getOptionId(), "Transfer"));
        assertTrue(updatedMenuOption.isSuccess());

        Company refreshFirstCompany3 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertEquals("Transfer".toLowerCase(), refreshFirstCompany3.getMenu().getOptions().get(0).getTitle().toLowerCase());
    }

    @Test
    public void givenAuthenticatedCompany_whenFindMenuOptions_thenReturnCompanyMenuOptions(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshFirstCompany1 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(refreshFirstCompany1.isLoggedIn());
        assertTrue(refreshFirstCompany1.getMenu().getOptions().isEmpty());

        //add first menu for company 1
        CreatedOptionResponse savedMenu = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        //add first menu for company 2
        CreatedOptionResponse savedMenu2 = menuService.addNewOption(new CreateOptionRequest("Check balance"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        //add first menu for company 3
        CreatedOptionResponse savedMenu3 = menuService.addNewOption(new CreateOptionRequest("Transfer"));
        assertEquals("Awesome! Your menu is now live.", savedMenu3.getResponse());

        Company refreshFirstCompany2 =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshFirstCompany2.isLoggedIn());
        assertFalse(refreshFirstCompany2.getMenu().getOptions().isEmpty());

        CompanyMenuOptionResponse companyMenu = menuService.getMenuOptionsForCompany(new CompanyMenuOptionRequest(refreshFirstCompany2.getCompanyId()));
        assertEquals("register".toLowerCase(), companyMenu.getMenuOptions().get(0).toLowerCase());
        assertEquals("Check balance".toLowerCase(), companyMenu.getMenuOptions().get(1).toLowerCase());
        assertEquals("Transfer".toLowerCase(), companyMenu.getMenuOptions().get(2).toLowerCase());
        assertThat(refreshFirstCompany2.getMenu().getOptions(), hasSize(3));
        System.out.println("Company 1:  "+companyMenu.getMenuOptions());

        //company 2
        CompanyRequest companyRequest2 = new CompanyRequest();
        companyRequest2.setCompanyName("Sui");
        companyRequest2.setCompanyEmail("example@gmail.com");
        companyRequest2.setCompanyPhone(List.of("08022211150"));
        companyRequest2.setCategory("ECOMMERCE");
        companyRequest2.setBusinessRegistrationNumber("987654321");
        CompanyResponse companyResponse2 = companyService.registerCompany(companyRequest2);
        String secondRegisteredCompanyPassword = CompanyServiceImpl.genPass;
        System.out.println("second registration: "+secondRegisteredCompanyPassword);
        assertTrue(companyResponse2.isSuccess());

        Company company2 = companyService.getByCompanyEmail("example@gmail.com");
        assertFalse(company2.isLoggedIn());

        LoginResponse loginResponse2 = companyService.signIn(new LoginRequest("example@gmail.com", secondRegisteredCompanyPassword));
        assertTrue(loginResponse2.getIsLoggedIn());
        CompanyPrincipal principal2 = new CompanyPrincipal(company2);
        UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(principal2, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth2);
        Company refreshCompany2 = companyService.getByCompanyEmail("example@gmail.com");
        assertTrue(refreshCompany2.isLoggedIn());
        assertTrue(refreshCompany2.getMenu().getOptions().isEmpty());


        //add first menu for company 2
        CreatedOptionResponse addFirstMenuOptionForCompany2 = menuService.addNewOption(new CreateOptionRequest("register"));
        assertEquals("Awesome! Your menu is now live.", addFirstMenuOptionForCompany2.getResponse());

        //add second menu for company 2
        CreatedOptionResponse addSecondMenuOptionForCompany2 = menuService.addNewOption(new CreateOptionRequest("Buy data"));
        assertEquals("Awesome! Your menu is now live.", addSecondMenuOptionForCompany2.getResponse());


        Company refreshSecondCompany2II = companyService.getByCompanyEmail("example@gmail.com");
        assertTrue(refreshSecondCompany2II.isLoggedIn());
        assertFalse(refreshSecondCompany2II.getMenu().getOptions().isEmpty());

        CompanyMenuOptionResponse secondCompanyMenu = menuService.getMenuOptionsForCompany(new CompanyMenuOptionRequest(refreshSecondCompany2II.getCompanyId()));
        assertEquals("register".toLowerCase(), secondCompanyMenu.getMenuOptions().get(0).toLowerCase());
        assertEquals("Buy data".toLowerCase(), secondCompanyMenu.getMenuOptions().get(1).toLowerCase());
        System.out.println("Second company: "+secondCompanyMenu.getMenuOptions());
        assertThat(refreshSecondCompany2II.getMenu().getOptions(), hasSize(2));
    }
}