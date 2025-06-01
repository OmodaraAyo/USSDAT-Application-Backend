package main.repositories;

import main.models.companies.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepo extends MongoRepository<Menu, String> {
}
