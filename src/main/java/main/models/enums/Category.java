package main.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.exceptions.ValidatorException;

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

    public static Category getCategory(String category) {
        for (Category c : Category.values()) {
            if (c.getCategory().equalsIgnoreCase(category.trim())) {
                return c;
            }
        }
        throw new ValidatorException("Invalid category.");
    }
}
