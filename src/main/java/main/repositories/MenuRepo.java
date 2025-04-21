package main.repositories;

import main.models.users.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuRepo extends MongoRepository<Menu, String> {
}
