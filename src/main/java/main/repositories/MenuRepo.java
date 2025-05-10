package main.repositories;

import main.models.companies.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuRepo extends MongoRepository<Menu, String> {
}
