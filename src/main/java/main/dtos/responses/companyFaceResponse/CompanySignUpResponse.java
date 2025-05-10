package main.dtos.responses.companyFaceResponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanySignUpResponse {
    private String message;
    private String id;
    private boolean isSuccess;
    private boolean IsLoggedIn;
}