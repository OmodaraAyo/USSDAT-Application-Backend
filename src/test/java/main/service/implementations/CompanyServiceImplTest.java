package main.service.implementations;

import main.UssdAtApplication;
import main.dtos.requests.companyFaceRequest.ChangePasswordRequest;
import main.dtos.requests.companyFaceRequest.CompanySignUpRequest;
import main.dtos.requests.companyFaceRequest.LoginRequest;
import main.dtos.requests.companyFaceRequest.UpdateCompanyRequest;
import main.dtos.responses.companyFaceResponse.DeleteCompanyAccountResponse;
import main.dtos.responses.companyFaceResponse.CompanyDetailsResponse;
import main.dtos.responses.companyFaceResponse.LoginResponse;
import main.dtos.responses.companyFaceResponse.LogoutResponse;
import main.dtos.responses.companyFaceResponse.ChangePasswordResponse;
import main.dtos.responses.companyFaceResponse.UpdateCompanyResponse;
import main.dtos.responses.companyFaceResponse.CompanySignUpResponse;
import main.exceptions.ValidatorException;
import main.models.enums.Category;
import main.models.security.CompanyPrincipal;
import main.models.companies.Company;
import main.models.utils.UssdCounter;
import main.repositories.CompanyRepo;
import main.service.implementations.companySide.CompanyServiceImpl;
import main.service.interfaces.companySide.CompanyService;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UssdAtApplication.class)
@ActiveProfiles("test")
public class CompanyServiceImplTest {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private CompanyService companyService;

    private CompanySignUpResponse signUpResponse;

    @BeforeEach
    public void startAllWithThis(){
        companyRepo.deleteAll();
        Query query = new Query(Criteria.where("_id").is("ussdShortCode"));
        Update update = new Update().set("ussdCode", 1);
        mongoTemplate.upsert(query, update, UssdCounter.class);

        CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
        signUpRequest.setCompanyName("Unius");
        signUpRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
        signUpRequest.setCompanyPhone(List.of("09012345678"));
        signUpRequest.setCategory("finance");
        signUpRequest.setBusinessRegistrationNumber("123456789");
        signUpResponse = companyService.registerCompany(signUpRequest);
    }


    @Test
    public void shouldCreateNewCompany() {
        assertTrue(signUpResponse.isSuccess());
        assertEquals(1, companyRepo.count());
    }


    @Test
    public void shouldRejectCompanyName_withSpecialCharacters(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius@1");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("@@_#1");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

    }

    @Test
    public void shouldFailValidation_forInvalidEmail(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {

            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024gmail.com");
            companyService.registerCompany(signUpRequest);
        });

        assertEquals( "Invalid email address.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius@gmail@gmail.com");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid email address.", exception2.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldFailValidation_forInvalidNigerianPhoneNumber(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("06123687901"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("08083  352449"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception2.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception3 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("08083  24  49"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception3.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception4 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("080835@@24_-"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception4.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception5 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("0+0835++24_-"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception5.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception6 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("+80835352449"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception6.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception7 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("+23480"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception7.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception8 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("Abcdefghijk"));
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception8.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldFailValidation_forInvalidCategory(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("08123687901"));
            signUpRequest.setCategory("water");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid category.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("unius2024@gmail.com");
            signUpRequest.setCompanyPhone(List.of("08123687901"));
            signUpRequest.setCategory("    ");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals( "Invalid category.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

    }

    @Test
    public void shouldSignInWithGeneratedPassword_afterCompanyRegistration(){
        String firstRegisteredCompanyPassword = CompanyServiceImpl.genPass;
        System.out.println("first registration: "+firstRegisteredCompanyPassword);

        LoginResponse loginResponse= companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", firstRegisteredCompanyPassword));
        assertTrue(loginResponse.getIsLoggedIn());

        CompanySignUpRequest signUpRequest2 = new CompanySignUpRequest();
        signUpRequest2.setCompanyName("Sui");
        signUpRequest2.setCompanyEmail("example@gmail.com");
        signUpRequest2.setCompanyPhone(List.of("08022211150"));
        signUpRequest2.setCategory("ECOMMERCE");
        signUpRequest2.setBusinessRegistrationNumber("987654321");
        signUpResponse = companyService.registerCompany(signUpRequest2);
        String secondRegisteredCompanyPassword = CompanyServiceImpl.genPass;
        System.out.println("second registration: "+secondRegisteredCompanyPassword);

        LoginResponse loginResponse2 = companyService.signIn(new LoginRequest("example@gmail.com", secondRegisteredCompanyPassword));
        assertTrue(loginResponse2.getIsLoggedIn());

    }

    @Test
    public void shouldNotSignInWithIncorrectCredentials(){
        String password = CompanyServiceImpl.genPass;
        System.out.println("Password from new test: "+password);

        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", "123456789"));
        });
        assertEquals("Bad credentials: Invalid email or password", exception.getMessage());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            companyService.signIn(new LoginRequest("test@gmail.com", password));
        });
        assertEquals("Bad credentials: Invalid email or password", exception2.getMessage());
    }

    @Test
    public void shouldFindCompanyByEmail(){
        CompanyDetailsResponse companyDetailsResponse = companyService.findCompanyByEmail("ayodeleomodara1234@gmail.com");
        assertEquals("Unius".toLowerCase(), companyDetailsResponse.getCompanyName().toLowerCase());
        assertEquals("123456789", companyDetailsResponse.getBusinessRegistrationNumber());
        assertEquals("ayodeleomodara1234@gmail.com".toLowerCase(), companyDetailsResponse.getCompanyEmail().toLowerCase());
        assertSame(companyDetailsResponse.getCategory(), Category.FINANCE);
        assertTrue(companyDetailsResponse.isActive());
    }

    @Test
    public void shouldAllowCompanyToRegisterOnlyOnce(){
        RuntimeException exception  = assertThrows(RuntimeException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
            signUpRequest.setCompanyPhone(List.of("09012345678"));
            signUpRequest.setCategory("FINANCE");
            signUpRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        RuntimeException exception2  = assertThrows(RuntimeException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("BlueBirds");
            signUpRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
            signUpRequest.setCompanyPhone(List.of("09012345678"));
            signUpRequest.setCategory("FINANCE");
            signUpRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

        RuntimeException exception3  = assertThrows(RuntimeException.class, () -> {
            CompanySignUpRequest signUpRequest = new CompanySignUpRequest();
            signUpRequest.setCompanyName("Unius");
            signUpRequest.setCompanyEmail("jpmorgan@gmail.com");
            signUpRequest.setCompanyPhone(List.of("08134128960", "09132457819"));
            signUpRequest.setCategory("FINANCE");
            signUpRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(signUpRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception3.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldFindCompanyById(){
        Company company = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");

        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        CompanyDetailsResponse companyDetailsRequest = companyService.findCompanyById(company.getCompanyId());
        assertEquals("Unius".toLowerCase(), companyDetailsRequest.getCompanyName());
        assertEquals("123456789".toLowerCase(), companyDetailsRequest.getBusinessRegistrationNumber());
        assertEquals("ayodeleomodara1234@gmail.com".toLowerCase(), companyDetailsRequest.getCompanyEmail().toLowerCase());
        assertSame(companyDetailsRequest.getCategory(), Category.FINANCE);
        assertTrue(companyDetailsRequest.isActive());
    }

    @Test
    public void shouldAllowCompanyToUpdateInformation_whenAuthenticated(){
        assertFalse(signUpResponse.isIsLoggedIn());
        String registeredCompanyPassword1 = CompanyServiceImpl.genPass;

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", registeredCompanyPassword1));
        assertTrue(loginResponse.getIsLoggedIn());

        Company company = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");

        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ChangePasswordResponse request = companyService.resetPassword(company.getCompanyId(),new ChangePasswordRequest(registeredCompanyPassword1, "Ayodele01$", "Ayodele01$"));
        assertEquals("Password changed successfully", request.getMessage());
        assertTrue(company.isLoggedIn());

        UpdateCompanyRequest updateRequest = new UpdateCompanyRequest();
        updateRequest.setCompanyPhone(List.of("09012345678"));
        updateRequest.setCategory("FINANCE");
        updateRequest.setCompanyApiKey("T9uO8N4v1GZrWQX9F2lRA2J7oTxkCWy6G9gO2A7GJvLkN2vEr3nE9QjV7Q0e3lKpFeXvQ0L1OZoQmQkz009xYtFAK");
        updateRequest.setBaseUrl("https://api.example.com/");
        UpdateCompanyResponse updatedCompany = companyService.updateCompanyDetails(company.getCompanyId(),updateRequest);
        assertEquals("Updated Successfully", updatedCompany.getMessage());
        assertTrue(company.isLoggedIn());

        LogoutResponse response = companyService.logOut(company.getCompanyId());
        assertEquals("Logout successful", response.getMessage());
        Company refreshedCompany = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(refreshedCompany.isLoggedIn());
    }

    @Test
    public void shouldRejectUpdateWhenUserIsUnauthenticated(){
        assertFalse(signUpResponse.isIsLoggedIn());
        AuthenticationServiceException exception = assertThrows(AuthenticationServiceException.class, () -> {
            companyService.resetPassword("1234567", new ChangePasswordRequest("wagwan1234", "Ayodele01$", "Ayodele01$"));
        });
        assertEquals("Authentication required.", exception.getMessage());
    }

    @Test
    public void shouldNotAuthenticateUserWithInvalidCredentials(){
        assertFalse(signUpResponse.isIsLoggedIn());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.signIn(new LoginRequest("pablo@gmail.com", "123456789"));
        });

        assertEquals("Bad credentials: Invalid email or password", exception.getMessage());
        assertFalse(signUpResponse.isIsLoggedIn());

        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {
            companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", "123456789"));
        });

        assertEquals("Bad credentials: Invalid email or password", exception2.getMessage());
        assertFalse(signUpResponse.isIsLoggedIn());

    }

    @Test
    public void shouldAllowCompanyToDeleteItsAccountIfAuthenticated(){
        assertFalse(signUpResponse.isIsLoggedIn());
        String registeredCompanyPassword1 = CompanyServiceImpl.genPass;
        Company company = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(company.isLoggedIn());

        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", registeredCompanyPassword1));
        assertTrue(loginResponse.getIsLoggedIn());

        Company refreshCompany = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertTrue(refreshCompany.isLoggedIn());

        CompanyPrincipal principal = new CompanyPrincipal(company);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ChangePasswordResponse request = companyService.resetPassword(refreshCompany.getCompanyId(), new ChangePasswordRequest(registeredCompanyPassword1, "Ayodele01$", "Ayodele01$"));
        assertEquals("Password changed successfully", request.getMessage());
        assertTrue(refreshCompany.isLoggedIn());

        DeleteCompanyAccountResponse response = companyService.deactivateCompany(refreshCompany.getCompanyId());
        assertEquals("Account closed successfully", response.getMessage());
        assertTrue(response.isSuccess());

        Company refreshCompany2 = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
        assertFalse(refreshCompany2.isLoggedIn());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", registeredCompanyPassword1));
        });

        assertEquals("Account is deactivated. Please contact support.", exception.getMessage());

        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {
            companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", "Ayodele01$"));
        });

        assertEquals("Account is deactivated. Please contact support.", exception2.getMessage());
    }

//    @Test
//    public void shouldAllowCompanyToCreateDefaultMenuIfAuthenticated(){
//        String registeredCompanyPassword1 = CompanyServiceImpl.genPass;
//        Company company = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
//        assertFalse(company.isLoggedIn());
//
//        LoginResponse loginResponse = companyService.signIn(new LoginRequest("ayodeleomodara1234@gmail.com", registeredCompanyPassword1));
//        assertTrue(loginResponse.getIsLoggedIn());
//
//        Company refreshCompany = companyRepo.findByCompanyEmail("ayodeleomodara1234@gmail.com");
//        assertTrue(refreshCompany.isLoggedIn());
//
//        CompanyPrincipal principal = new CompanyPrincipal(company);
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        ChangePasswordResponse request = companyService.resetPassword(new ChangePasswordRequest(registeredCompanyPassword1, "Ayodele01$"));
//        assertEquals("Password changed successfully", request.getMessage());
//        assertTrue(refreshCompany.isLoggedIn());
//
//        companyService.addMenu(new MenuRequest("register"));
//
//    }

}