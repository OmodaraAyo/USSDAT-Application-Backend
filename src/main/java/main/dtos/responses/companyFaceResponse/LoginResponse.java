package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String x_y_z;
    private String ref;
    private String response;
    private String warning;
    private Boolean isLoggedIn;
    private boolean isFirstLogin;
}
