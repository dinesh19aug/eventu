package com.eventu.vo;

public enum Type {
    BIRTHDAY("BIRTHDAY"), PARTY("PARTY"), CONFERENCE("CONFERENCE"), WEDDING("WEDDING"), ANNIVERSARY("ANNIVERSARY"), TALK("TALK");

    private String value;

    Type(String value) {
        this.value = value;
    }
}
