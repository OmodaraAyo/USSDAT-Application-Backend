package main.dtos.responses.customerFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.models.users.UserOptions;

import javax.swing.text.html.Option;
import java.util.List;

@Data
//@NoArgsConstructor
public class FetchMenuResponse {
    private String message;
    private String context;
    private Boolean hasMore;
    private List<UserOptions> options;
    private Integer page;

    public FetchMenuResponse(String string, boolean b) {
    }
}
