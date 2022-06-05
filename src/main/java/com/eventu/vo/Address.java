package com.eventu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
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
