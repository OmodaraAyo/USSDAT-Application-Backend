package main.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Category {
    FINANCE("finance"),
    HEALTHCARE("healthCare"),
    TECHNOLOGY("technology"),
    SOCIAL("social"),
    EDUCATION("education"),
    ECOMMERCE("ecommerce");
    private final String category;
}
