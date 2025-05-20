package main.dtos.requests.companyFaceRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    @Size(min = 9) private String newPassword;
    @Size(min = 9) private String confirmPassword;
}
