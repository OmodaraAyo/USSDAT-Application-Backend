package main.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("admin"),
    USER("user");
    private final String role;
}
