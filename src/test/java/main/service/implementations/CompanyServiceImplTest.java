package main.service.implementations;

import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.exceptions.ValidatorException;
import main.models.enums.Category;
import main.models.users.Company;
import main.models.utils.UssdCounter;
import main.repository.CompanyRepo;
import main.service.interfaces.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

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
        companyRequest.setCompanyPhone("09012345678");
        companyRequest.setCategory(Category.FINANCE);
        companyRequest.setBusinessRegistrationNumber("123456789");
        companyResponse = companyService.createCompany(companyRequest);
    }


    @Test
    public void testToCreateANewCompany() {
        assertTrue(companyResponse.isSuccess());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void testInvalidCompanyNameWithSpecialCharacters(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius@1");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("@@_#1");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "The following characters are not allowed: @, #, $, %, ,, !, ?, /, \\. Please remove them and try again.", exception2.getMessage());
        assertEquals(1, companyRepo.count());

    }

    @Test
    public void testEmailValidationFailsForInvalidPattern(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {

            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024gmail.com");
            companyService.createCompany(companyRequest);
        });

        assertEquals( "Invalid email address.", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius@gmail@gmail.com");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid email address.", exception2.getMessage());
        assertEquals(1, companyRepo.count());
    }

    @Test
    public void testPhoneNumberValidationFailsForInvalidNigeriaPhoneNumberPattern(){
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("06123687901");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception2 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("08083  352449");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception2.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception3 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("08083  24  49");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception3.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception4 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("080835@@24_-");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception4.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception5 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("0+0835++24_-");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception5.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception6 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("+80835352449");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception6.getMessage());
        assertEquals(1, companyRepo.count());

        ValidatorException exception7 = assertThrows(ValidatorException.class, () -> {
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setCompanyName("Unius");
            companyRequest.setCompanyEmail("unius2024@gmail.com");
            companyRequest.setCompanyPhone("+23480");
            companyService.createCompany(companyRequest);
        });
        assertEquals( "Invalid phone number. Please try again", exception7.getMessage());
        assertEquals(1, companyRepo.count());
    }

}