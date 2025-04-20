package main.dtos.signOut;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.dtos.signIn.LoginResponse;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogoutResponse {
    private String message;
}
