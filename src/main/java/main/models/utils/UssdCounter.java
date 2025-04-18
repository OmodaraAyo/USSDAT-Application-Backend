package main.models.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ussd-counter")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UssdCounter {
    @Id
    private String id;
    private int ussdCode;
}
