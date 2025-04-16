package main.dtos.signIn;

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
