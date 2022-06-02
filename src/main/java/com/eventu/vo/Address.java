package com.eventu.vo;

import lombok.Data;

import java.util.Locale;


@Data
public class Address {
    private String addressLine_1;
    private String addressLine_2;
    private String roomNumber;
    private String floorNumber;
    private String suiteNumber;
    private String city;
    private String state;
    private String zipCode;
    private Locale.IsoCountryCode countryCode;
}
