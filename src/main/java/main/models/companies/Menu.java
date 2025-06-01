package main.models.companies;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Menu {
    @Indexed
    @Id
    private String id;
    private List<Option> options = new ArrayList<>();
    private String createdAt;
    private String updatedAt;

}
