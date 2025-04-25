package main.service.implementations.customerSide;

import main.repositories.MenuRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceImplTest {

    @Mock
    private MenuRepo menuRepo;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchMainMenu() {
    }

    @Test
    void fetchMenuFrmCompany() {
    }
}
