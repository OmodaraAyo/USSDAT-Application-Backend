package main.dtos.responses.customerFaceResponse;

import lombok.Data;

import javax.swing.text.html.Option;
import java.util.List;

@Data
public class FetchMenuResponse {
    private String message;
    private String context;
    private Boolean hasMore;
    private List<Option> options;
    private Integer page;
}
