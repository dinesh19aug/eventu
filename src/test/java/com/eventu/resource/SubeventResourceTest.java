package com.eventu.resource;

import com.eventu.repository.EventRepository;
import com.eventu.repository.SubeventRepository;
import com.eventu.vo.Event;
import com.eventu.vo.SubEvent;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class SubeventResourceTest {

    SubEvent subEvent;
    @InjectMock
    SubeventRepository subeventRepository;

    @InjectMock
    EventRepository eventRepository;

    @BeforeEach
    public void setUp(){
        subEvent = SubEvent.builder()
                //.id(new ObjectId("5291c6ad7e0450024af5c82a"))
                .eventName("Hands on Java workshop")
                .eventStartDate( LocalDate.of(2022,03,12))
                .eventEndDate( LocalDate.of(2022,03,30))
                .startTime(LocalDateTime.now().toLocalTime())
                .endTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES).toLocalTime())
                .speakerName("Test Speaker")
                .organizationName("Test Org")
                .organizerName("Test organizer")
                .eventUrl("http://test.com")
                .orgUrl("http://test.com")
                .eventDescription(" Test event description")
                .eventId(new ObjectId("6291c6ad7e0450024af5c81a"))
                .build();
    }

    @Test
    @DisplayName("When eventId exists then add subevent and return Subevent")
    void create_happy_path(){
        Mockito.when(eventRepository.findById(any(ObjectId.class))).thenReturn(Uni.createFrom().item(new Event()));
        Mockito.when(subeventRepository.create(any(), any(ObjectId.class))).thenReturn(Uni.createFrom().item(subEvent));
        Mockito.when(eventRepository.addEventSummary(any(Event.class), any(SubEvent.class))).thenReturn(Uni.createFrom().item(new Event()));
        given().body(subEvent).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .post("/party/event/6291c6ad7e0450024af5c82a/subEvent")
                .then()
                .statusCode(200);

    }

    @Test
    @DisplayName("When eventId exists but subevent is duplicate(index= eventId + SubeventName must be unique) Then throw Subevent already exits")
    void create_event_not_updated(){
        Event mockEvent = Mockito.mock(Event.class);
        Mockito.when(mockEvent.getId()). thenReturn(new ObjectId("6291c6ad7e0450024af5c82a"));
        Mockito.when(eventRepository.findById(any(ObjectId.class))).thenReturn(Uni.createFrom().item(mockEvent));
        Mockito.when(subeventRepository.persist(any(SubEvent.class)))
                .thenReturn(Uni.createFrom().failure(new MongoException(11000, "SubEvent already exists")));
        Mockito.when(subeventRepository.createErrorMessage(any(),any())).thenReturn("SubEvent already exists");
        Mockito.when(subeventRepository.create(any(SubEvent.class), any(ObjectId.class)))
                .thenCallRealMethod();

        given().body(subEvent)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/party/event/6291c6ad7e0450024af5c82a/subEvent")

                .then()
                .body("error.errorDesc", is("SubEvent already exists"))
                .statusCode(500);

    }

    @Test
    @DisplayName("When eventId does not exists Then return Event does not exist")
    void create_event_not_exist(){
        Mockito.when(eventRepository.findById(any(ObjectId.class))).thenReturn(Uni.createFrom().nullItem());
        given().body(subEvent).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/event/6291c6ad7e0450024af5c82a/subEvent")
                .then()
                .body("error.errorDesc", is("Event does not exist"))
                .statusCode(500);

    }
}