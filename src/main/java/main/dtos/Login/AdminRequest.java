package main.dtos.Login;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AdminRequest {
    private String companyEmail;
    private String password;
}
