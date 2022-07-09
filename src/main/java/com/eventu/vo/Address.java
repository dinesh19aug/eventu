package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;


@Data
@MongoEntity(collection="Address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    public ObjectId id;
    private String addressLine_1;
    private String addressLine_2;
    private String roomNumber;
    private String floorNumber;
    private String suiteNumber;
    private String city;
    private String state;
    private String zipCode;
    private String countryCode;
}
