package main.service.interfaces;

import main.dtos.requests.MenuRequest;
import main.dtos.responses.MenuResponse;

public interface MenuService {

    MenuResponse addNewMenu(MenuRequest request);
}
