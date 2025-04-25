package main.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateOptionResponse {
    private String optionId;
    private boolean isSuccess;
    private String updatedAt;
}
