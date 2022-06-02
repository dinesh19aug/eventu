package com.eventu.vo;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;

@Data
@MongoEntity(collection="Event")
public class Event {

}
