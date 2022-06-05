package com.eventu.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AStatus {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String status;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public EventError error;

@SneakyThrows
@Override
        public String toString(){

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(this);

}
}
