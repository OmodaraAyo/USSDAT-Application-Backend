package main.service.implementations.companySide;

import main.models.enums.Category;
import main.service.interfaces.companySide.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<String> getAllCategories() {
        return Arrays.stream(Category.values()).map(Category::getCategory).toList();
    }
}
