package main.dtos.requests.companyFaceRequest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginRequest {
    private String companyEmail;
    private String password;
}
