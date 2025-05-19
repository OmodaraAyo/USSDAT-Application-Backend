package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreatedOptionResponse {
    private String companyId;
    private String menuId;
    private String title;
    private String optionId;
    private String response;
    private boolean isSuccess;
}
