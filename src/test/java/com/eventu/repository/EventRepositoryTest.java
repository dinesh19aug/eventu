package com.eventu.repository;

import com.eventu.vo.Event;
import com.eventu.vo.Type;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class EventRepositoryTest {

    Event event;

    @BeforeEach
    public void setUp(){
        event = Event.builder().eventType(Type.PARTY)
                .orgUrl("http://test.com")
                .orgName("Test org")
                .personId(new ObjectId("6291c6ad7e0450024af5c81a"))
                .id(new ObjectId("5291c6ad7e0450024af5c82a"))
                .eventName("Test event")
                .build();
    }

    @Test
    @DisplayName("Return a new event")
    void create_return_event(){
        EventRepository repository = spy(EventRepository.class);
        //when(repository.persist(any(Event.class))).thenReturn(Uni.createFrom().item(event));
        doReturn(Uni.createFrom().item(event)).when(repository).persist(any(Event.class));
        Event eventResponse = repository.create(event, new ObjectId("6291c6ad7e0450024af5c81a")).await().atMost(Duration.ofSeconds(1));
        Assertions.assertEquals("Test event", eventResponse.getEventName());
    }

}