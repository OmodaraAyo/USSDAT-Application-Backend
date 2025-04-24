package main.dtos.responses.companyFaceResponse;

import lombok.*;
import main.models.users.Menu;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyResponse {
    private String message;
    private String id;
    private boolean isSuccess;
    private boolean IsLoggedIn;
}
