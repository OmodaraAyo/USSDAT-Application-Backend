package main.dtos.responses;

import lombok.*;

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
