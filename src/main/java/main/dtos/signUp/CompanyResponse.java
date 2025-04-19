package main.dtos.signUp;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyResponse {
    private String message;
    private boolean isSuccess;
}
