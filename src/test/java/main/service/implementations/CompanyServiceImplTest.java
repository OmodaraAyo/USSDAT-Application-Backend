package main.service.implementations;

import main.dtos.signUp.CompanyRequest;
import main.dtos.signUp.CompanyResponse;
import main.models.enums.Category;
import main.repository.CompanyRepo;
import main.service.interfaces.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepo companyRepo;


    @Test
    public void testToCreateANewCompany() {
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyName("Unius");
        companyRequest.setCompanyEmail("unius2024@gmail.com");
        companyRequest.setCompanyPhone("09012345678");
        companyRequest.setCategory(Category.FINANCE);
        companyRequest.setCompanyAddress("2B, Akinsola street");
        companyRequest.setBusinessRegistrationNumber("123456789");

        CompanyResponse companyResponse = companyService.createCompany(companyRequest);
        assertTrue(companyResponse.isSuccess());
        assertEquals(3, companyRepo.count());
    }

}