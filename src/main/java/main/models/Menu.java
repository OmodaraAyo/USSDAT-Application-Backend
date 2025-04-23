package main.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "menu")
public class Menu {
    @Id
    private String id;
    @Indexed
    private String ussdShortCode;
    private String context;
    private List<>
}
