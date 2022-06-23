package com.eventu.repository;

import com.eventu.vo.Event;
import com.eventu.vo.Type;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class EventRepositoryTest {

    Event event;

    @BeforeEach
    public void setUp(){
        event = new Event();
        event.setEventType(Type.PARTY);
        event.setOrgUrl("http://test.com");
        event.setOrgName("Test org");
        event.setPersonId(new ObjectId("6291c6ad7e0450024af5c81a"));
        event.setId(new ObjectId("5291c6ad7e0450024af5c82a"));
        event.setEventName("Test event");

    }

    @Test
    @DisplayName("Return a new event")
    void create_return_event(){
        EventRepository repository = spy(EventRepository.class);
        doReturn(Uni.createFrom().item(event)).when(repository).persist(any(Event.class));
        Event eventResponse = repository.create(event, new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(1));
        Assertions.assertEquals("Test event", eventResponse.getEventName());
    }

    @Test
    @DisplayName("Return a error when the event is duplicate ")
    void create_return_event_exists(){
        EventRepository repository = spy(EventRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(11000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).persist(Mockito.any(Event.class));
        try{
            repository.create(event,new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
            Assertions.assertEquals("Event already exists", ex.getCause().getMessage());
        }
    }

    @Test
    @DisplayName("Return an unknown error when the exception is not 11000 ")
    void create_return_unknown_error(){
        EventRepository repository = spy(EventRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(12000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).persist(Mockito.any(Event.class));
        try{
            repository.create(event,new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
            Assertions.assertEquals("Unknown error", ex.getCause().getMessage());
        }
    }


}