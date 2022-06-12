package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.Person;
import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonRepository implements ReactivePanacheMongoRepository<Person> {

    public Uni<Person> create(Person person){

        return persist(person)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f), f)) ;

    }

    private String createErrorMessage(Throwable ex){
        if(((MongoWriteException)ex).getCode() == 11000){
            return "Email already exists";
        }
        return "Unknown error";
    }


    public Uni<Person> getPersonByEmail(String email){
        return find("emailAddress", email).firstResult();
    }

    public Uni<Person> getPersonById(String personId) {
        return findById(new ObjectId(personId));
    }
}
