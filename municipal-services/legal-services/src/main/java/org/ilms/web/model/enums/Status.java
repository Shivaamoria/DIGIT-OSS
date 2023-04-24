package org.ilms.web.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    CANCELLED("CANCELLED"),
    REJECTED("REJECTED"),
    DRAFTED("DRAFTED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Status fromValue(String text) {
        for (Status b : Status.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}