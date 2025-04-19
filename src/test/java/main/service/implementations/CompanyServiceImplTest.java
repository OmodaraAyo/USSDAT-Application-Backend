package main.service.implementations;

import main.UssdAtApplication;
import main.dtos.company.CompanyDetailsResponse;
import main.dtos.signIn.LoginRequest;
import main.dtos.signIn.LoginResponse;
import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.exceptions.ValidatorException;
import main.models.enums.Category;
import main.models.utils.UssdCounter;
import main.repository.CompanyRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private CompanyServiceImpl companyService;

    CompanyResponse companyResponse;

    @BeforeEach
    public void startAllWithThis(){
        companyRepo.deleteAll();
        Query query = new Query(Criteria.where("_id").is("ussdShortCode"));
        Update update = new Update().set("ussdCode", 1);
        mongoTemplate.upsert(query, update, UssdCounter.class);

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyName("Unius");
        companyRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
        companyRequest.setCompanyPhone(List.of("09012345678"));
        companyRequest.setCategory("finance");
        companyRequest.setBusinessRegistrationNumber("123456789");
        companyResponse = companyService.registerCompany(companyRequest);
    }


    @Test
    public void shouldCreateNewCompany() {
        assertTrue(companyResponse.isSuccess());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldRejectCompanyName_withSpecialCharacters(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius@1");
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("@@_#1");
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

    }

    @Test
    public void shouldFailValidation_forInvalidEmail(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {

            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024gmail.com");
            companyService.registerCompany(companyRequest);
        });

        assertEquals( "Invalid email address.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius@gmail@gmail.com");
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid email address.", exception2.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldFailValidation_forInvalidNigerianPhoneNumber(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("06123687901"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("08083  352449"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception2.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception3 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("08083  24  49"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception3.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception4 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("080835@@24_-"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception4.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception5 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("0+0835++24_-"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception5.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception6 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("+80835352449"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception6.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception7 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("+23480"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception7.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception8 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("Abcdefghijk"));
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception8.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldFailValidation_forInvalidCategory(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("08123687901"));
            companyRequest.setCategory("water");
            companyService.registerCompany(companyRequest);
        });
        assertEquals( "Invalid category.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone(List.of("08123687901"));
            companyRequest.setCategory("    ");
            companyService.registerCompany(companyRequest);
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

        CompanyRequest companyRequest2 = new CompanyRequest();
        companyRequest2.setCompanyName("Sui");
        companyRequest2.setCompanyEmail("example@gmail.com");
        companyRequest2.setCompanyPhone(List.of("08022211150"));
        companyRequest2.setCategory("ECOMMERCE");
        companyRequest2.setBusinessRegistrationNumber("987654321");
        companyResponse = companyService.registerCompany(companyRequest2);
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
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
            companyRequest.setCompanyPhone(List.of("09012345678"));
            companyRequest.setCategory("FINANCE");
            companyRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(companyRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        RuntimeException exception2  = assertThrows(RuntimeException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("BlueBirds");
            companyRequest.setCompanyEmail("ayodeleomodara1234@gmail.com");
            companyRequest.setCompanyPhone(List.of("09012345678"));
            companyRequest.setCategory("FINANCE");
            companyRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(companyRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

        RuntimeException exception3  = assertThrows(RuntimeException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("jpmorgan@gmail.com");
            companyRequest.setCompanyPhone(List.of("08134128960", "09132457819"));
            companyRequest.setCategory("FINANCE");
            companyRequest.setBusinessRegistrationNumber("123456789");
            companyService.registerCompany(companyRequest);
        });
        assertEquals("An account with this information already exists. Please sign in to access your account.", exception3.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void shouldAllowCompanyToUpdateInformation_whenAuthenticated(){

    }
}