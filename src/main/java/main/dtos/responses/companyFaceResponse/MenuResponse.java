package main.dtos.responses.companyFaceResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuResponse {
    private String companyId;
    private String id;
    private String response;
}
