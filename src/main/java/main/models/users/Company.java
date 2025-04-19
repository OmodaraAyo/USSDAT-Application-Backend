package main.models.users;

import lombok.*;
import main.models.enums.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    @Indexed(unique = true)
    private String companyName;
    private List<String> companyPhone;
    @Indexed(unique = true)
    private String companyEmail;
    private String password;
    private String businessRegistrationNumber;
    private Category category;
    private String companyApiKey;
    @Indexed(unique = true)
    private String apiKey;
    private String baseUrl;
    private boolean isActive;
    private boolean isFirstLogin;
    private String lastLoginDate;
    private String createAt;
    private String updateAt;
}
