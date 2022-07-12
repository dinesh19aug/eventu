package com.eventu.repository;

import com.eventu.vo.SubEvent;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class SubeventRepositoryTest {

    SubEvent subEvent;
    SubEvent newSubevent;

    @BeforeEach
    void setUp() {
        subEvent = new SubEvent(new ObjectId("5291c6ad7e0450024af5c82a"),"Hands on Java workshop",
                LocalDate.of(2022,03,12),
                LocalDate.of(2022,03,30),
                LocalDateTime.now().toLocalTime(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES).toLocalTime(),
                "Test Speaker","Test organizer","Test Org",
                "http://eventUrl.com","http://orgUrl.com",
                " Test event description",new ObjectId("6291c6ad7e0450024af5c81a"));

    newSubevent = new SubEvent(null,"UPDATED::Hands on Java workshop",LocalDate.of(2022,03,12),
                LocalDate.of(2022,03,30),
                LocalDateTime.now().toLocalTime(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES).toLocalTime(),
                "Test Speaker","Test organizer","Test Org","http://eventUrl.com","http://orgUrl.com",
                " Test event description",new ObjectId("6291c6ad7e0450024af5c81a"));

    }

    @Test
    @DisplayName("Subevent should be returned after persisting")
    void create_happyPath() {
        SubeventRepository repository = spy(SubeventRepository.class);
        doReturn(Uni.createFrom().item(subEvent)).when(repository).persist(any(SubEvent.class));
        SubEvent subEventResponse = repository.create(subEvent, new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(1));
        Assertions.assertEquals("Hands on Java workshop", subEventResponse.getEventName());
    }

    @Test
    @DisplayName("Return a error when the Subevent is duplicate ")
    void create_return_Subevent_exists(){
        SubeventRepository repository = spy(SubeventRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(11000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).persist(Mockito.any(SubEvent.class));
        try{
            repository.create(subEvent,new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
            Assertions.assertEquals("SubEvent already exists", ex.getCause().getMessage());
        }
    }

    @Test
    @DisplayName("Return an unknown error when the exception is not 11000 ")
    void create_return_unknown_error(){
        SubeventRepository repository = spy(SubeventRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(12000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).persist(Mockito.any(SubEvent.class));
        try{
            repository.create(subEvent,new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(3));
        }catch (Exception ex){
            Assertions.assertEquals("Unknown error", ex.getCause().getMessage());
        }
    }

    @Test
    @DisplayName("When event to be updated is correctly updated then return update Uni<SubEvent>")
    void updateSubEvent_happyPath(){
        SubeventRepository repository = spy(SubeventRepository.class);
        doReturn(Uni.createFrom().item(subEvent)).when(repository).update(any(SubEvent.class));
        SubEvent result =  repository.updateSubEvent(subEvent, newSubevent).await().indefinitely();
        Assertions.assertEquals(result.getEventName(), "UPDATED::Hands on Java workshop");

    }

    @Test
    @DisplayName("Return a error when the Subevent is duplicate ")
    void updateSubEvent_return_Subevent_exists(){
        SubeventRepository repository = spy(SubeventRepository.class);
        Mockito.doReturn(Uni.createFrom().failure( new MongoWriteException(new WriteError(11000, "Duplicate value", new BsonDocument()),null)))
                .when(repository).update(any(SubEvent.class));
        try{
            repository.updateSubEvent(subEvent, newSubevent).await().indefinitely();
        }catch (Exception ex){
            Assertions.assertEquals("Unable to update the Subevent", ex.getCause().getMessage());
        }
    }
}