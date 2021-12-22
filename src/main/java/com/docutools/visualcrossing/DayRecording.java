package com.docutools.visualcrossing;

import org.json.JSONObject;

public class DayRecording {

    private final Double cloudcover;
    private final String conditions;
    private final String description;
    private final Double temp;
    private final Double feelslike;
    private final Double humidity;
    private final Double pressure;
    private final Double snow;
    private final String icon;

    public DayRecording(JSONObject object) {
        cloudcover = object.optDouble("cloudcover");
        conditions = object.optString("conditions");
        description = object.optString("description");
        temp = object.optDouble("temp");
        feelslike = object.optDouble("feelslike");
        humidity = object.optDouble("humidity");
        pressure = object.optDouble("pressure");
        snow = object.optDouble("snow");
        icon = object.optString("icon");
    }

    public Double getCloudcover() {
        return cloudcover;
    }

    public String getConditions() {
        return conditions;
    }

    public String getDescription() {
        return description;
    }

    public Double getTemp() {
        return temp;
    }

    public Double getFeelslike() {
        return feelslike;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getSnow() {
        return snow;
    }

    public String getIcon() {
        return icon;
    }
}
