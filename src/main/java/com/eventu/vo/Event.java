package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
@MongoEntity(collection="Event")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Event extends ReactivePanacheMongoEntity {

    private ObjectId id;
    private String eventName;
    private Type eventType;
    private  String orgName;
    private String orgUrl;
    private String eventUrl;
    private String personId;

}
