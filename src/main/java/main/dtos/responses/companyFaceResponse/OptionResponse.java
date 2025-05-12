package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class OptionResponse {
    private String menuId;
    private String optionId;
    private String title;
    private String createdAt;
    private String updatedAt;
}
