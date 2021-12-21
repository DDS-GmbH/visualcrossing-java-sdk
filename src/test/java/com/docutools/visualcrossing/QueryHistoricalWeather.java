package com.docutools.visualcrossing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

@DisplayName("Query historical temperature")
public class QueryHistoricalWeather {

    private String key;

    @BeforeEach
    void setup() {
        key = System.getenv("VC_KEY");
    }

    @Test
    @DisplayName("Get temperature for Vienna, AUT for christmas 2020")
    void shouldGetViennaTempForChristmas2020() {
        // Arrange
        String streetAddress = "Stephansplatz 1";
        String zipCode = "1010";
        String country = "AT";
        Instant date = Instant.parse("2020-12-15T13:00:00Z");

        VisualCrossingClient vcc = VisualCrossingClient.builder()
                .apiKey(key)
                .build();

        VisualCrossingRequest request = VisualCrossingRequest.builder()
                .address(String.format("%s, %s %s", streetAddress, zipCode, country))
                .timestamp(date)
                .elements("temp", "description", "icon")
                .include(VisualCrossingSections.DAYS)
                .unitGroup(VisualCrossingUnitGroups.METRIC)
                .build();

        // Act
        VisualCrossingResponse response = vcc.execute(request);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getQueryCost());

        DayRecording[] days = response.getDays();
        Assertions.assertNotNull(days);
        Assertions.assertEquals(1, days.length);

        DayRecording day = days[0];
        Assertions.assertNotNull(day);
        Assertions.assertEquals(3.7, day.getTemp());
        Assertions.assertEquals("clear-day", day.getIcon());
        Assertions.assertEquals("Clear conditions throughout the day.", day.getDescription());
    }

}
