package com.docutools.visualcrossing;

import org.json.JSONObject;

public class Alert {

    private final String event;
    private final String description;

    public Alert(JSONObject json) {
        this.event = json.optString("event");
        this.description = json.optString("description");
    }

    public String getEvent() {
        return event;
    }

    public String getDescription() {
        return description;
    }
}
