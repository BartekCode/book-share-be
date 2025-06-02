package com.example.book.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genre {
    FICTION("Fiction"),
    NON_FICTION("NonFiction"),
    MYSTERY("Mystery"),
    FANTASY("Fantasy"),
    SCIENCE_FICTION("ScienceFiction"),
    SCIENCE("Science"),
    BIOGRAPHY("Biography"),
    HISTORY("History"),
    ROMANCE("Romance"),
    THRILLER("Thriller"),
    HORROR("Horror"),
    UNDEFINED("Undefined"),
    TECH("Tech");

    private final String code;

    Genre(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Genre fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Genre genre : values()) {
            if (genre.code.equalsIgnoreCase(code)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("No Genre with code: " + code);
    }
}

