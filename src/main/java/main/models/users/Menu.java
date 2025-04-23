package main.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Menu {
    @Indexed
    private String companyId;
    @Id
    private String id;
    private String title;
    private String createdAt;
    private String updatedAt;
}
