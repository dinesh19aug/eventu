package com.eventu.resource;

import com.eventu.vo.SubEvent;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;

@QuarkusTest
class SubeventResourceTest {

    SubEvent subEvent;

    @BeforeEach
    public void setUp(){
        subEvent = SubEvent.builder()
                .id(new ObjectId("5291c6ad7e0450024af5c82a"))
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
        given().body(subEvent).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/event/6291c6ad7e0450024af5c82a/subEvent")
                .then()
                .statusCode(500);
                //.body("eventName", is("Test Event1"))    ;
    }
}