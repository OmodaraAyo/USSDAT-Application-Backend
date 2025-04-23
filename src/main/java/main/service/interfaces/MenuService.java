package main.service.interfaces;

import main.dtos.requests.MenuRequest;
import main.dtos.requests.MenuTitleRequest;
import main.dtos.responses.MenuResponse;
import main.dtos.responses.MenuTitleResponse;

public interface MenuService {

    MenuResponse addNewMenu(MenuRequest request);
    MenuTitleResponse findByMenuTitle(MenuTitleRequest menuTitleRequest);
}
