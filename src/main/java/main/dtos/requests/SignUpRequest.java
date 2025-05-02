package main.dtos.requests;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SignUpRequest {
    private String companyName;
    private List<String> companyPhone;
    private String companyEmail;
    private String businessRegistrationNumber;
    private String category;
}
