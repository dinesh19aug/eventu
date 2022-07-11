package com.eventu.repository;

import com.eventu.vo.Address;
import com.eventu.vo.Event;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;
import io.smallrye.mutiny.Uni;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class AddressRepositoryTest {

    Address address;
    Event event;
    @BeforeEach
    void setUp() {
        event = new Event();
        address = new Address();
        address.setId(new ObjectId());
        address.setAddressLine_1("Line 1 address");
        address.setAddressLine_2("Line 2 address");
        address.setCity("Charlotte");
        address.setState("NC");
        address.setZipCode("28262");
        event.setAddress(address);
    }

    @Test
    void create_happy_path() {
        AddressRepository repository = spy(AddressRepository.class);
        doReturn(Uni.createFrom().item(address)).when(repository).persistOrUpdate(address);
        Address result = repository.create(event).await().indefinitely();
        Assertions.assertEquals("Line 1 address", result.getAddressLine_1());
    }

    @Test
    @DisplayName("Error creating new address")
    void create_negative_path() {
        AddressRepository repository = spy(AddressRepository.class);
        doReturn(Uni.createFrom().failure(new MongoWriteException(new WriteError(11000, "Error creatingAddress", new BsonDocument()),null)))
                .when(repository).persistOrUpdate(address);
        try {
            Address result = repository.create(event).await().indefinitely();
        }catch (Exception ex){
            Assertions.assertEquals("Error creatingAddress", ex.getCause().getMessage());
        }

    }
}