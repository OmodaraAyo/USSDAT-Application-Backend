package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuFromCompanyDBRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuFromCompanyDBResponse;
import main.exceptions.CompanyNotFound;
import main.models.companies.Company;
import main.models.companies.Menu;
import main.models.companies.Option;
import main.repositories.CompanyRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.flapdoodle.embed.process.io.Files.deleteAll;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FetchMenuServiceTest {

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private FetchMenuService fetchMenuService;
    @BeforeEach
    void setUp() {
        Company testCompany = new Company();
        testCompany.setUssdShortCode("13345");

        Menu testMenu = new Menu();
        List<Option> options = new ArrayList<>();

        Option option1 = new Option();
        option1.setMenuId("menu123");
        option1.setOptionId("opt1");
        option1.setTitle("Option 1");
        option1.setCreatedAt("");
        option1.setUpdatedAt("");

        Option option2 = new Option();
        option2.setMenuId("menu123");
        option2.setOptionId("opt2");
        option2.setTitle("Option 2");
        option2.setCreatedAt("");
        option2.setUpdatedAt("");

        Option option3 = new Option();
        option3.setMenuId("menu123");
        option3.setOptionId("opt3");
        option3.setTitle("Option 3");
        option3.setCreatedAt("");
        option3.setUpdatedAt("");

        options.add(option1);
        options.add(option2);
        options.add(option3);

        testMenu.setOptions(options);
        testCompany.setMenu(testMenu);

        companyRepo.save(testCompany);
    }
    @AfterEach
   void tearDown(){
        companyRepo.deleteAll();
    }


    @Test
    void testFetchMainMenu_Success() {
        FetchMenuFromCompanyDBRequest request = new FetchMenuFromCompanyDBRequest();
        request.setSubCode("13345");

        FetchMenuFromCompanyDBResponse response = fetchMenuService.fetchMainMenu(request);

        assertNotNull(response);
        assertEquals("Menu fetched successfully", response.getMessage());
        assertEquals("main menu", response.getContext());
        assertEquals(3, response.getOptions().size());
        assertEquals("Option 1", response.getOptions().get(0).getTitle());
    }
    @Test
    void testFetchMainMenu_CompanyNotFound() {
        FetchMenuFromCompanyDBRequest request = new FetchMenuFromCompanyDBRequest();
        request.setSubCode("99999");

        Exception exception = assertThrows(CompanyNotFound.class, () -> fetchMenuService.fetchMainMenu(request));
        assertEquals("This company does not exist", exception.getMessage());
    }
}