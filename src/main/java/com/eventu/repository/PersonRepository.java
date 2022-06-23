package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.Person;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonRepository implements ReactivePanacheMongoRepository<Person>, IRepository {

    public Uni<Person> create(Person person){

        return persist(person)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f,"Email already exists"), f)) ;

    }

    public Uni<Person> getPersonByEmail(String email){
        return find("emailAddress", email).firstResult();
    }

    public Uni<Person> getPersonById(String personId) {
        return findById(new ObjectId(personId));
    }
}
