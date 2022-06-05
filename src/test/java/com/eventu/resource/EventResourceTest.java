package com.eventu.resource;

import com.eventu.vo.Event;
import com.eventu.vo.Type;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


@QuarkusTest
class EventResourceTest {

    Event event;

    @BeforeEach
    public void setUp(){
        event = Event.builder().eventName("Test Event")
                .eventType(Type.PARTY)
                .orgName("Test org")
                .eventUrl("http://testevent.com")
                .orgUrl("http://testOrg.com")
                .build();
    }

    @Test
    void createEvent() {
        PanacheMock.mock(Event.class);
        PanacheMock.doReturn(Uni.createFrom().item(event)).when(Event.class).persist();
        given().body(event).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post("/party/person/1/event")
                .then()
                .statusCode(200)
                .body("eventName", is("Test Event"))    ;
    }
}