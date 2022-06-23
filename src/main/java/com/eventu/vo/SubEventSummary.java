package com.eventu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class SubEventSummary {
    private String eventName;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
