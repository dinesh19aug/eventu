package com.eventu.resource;

import com.eventu.repository.PersonRepository;
import com.eventu.vo.Address;
import com.eventu.vo.Person;
import com.mongodb.MongoException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
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

        when(repository.create(any(Person.class))).thenReturn(uniPerson);

    }
    @Test
    @DisplayName("When a new person object is passed then person object with Id should be returned")
    void create_success() {
        when(repository.create(any(Person.class))).thenReturn(Uni.createFrom().failure(new MongoException(11000, "Email already exists")));
        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(person).post("/party/person")
                .then().statusCode(500)
                .body("error.errorDesc", is("Email already exists"));

    }




    /**
     * THIS ONE FAILS WITH at line 63 with the message
     * org.mockito.exceptions.base.MockitoException:
     * Checked exception is invalid for this method!
     * Invalid: com.eventu.exception.BusinessException
     */
    @Test
    @DisplayName("When a new person object is passed but email exists then AStatus object with error description")
    void create_duplicate_emailId() {

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(person).post("/party/person")
                .then().statusCode(200)
                .body("address.addressLine_1", is("1000 Test dr"));
    }


}