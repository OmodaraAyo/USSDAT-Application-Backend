package main.service.implementations;

import main.dtos.requests.CompanyRequest;
import main.dtos.requests.LoginRequest;
import main.dtos.requests.MenuRequest;
import main.dtos.responses.CompanyResponse;
import main.dtos.responses.LoginResponse;
import main.dtos.responses.MenuResponse;
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

import java.util.List;

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

//    private CompanyResponse companyResponse;

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
    public void shouldNotAddNewMenuWhenCompanyIsAuthenticated(){
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> {
            menuService.addNewMenu(new MenuRequest("register"));
        });
        assertEquals("Authentication required.", exception.getMessage());
    }


    @Test
    public void shouldAddMenuWhenCompanyIsAuthenticated(){
        String firstCompanyPassword = CompanyServiceImpl.genPass;
        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com",firstCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");

        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(company.isLoggedIn());
        assertTrue(company.getDefaultMenus().isEmpty());

        MenuResponse savedMenu = menuService.addNewMenu(new MenuRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        assertTrue(company.isLoggedIn());
        Company refreshCompany = companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(refreshCompany.getDefaultMenus().isEmpty());
        assertEquals("register", refreshCompany.getDefaultMenus().get(0).getTitle());
    }

    @Test
    public void shouldAllowAuthenticatedCompaniesToModifyMenu(){
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
        assertTrue(refreshFirstCompany1.getDefaultMenus().isEmpty());

        //add first menu for company 1
        MenuResponse savedMenu = menuService.addNewMenu(new MenuRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        Company refreshFirstCompanyII =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshFirstCompanyII.isLoggedIn());
        assertFalse(refreshFirstCompanyII.getDefaultMenus().isEmpty());
        assertEquals("register", refreshFirstCompanyII.getDefaultMenus().get(0).getTitle());


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

        Company refreshSecondCompany2 = companyService.getByCompanyEmail("example@gmail.com");
        CompanyPrincipal principal2 = new CompanyPrincipal(company2);
        UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(principal2, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth2);
        assertTrue(refreshSecondCompany2.isLoggedIn());
        assertTrue(refreshSecondCompany2.getDefaultMenus().isEmpty());

        //add first menu for company 2
        MenuResponse savedMenu2 = menuService.addNewMenu(new MenuRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());

        Company refreshSecondCompany2II = companyService.getByCompanyEmail("example@gmail.com");
        assertTrue(refreshSecondCompany2II.isLoggedIn());
        assertFalse(refreshSecondCompany2II.getDefaultMenus().isEmpty());
        assertEquals("register", refreshSecondCompany2II.getDefaultMenus().get(0).getTitle());

    }

    @Test
    public void shouldNotSaveDuplicateMenuItems(){
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
        assertTrue(refreshFirstCompany1.getDefaultMenus().isEmpty());

        //add first menu for company 1
        MenuResponse savedMenu = menuService.addNewMenu(new MenuRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());

        Company refreshFirstCompanyII =  companyService.getByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshFirstCompanyII.isLoggedIn());
        assertFalse(refreshFirstCompanyII.getDefaultMenus().isEmpty());
        assertEquals("register", refreshFirstCompanyII.getDefaultMenus().get(0).getTitle());

        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            menuService.addNewMenu(new MenuRequest("register"));
        });

        assertEquals("Oops! A menu titled 'Register' already exists. Please try another name.", exception.getMessage());

    }
}