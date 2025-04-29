package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.models.companies.Menu;
import main.models.companies.Option;
import main.models.users.UserSession;
import main.repositories.MenuRepo;
import main.utils.UserSessionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest {
    @Mock
    private MenuRepo menuRepo;

    @Mock
    private UserSessionStore userSessionStore;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testFetchMainMenu_Success() {
        // Mock Data
        UserSession userSession = new UserSession();
        userSession.setSessionId("session123");
        userSession.setSubCode("subCode");
        userSession.setCurrentPage(1);
        userSession.setLastResponse("");

        Menu menu = new Menu();
        List<Option> options = Arrays.asList(
                createOption("menu123", "opt1", "Option 1", "2023-04-06", "2023-04-06"),
                createOption("menu123", "opt2", "Option 2", "2023-04-06", "2023-04-06"),
                createOption("menu123", "opt3", "Option 3", "2023-04-06", "2023-04-06")
        );
        menu.setOptions(options);

        when(menuRepo.findById("subCode")).thenReturn(java.util.Optional.of(menu));
        when(userSessionStore.getSession("session123")).thenReturn(userSession);

        // Execute
        FetchMenuRequest request = new FetchMenuRequest();
        request.setSessionId("session123");
        request.setSubCode("subCode");
        request.setContext("");
        request.setResponse("");
        request.setPage(1);

        FetchMenuResponse response = customerService.fetchMainMenu(request);


        // Verify
        assertTrue(response.getMessage().contains("Option 1"));
        assertTrue(response.getMessage().contains("Option 2"));
        assertTrue(response.getMessage().contains("Option 3"));
        assertTrue(response.getMessage().contains("99. ➡ Next Page"));
    }

    @Test
    void fetchMainMenu() {
    }

    @Test
    void fetchMenuFrmCompany() {
    }
}
