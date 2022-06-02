package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import org.bson.types.ObjectId;


@Data
@MongoEntity(collection="Person")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Person extends AStatus{
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String emailAddress;
    private Address address;
}
