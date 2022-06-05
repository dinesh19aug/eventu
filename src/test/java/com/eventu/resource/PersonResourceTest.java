package com.eventu.resource;

import com.eventu.repository.PersonRepository;
import com.eventu.vo.Address;
import com.eventu.vo.Person;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@QuarkusTest
class PersonResourceTest {

     @InjectMock
    PersonRepository repository;
    @Inject
    PersonResource resource;
    Uni<Person> uniPerson;
    Person person;
    @BeforeEach
    public  void setUp(){
        person =  Person.builder().address(Address.builder()
                        .addressLine_1("1000 Test dr")
                        .addressLine_2("opposite crescent dr").suiteNumber("Ste. 200").city("testCity")
                        .countryCode("US").state("CA")
                        .build()).emailAddress("test@test.com").firstName("John").lastName("Barista")
                .mobileNumber("70444444444")

                .id(new ObjectId())
                .build();
       uniPerson = Uni.createFrom().item(person);

        when(repository.persist(any(Person.class))).thenReturn(uniPerson);

    }
   // THIS TEST IS WORKING
    @Test
    @DisplayName("When a new person object is passed then person object with Id should be returned")
    void create_success() {
        final Uni<Response> p = resource.create(person);
        Person respPerson = (Person) p.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted().getItem().getEntity();
        Assertions.assertNotNull(p);
        Assertions.assertEquals(person, respPerson);
    }



    /**
     * THIS ONE FAILS WITH at line 63 with the message
     * org.mockito.exceptions.base.MockitoException:
     * Checked exception is invalid for this method!
     * Invalid: com.eventu.exception.BusinessException
     */
    /*@Test
    @DisplayName("When a new person object is passed but email exists then AStatus object with error description")
    void create_duplicate_emailId() {
        //doThrow(MongoWriteException.class).when(repository).persist(any(Person.class));
        when(repository.persist(any(Person.class))).thenThrow(Uni); /// FAILS HERE
        final Uni<Response> p = resource.create(person);
        Person respPerson = (Person) p.subscribe().withSubscriber(UniAssertSubscriber.create()).assertCompleted().getItem().getEntity();
        Assertions.assertNotNull(p);
        Assertions.assertEquals(person, respPerson);
    }*/


}