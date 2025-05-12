package main.dtos.responses.customerFaceResponse;

import lombok.Data;
import main.models.companies.Option;

import java.util.List;
@Data
public class UserInteractionResponse {
    private String message;
    private Boolean isEnd;
    private List<Option> options;
//    private boolean isEnd;
}
