package com.teamE.commonAddsEvents;


public enum Scope  {
    DORMITORY("dormitory"),
    STUDENT("student"),
    OTHER("other");

    private String value;

    Scope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
