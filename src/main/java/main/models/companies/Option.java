package main.models.companies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Option {
    private String menuId;
    private String optionId;
    private String title;
//    private String context;
    private String createdAt;
    private String updatedAt;

}
