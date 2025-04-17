package main.utils;

import main.models.utils.UssdCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class UssdCounterUtil {

    @Autowired
    private MongoTemplate mongoTemplate;

    public int getNextUssdCode(){
        Query query = new Query(Criteria.where("_id").is("ussdShortCode"));
        Update update = new Update().inc("ussdCode", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        UssdCounter ussdCounter = mongoTemplate.findAndModify(query, update, options, UssdCounter.class);
        assert ussdCounter != null;
        int next = ussdCounter.getUssdCode();

        if(next > 900){
            throw new IllegalArgumentException("Oh no! ussd code limit maxed out");
        }
        return next;
    }
}
