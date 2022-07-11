package com.eventu.repository;

import com.eventu.vo.Address;
import com.eventu.vo.Event;
import com.eventu.vo.SubEvent;
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
    Address address;

    @BeforeEach
    public void setUp(){
        event = new Event();
        event.setEventType(Type.PARTY);
        event.setOrgUrl("http://test.com");
        event.setOrgName("Test org");
        event.setPersonId(new ObjectId("6291c6ad7e0450024af5c81a"));
        event.setId(new ObjectId("5291c6ad7e0450024af5c82a"));
        event.setEventName("Test event");

        address = new Address();
        address.setId(new ObjectId());
        address.setAddressLine_1("Line 1 Adress");
        address.setAddressLine_2("Line 2 address");
        address.setCity("Charlotte");
        address.setState("NC");
        address.setZipCode("28262");
        event.setAddress(address);

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

    @Test
    @DisplayName("When Subevent is successfully added to Event then return updated event")
    void addEventSummary_happy_path(){
        SubEvent subEvent = new SubEvent();
        subEvent.setId(new ObjectId("6291c6ad7e0450024af5c81a"));
        EventRepository repository = spy(EventRepository.class);
        event.setId(new ObjectId("6291c6ad7e0450024af5c81a"));
        Mockito.doReturn(Uni.createFrom().item( event)).when(repository).findById(event.getId());
        Mockito.doReturn(Uni.createFrom().item( event)).when(repository).persistOrUpdate(event);
        Event e = repository.addEventSummary(event, subEvent).await().atMost(Duration.ofSeconds(1));
        Assertions.assertEquals(1, e.getSubEventSummaryMap().size());
    }

    @Test
    @DisplayName("When EventID is not found then throw exception")
    void addEventSummary_negative_path(){
        EventRepository repository = spy(EventRepository.class);
        SubEvent subEvent = new SubEvent();
        subEvent.setId(new ObjectId("6291c6ad7e0450024af5c81a"));
        event.setId(new ObjectId("6291c6ad7e0450024af5c81a"));
        Mockito.doReturn(Uni.createFrom().item(event)).when(repository).findById(event.getId());
        Mockito
                .doReturn(Uni.createFrom().failure(new MongoWriteException(new WriteError(12000, "Cannot update the event", new BsonDocument()),null)))
                .when(repository).persistOrUpdate(event);

        try {
            Event e = repository.addEventSummary(event, subEvent).await().atMost(Duration.ofSeconds(1));
        }catch (Exception ex){
            Assertions.assertEquals("Unknown error", ex.getCause().getMessage());
        }

    }

    @Test
    @DisplayName("addAddress() updates the address to event object")
    void addAddress_happy_path(){
        EventRepository repository = spy(EventRepository.class);
        Mockito.doReturn(Uni.createFrom().item( event)).when(repository).findById(event.getId());
        event.setVenueAddress(new ObjectId());
        Mockito.doReturn(Uni.createFrom().item(event)).when(repository).persistOrUpdate(event);
        Event ev = repository.addAddress(event, address).await().atMost(Duration.ofSeconds(2));
        Assertions.assertNotNull(ev.getVenueAddress());
    }

    @Test
    @DisplayName("addAddress() unable to update the address to event")
    void addAddress_exception(){
        EventRepository repository = spy(EventRepository.class);
        Mockito.doReturn(Uni.createFrom().item( event)).when(repository).findById(event.getId());
        event.setVenueAddress(new ObjectId());
        Mockito.doReturn(Uni.createFrom().failure(new MongoWriteException(new WriteError(11000, "Cannot update the event", new BsonDocument()),null)))
                .when(repository).persistOrUpdate(event);
        try{
        Event ev = repository.addAddress(event, address).await().atMost(Duration.ofSeconds(2));
    }catch (Exception ex){
        Assertions.assertEquals("Cannot update the address to event", ex.getCause().getMessage());
    }
    }

}