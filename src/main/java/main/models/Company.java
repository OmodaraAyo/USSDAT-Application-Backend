package main.models;

import lombok.*;
import main.models.enums.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Companies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Company {
    @Id
    private String companyId;
    private String ussdShortCode;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private Category category;
    private String apiKey;
    private String baseUrl;
    private String createAt;
    private String updateAt;
    private boolean isActive;
}
