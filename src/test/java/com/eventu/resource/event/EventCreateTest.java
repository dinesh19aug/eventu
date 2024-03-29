package com.eventu.resource.event;

import com.eventu.repository.AddressRepository;
import com.eventu.repository.EventRepository;
import com.eventu.repository.PersonRepository;
import com.eventu.vo.Address;
import com.eventu.vo.Event;
import com.eventu.vo.Person;
import com.eventu.vo.Type;
import com.mongodb.MongoException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
class EventCreateTest {

    Event event;
    @InjectMock
    PersonRepository personRepository;

    @InjectMock
    EventRepository eventRepository;

    @InjectMock
    AddressRepository addressRepository;

    @BeforeEach
    public void setUp(){
        event = new Event();
        event.setEventName("Test Event1");
        event.setEventType(Type.PARTY);
        event.setOrgName("Test org");
        event.setEventUrl("http://testevent.com");
        event.setOrgUrl("http://testOrg.com");

    }

    @Test
    @DisplayName("Create a new event WHEN person Id exist and event is brand new")
    void createEvent() {

        Mockito.when(eventRepository.create(any(Event.class), any(ObjectId.class))).thenReturn(Uni.createFrom().item(event));
        Person mockPerson = Mockito.mock(Person.class);
        Mockito.when(personRepository.getPersonById(any(String.class))).thenReturn(Uni.createFrom().item(mockPerson));
        Mockito.when(mockPerson.getId()).thenReturn(new ObjectId("6291c6ad7e0450024af5c81a"));
        Address mockAddress = Mockito.mock(Address.class);
        Mockito.when(addressRepository.create(any())).thenReturn(Uni.createFrom().item( mockAddress));
        Mockito.when(eventRepository.addAddress(any(Event.class), any(Address.class))).thenReturn(Uni.createFrom().item(event));
        given().body(event).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/person/6291c6ad7e0450024af5c82a/event")
                .then()
                .statusCode(200)
                .body("eventName", is("Test Event1"))    ;
    }

    @Test
    @DisplayName("When person Id does not exist then show error message 'Person does not exist'")
    void createEvent_person_exist() {

        Mockito.when(personRepository.getPersonById(any(String.class))).thenReturn(Uni.createFrom().nullItem());
       given().body(event).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/person/6291c6ad7e0450024af5c82a/event")
                .then()
                .statusCode(500)
                .body("status", is("Error")).and().body("error.errorDesc", is("Person does not exist"))    ;
    }

    @Test
    @DisplayName("When person Id exist but event name already created then show error message 'Event already exists'")
    void createEvent_event_exist() {
        Mockito.when(eventRepository.create(any(Event.class), any(ObjectId.class))).thenReturn(Uni.createFrom().failure(new MongoException(11000, "Event already exists")));
        Person mockPerson = Mockito.mock(Person.class);
        Mockito.when(personRepository.getPersonById(any(String.class))).thenReturn(Uni.createFrom().item(mockPerson));
        Mockito.when(mockPerson.getId()).thenReturn(new ObjectId("6291c6ad7e0450024af5c81a"));
        given().body(event).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/person/6291c6ad7e0450024af5c82a/event")
                .then()
                .statusCode(500)
                .body("status", is("Error")).and().body("error.errorDesc", is("Event already exists"))    ;
    }
}