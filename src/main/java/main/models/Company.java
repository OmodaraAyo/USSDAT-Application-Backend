package main.models;

import lombok.*;
import main.models.enums.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed(unique = true)
    private String ussdShortCode;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;
    private String password;
    private String businessRegistrationNumber;
    private Category category;
    private String companyApiKey;
    private String apiKey;
    private String baseUrl;
    private boolean isFirstLogin;
    private String lastLoginDate;
    private String createAt;
    private String updateAt;
    private boolean isActive;
}
