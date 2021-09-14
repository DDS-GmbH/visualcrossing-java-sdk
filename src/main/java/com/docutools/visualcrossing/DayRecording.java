package com.docutools.visualcrossing;

public record DayRecording(Double cloudcover,
                           String conditions,
                           String description,
                           Double temp,
                           Double feelslike,
                           Double humidity,
                           Double pressure,
                           Double snow) {
}
