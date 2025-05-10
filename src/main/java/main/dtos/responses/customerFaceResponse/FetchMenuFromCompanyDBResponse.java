package main.dtos.responses.customerFaceResponse;

import lombok.Data;
import main.models.companies.Option;

import java.util.List;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class FetchMenuFromCompanyDBResponse {
    private String message;
    private String context;
    private List<Option> options;

}

