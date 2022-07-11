package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@MongoEntity(collection="SubEvent")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class SubEvent extends ReactivePanacheMongoEntity {
    private ObjectId id;
    private String eventName;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String speakerName;
    private String organizerName;
    private String organizationName;
    private String eventUrl;
    private String orgUrl;
    private String eventDescription;
    private ObjectId eventId;
}
