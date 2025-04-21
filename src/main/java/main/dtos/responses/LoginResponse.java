package main.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String token;
    private String response;
    private String warning;
    private Boolean isLoggedIn;
    private boolean isFirstLogin;
}
