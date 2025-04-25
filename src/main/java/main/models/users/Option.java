package main.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Option {
    private String menuId;
    private String optionId;
    private String title;
    private String createdAt;
    private String updatedAt;
}
