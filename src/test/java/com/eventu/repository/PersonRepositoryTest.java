package com.eventu.repository;

import com.eventu.vo.Address;
import com.eventu.vo.Person;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.smallrye.mutiny.Uni;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

class PersonRepositoryTest {


    Person person;

    @BeforeEach
    public void setup(){
        person = Person.builder().emailAddress("test2test.com")
                .firstName("Test name")
                .lastName("Test last name")
                .mobileNumber("7045558888")
                .address(Address.builder().
                addressLine_1("Test address 1")
                                .addressLine_2("address line 2")
                                .city("Las vegas")
                                .state("NV")
                                .roomNumber("123")
                                .floorNumber("2")
                                .build()
                        )
                .build();
    }

    @Test
    @DisplayName("When create is called then return Uni<Person>")
    public void create_get_Person(){
        PersonRepository repository = spy(PersonRepository.class);
        Mockito.doReturn(Uni.createFrom().item(person)).when(repository).persist(Mockito.any(Person.class));
        //Mockito.when()
        Uni<Person> personUni = repository.create(person);
        Assertions.assertNotNull(personUni);
        Person p = personUni.await().atMost(Duration.ofSeconds(5));
        System.out.println(p);
    }

    @Test
    @DisplayName("When create is called then return Business exception")
    public void create_get_Person_throwException(){
        PersonRepository repository = spy(PersonRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(11000, "Duplicate value", new BsonDocument()),null)))
        .when(repository).persist(Mockito.any(Person.class));
        try{
        repository.create(person).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
           Assertions.assertEquals("Email already exists", ex.getCause().getMessage());
        }
    }

    @Test
    @DisplayName("When create is called then return Business exception with Unknown error")
    public void create_get_Person_throwUnknown (){
        PersonRepository repository = spy(PersonRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(12000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).persist(Mockito.any(Person.class));
        try{
            repository.create(person).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
            Assertions.assertEquals("Unknown error", ex.getCause().getMessage());
        }
    }

    @Test
    @DisplayName("When create is called then return Business exception with Unknown error")
    public void getPersonByEmail_getUniPerson (){
        PersonRepository repository = spy(PersonRepository.class);
        ReactivePanacheQuery<Person> reactivePersonQuery = Mockito.mock(ReactivePanacheQuery.class);
        Mockito.doReturn(reactivePersonQuery)
                .when(repository).find("emailAddress", "someEmail@test.com");
        Mockito.doReturn(Uni.createFrom().item(person))
                .when(reactivePersonQuery).firstResult();
        Person p = repository.getPersonByEmail("someEmail@test.com").await().atMost(Duration.ofSeconds(3));
        Assertions.assertEquals(p.getFirstName(),"Test name");

    }

    @Test
    @DisplayName("When Person by id is called then return Business exception with Unknown error")
    public void getPersonById_getUniPerson (){
        PersonRepository repository = spy(PersonRepository.class);
        Mockito.doReturn(Uni.createFrom().item(person))
                .when(repository).findById(any(ObjectId.class));
        Person p = repository.getPersonById("6291c6ad7e0450024af5c81a").await().atMost(Duration.ofSeconds(3));
        Assertions.assertEquals(p.getFirstName(),"Test name");

    }
}