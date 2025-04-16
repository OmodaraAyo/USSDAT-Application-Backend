package main.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Companies-Admin")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyAdmin {

    @Id
    private String adminId;
    private String companyEmail;
    private String password;
    private String lastLoginDate;
    private boolean isFirstLogin;
    private String createdAt;
    private String updatedAt;
}
