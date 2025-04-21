package main.service.implementations;

import main.dtos.requests.CompanyRequest;
import main.dtos.requests.MenuRequest;
import main.dtos.responses.CompanyResponse;
import main.dtos.responses.MenuResponse;
import main.models.users.Company;
import main.models.utils.UssdCounter;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MenuServiceImplTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CompanyService companyService;

    private CompanyResponse companyResponse;

    @BeforeEach
    public void startAllWithThis(){
        companyService.deleteAllCompanies();
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
    public void shouldAddNewMenu(){
        MenuResponse savedMenu = menuService.addNewMenu(companyResponse.getId(), new MenuRequest("register"));
        assertEquals("Awesome! Your menu is now live.", savedMenu.getResponse());
        Company company = companyService.getCompanyById(companyResponse.getId());
        assertEquals("register", company.getDefaultMenus().get(0).getTitle());

//        MenuResponse savedMenu2 = menuService.addNewMenu(companyResponse.getId(), new MenuRequest("Check balance"));
//        assertEquals("Awesome! Your menu is now live.", savedMenu2.getResponse());
//        Company company2 = companyService.getCompanyById(companyResponse.getId());
//        assertEquals("Check balance", company2.getDefaultMenus().get(1).getTitle());
    }

}