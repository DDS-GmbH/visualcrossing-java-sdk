package com.docutools.visualcrossing;

public record VisualCrossingResponse(int queryCost,
                                     double latitude,
                                     double longitude,
                                     String resolvedAddress,
                                     String address,
                                     String timezone,
                                     int tzoffset,
                                     String description,
                                     DayRecording[] days) {
}
