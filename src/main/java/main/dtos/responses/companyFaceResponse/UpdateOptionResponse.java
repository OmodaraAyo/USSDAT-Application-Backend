package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateOptionResponse {
    private String optionId;
    private String message;
    private boolean isSuccess;
    private String updatedAt;
}
