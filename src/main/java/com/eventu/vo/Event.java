package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.Map;

@Data
@MongoEntity(collection="Event")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
public class Event extends ReactivePanacheMongoEntity {

    public ObjectId id;
    public String eventName;
    public Type eventType;
    public  String orgName;
    public String orgUrl;
    public String eventUrl;
    public ObjectId personId;
    public Map<String, SubEventSummary> subEventSummaryMap;
    public ObjectId venueAddress;
    @BsonIgnore
    private Address address;

}


