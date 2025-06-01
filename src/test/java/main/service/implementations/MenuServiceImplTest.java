package main.service.implementations;

import main.dtos.requests.companyFaceRequest.*;
import main.dtos.responses.companyFaceResponse.*;
import main.exceptions.EmptyItemException;
import main.exceptions.MenuOptionNotFoundException;
import main.exceptions.ValidatorException;
import main.helper.CompanyUpdateSaverImpl;
import main.models.companies.Menu;
import main.models.enums.Category;
import main.models.enums.Role;
import main.models.security.CompanyPrincipal;
import main.models.companies.Company;
import main.repositories.MenuRepo;
import main.service.implementations.companySide.AuthenticatedCompanyService;
import main.service.implementations.companySide.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @InjectMocks
    private MenuServiceImpl menuService;

    @Mock
    private MenuRepo menuRepo;

    @Mock
    private AuthenticatedCompanyService authenticatedCompanyService;

    @Mock
    private CompanyUpdateSaverImpl companyUpdateSaver;

    private Company mockCompany;
    private Company mockCompany2;

    @BeforeEach
    public void startAllWithThis(){
        mockCompany = new Company();
        mockCompany.setCompanyId("djiwllqdjwewi223330");
        mockCompany.setUssdShortCode("1234");
        mockCompany.setCompanyName("Test Inc");
        mockCompany.setCompanyPhone(List.of("09012345672"));
        mockCompany.setCompanyEmail("test@example.com");
        mockCompany.setPassword("0987654321");
        mockCompany.setCategory(Category.getCategory("finance"));
        mockCompany.setRole(Role.ADMIN);
        mockCompany.setBusinessRegistrationNumber("55577779999101");
        mockCompany.setCompanyApiKey("124556677888888989090988776655444");
        mockCompany.setApiKey("09939391828191919828838382929292");
        mockCompany.setBaseUrl("testInc.com");
        mockCompany.setLoggedIn(true);
        mockCompany.setMenu(new Menu());

//      mock company II
        mockCompany2 = new Company();
        mockCompany2.setUssdShortCode("1345");
        mockCompany2.setCompanyId("qwweririrueioo29033");
        mockCompany2.setCompanyName("Test2 Inc");
        mockCompany2.setCompanyPhone(List.of("09012345672"));
        mockCompany2.setCompanyEmail("test2@example.com");
        mockCompany2.setPassword("password");
        mockCompany.setCategory(Category.getCategory("healthCare"));
        mockCompany2.setRole(Role.ADMIN);
        mockCompany2.setBusinessRegistrationNumber("9585848379922");
        mockCompany2.setCompanyApiKey("102939383374488557899284fyhjssjsjs");
        mockCompany2.setApiKey("01983sjjsjaakkawu2289w9sskss");
        mockCompany2.setBaseUrl("testInc2.com");
        mockCompany2.setLoggedIn(true);
        mockCompany2.setMenu(new Menu());
    }

    @Test
    public void shouldNotAddMenuOptionWhenCompanyIsNotAuthenticated(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenThrow(new AuthenticationServiceException("Authentication required"));
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, ()-> {
            menuService.addNewOption("12345", new CreateOptionRequest("register"));
        });
        assertEquals("Authentication required", exception.getMessage());
    }

    @Test
    public void menuOptions_shouldBeEmptyByDefault(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());
    }

    @Test
    public void givenAuthenticatedCompanyWithEmptyMenuOption_whenOptionIsAdded_thenMenuIsNotEmpty(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company =authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(company.getCompanyId(), request);

        assertNotNull(response);
        assertEquals("register", mockCompany.getMenu().getOptions().get(0).getTitle());
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(1));

    }

    @Test
    public void whenMultipleCompaniesAddOptionsSimultaneously_thenMenusAreUpdatedCorrectly(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertEquals("register", mockCompany.getMenu().getOptions().get(0).getTitle());
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(1));


        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany2);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany2);

        CompanyPrincipal principal2 = new CompanyPrincipal(mockCompany2);
        UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(principal2, null, principal2.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth2);
        Company company2 = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company2.isLoggedIn());
        assertTrue(company2.getMenu().getOptions().isEmpty());
        CreateOptionRequest request2 = new CreateOptionRequest("register");
        CreatedOptionResponse response2 = menuService.addNewOption(mockCompany2.getCompanyId(), request2);
        assertNotNull(response2);
        assertEquals("register", mockCompany2.getMenu().getOptions().get(0).getTitle());
        assertFalse(company2.getMenu().getOptions().isEmpty());
        assertThat(company2.getMenu().getOptions(), hasSize(1));
    }

    @Test
    public void givenAuthenticatedCompany_whenAddingEmptyOrNullOption_thenThrowValidationException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());
        CreateOptionRequest request = new CreateOptionRequest("");

        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            menuService.addNewOption(mockCompany.getCompanyId(), request);
        });
        assertEquals("Please enter a menu title.", exception.getMessage());
        assertTrue(company.getMenu().getOptions().isEmpty());
    }

    @Test
    public void givenAuthenticatedCompany_whenAddingDuplicateOption_thenThrowValidationException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertEquals("register", mockCompany.getMenu().getOptions().get(0).getTitle());
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(1));

        CreateOptionRequest request2 = new CreateOptionRequest("register");
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            menuService.addNewOption(mockCompany.getCompanyId(), request2);
        });
        assertEquals("Oops! A menu titled 'register' already exists. Please try another name.", exception.getMessage());
    }

    @Test
    public void givenCurrentUserHasMenus_whenFindingByTitle_thenReturnMatchingMenu(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());
        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertEquals("register", mockCompany.getMenu().getOptions().get(0).getTitle());
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(1));

        CreateOptionRequest request2 = new CreateOptionRequest("Check balance");
        CreatedOptionResponse response2 = menuService.addNewOption(mockCompany.getCompanyId(), request2);
        assertNotNull(response2);
        assertTrue("Check balance".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(1).getTitle()));
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(2));

        CreateOptionRequest request3 = new CreateOptionRequest("Transfer");
        CreatedOptionResponse response3 = menuService.addNewOption(mockCompany.getCompanyId(), request3);
        assertNotNull(response3);
        assertTrue("Transfer".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(2).getTitle()));
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(3));

        MenuOptionResponse optionResponse = menuService.getMenuOptionByTitle(company.getCompanyId(), "Check balance");
        assertNotNull(optionResponse);
        assertTrue("Check Balance".equalsIgnoreCase(optionResponse.getTitle()));
    }

    @Test
    public void givenCurrentUserHasNoMenus_whenFindingByTitle_thenThrowEmptyItemException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        EmptyItemException exception = assertThrows(EmptyItemException.class, () -> {
            menuService.getMenuOptionByTitle(company.getCompanyId(), "account");
        });

        assertEquals("Looks like there’s nothing here yet. Add a menu to get started!", exception.getMessage());
    }

    @Test
    public void givenCurrentUserHasMenus_whenSearchingForInvalidTitle_thenThrowMenuNotFoundException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));
        assertFalse(company.getMenu().getOptions().isEmpty());
        assertThat(company.getMenu().getOptions(), hasSize(1));

        MenuOptionNotFoundException exception = assertThrows(MenuOptionNotFoundException.class, () -> {
            menuService.getMenuOptionByTitle(company.getCompanyId(), "account");
        });

        assertTrue("No menu option found with the title \"account\".".equalsIgnoreCase(exception.getMessage()));
    }

    @Test
    public void givenCompanyIsNotAuthenticated_whenSearchingForMenuTitle_thenThrowException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenThrow(new AuthenticationServiceException("Authentication required"));
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> {
            menuService.getMenuOptionByTitle(mockCompany.getCompanyId(), "account");
        });
        assertEquals("Authentication required", exception.getMessage());
    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenMenuIsDeletedById_thenReturnSuccessResponse(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();

        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));

        CreateOptionRequest request2 = new CreateOptionRequest("account");
        CreatedOptionResponse response2 = menuService.addNewOption(mockCompany.getCompanyId(), request2);
        assertNotNull(response2);
        assertTrue("account".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(1).getTitle()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(2));

        DeleteMenuOptionRequest deleteMenuOptionRequest = new DeleteMenuOptionRequest(response.getOptionId());
        DeleteMenuOptionResponse deleteMenuOptionResponse = menuService.deleteMenuOptionById(mockCompany.getCompanyId(), deleteMenuOptionRequest.getOptionId());
        assertNotNull(deleteMenuOptionResponse);
        assertTrue("Deleted successfully.".equalsIgnoreCase(deleteMenuOptionResponse.getMessage()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(1));
    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenFindingMenuById_thenReturnSuccessResponse(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));

        CreateOptionRequest request2 = new CreateOptionRequest("account");
        CreatedOptionResponse response2 = menuService.addNewOption(mockCompany.getCompanyId(), request2);
        assertNotNull(response2);
        assertTrue("account".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(1).getTitle()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(2));

        MenuOptionResponse menuOptionResponse = menuService.getMenuOptionById(company.getCompanyId(), response.getOptionId());
        assertNotNull(menuOptionResponse);
        assertTrue("register".equalsIgnoreCase(menuOptionResponse.getTitle()));


    }

    @Test
    public void givenAuthenticatedUserWithMenu_whenFindingMenuWIthInvalidId_thenThrowMenuNotFoundException(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));

        MenuOptionNotFoundException exception = assertThrows(MenuOptionNotFoundException.class, () -> {
            menuService.getMenuOptionById(company.getCompanyId(), "120223833838339");
        });
        assertTrue("No menu option found with this id: \"120223833838339\".".equalsIgnoreCase(exception.getMessage()));
    }

    @Test
    public void givenAuthenticatedUserWithMenu_canUpdateMenuOption_thenReturnSuccessResponse(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(company.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(1));

        UpdateOptionRequest updateOptionRequest = new UpdateOptionRequest("account");
        UpdateOptionResponse updateOptionResponse = menuService.updateMenuOption(company.getCompanyId(), response.getOptionId(), updateOptionRequest);
        assertNotNull(updateOptionResponse);
        assertTrue("account".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(1));
    }

    @Test
    public void givenAuthenticatedCompany_whenFindingAllMenuOptions_thenReturnCompanyMenuOptions(){
        when(authenticatedCompanyService.getCurrentAuthenticatedCompany()).thenReturn(mockCompany);
        when(companyUpdateSaver.saveUpdatedCompany(any(Company.class))).thenReturn(mockCompany);

        CompanyPrincipal principal = new CompanyPrincipal(mockCompany);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Company company = authenticatedCompanyService.getCurrentAuthenticatedCompany();
        assertTrue(company.isLoggedIn());
        assertTrue(company.getMenu().getOptions().isEmpty());

        CreateOptionRequest request = new CreateOptionRequest("register");
        CreatedOptionResponse response = menuService.addNewOption(mockCompany.getCompanyId(), request);
        assertNotNull(response);
        assertTrue("register".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(0).getTitle()));

        CreateOptionRequest request2 = new CreateOptionRequest("Account");
        CreatedOptionResponse response2 = menuService.addNewOption(mockCompany.getCompanyId(), request2);
        assertNotNull(response2);
        assertTrue("Account".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(1).getTitle()));

        CreateOptionRequest request3 = new CreateOptionRequest("Balance");
        CreatedOptionResponse response3 = menuService.addNewOption(mockCompany.getCompanyId(), request3);
        assertNotNull(response3);
        assertTrue("Balance".equalsIgnoreCase(mockCompany.getMenu().getOptions().get(2).getTitle()));
        assertThat(mockCompany.getMenu().getOptions(), hasSize(3));

        CompanyMenuOptionsResponse companyMenuOptionsResponse = menuService.getMenuOptionsForCompany(company.getCompanyId());
        assertNotNull(companyMenuOptionsResponse);
        assertThat(companyMenuOptionsResponse.getMenuOptions(), hasSize(3));
        assertEquals(companyMenuOptionsResponse.getCompanyId(), company.getCompanyId());
        assertTrue("register".equalsIgnoreCase(companyMenuOptionsResponse.getMenuOptions().get(0)));
        assertTrue("Account".equalsIgnoreCase(companyMenuOptionsResponse.getMenuOptions().get(1)));
        assertTrue("Balance".equalsIgnoreCase(companyMenuOptionsResponse.getMenuOptions().get(2)));
    }

//    @Test
//    public void test(){
//        CompanySignUpRequest companySignUpRequest = new CompanySignUpRequest();
//        companySignUpRequest.setCompanyName("Unius");
//        companySignUpRequest.setCompanyEmail("pablo1234@gmail.com");
//        companySignUpRequest.setCompanyPhone(List.of("09012345678"));
//        companySignUpRequest.setCategory("finance");
//        companySignUpRequest.setBusinessRegistrationNumber("123456789");
//        companyService.registerCompany(companySignUpRequest);
//    }
}