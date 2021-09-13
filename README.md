# visual crossing java (8) sdk

Simple Java SDK for [the visual crossing weather API](https://www.visualcrossing.com/).

## Getting Started

The current version is hosted on Github Packages:

```xml
&lt;dependency&gt;&#10;  &lt;groupId&gt;com.docutools&lt;/groupId&gt;&#10;  &lt;artifactId&gt;visualcrossing-sdk&lt;/artifactId&gt;&#10;  &lt;version&gt;2021-09-13&lt;/version&gt;&#10;&lt;/dependency&gt;
```

## Usage

```java
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
                .elements("temp")
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
    }

}

```

## Contribute

Feel free to raise any pull request or open an issue. This library is actively maintained and used
by [docu tools](https://docu-tools.com).