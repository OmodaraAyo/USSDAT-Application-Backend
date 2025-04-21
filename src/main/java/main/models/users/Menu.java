package main.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Menu {
    @Id
    private String id;
    private String title;
    private String createdAt;
    private String updatedAt;
}
