package com.docutools.visualcrossing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class VisualCrossingResponse {

    private final int queryCost;
    private final double latitude, longitude;
    private final String resolvedAddress, address;
    private final String timezone;
    private final int tzoffset;
    private final String description;
    private final DayRecording[] days;
    private final Alert[] alerts;
    private final CurrentConditions currentConditions;

    public VisualCrossingResponse(JSONObject json) {
        queryCost = json.optInt("queryCost", -1);
        latitude = json.optDouble("latitude");
        longitude = json.optDouble("longitude");
        resolvedAddress = json.optString("resolvedAddress");
        address = json.optString("address");
        timezone = json.optString("timezone");
        tzoffset = json.optInt("tzoffset");
        description = json.optString("description");
        days = convertJsonArrayToListOfObjects(json.optJSONArray("days"), DayRecording::new).toArray(new DayRecording[0]);
        alerts = convertJsonArrayToListOfObjects(json.optJSONArray("alerts"), Alert::new).toArray(new Alert[0]);
        currentConditions = new CurrentConditions(json.optJSONObject("currentConditions"));
    }

    public int getQueryCost() {
        return queryCost;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public String getAddress() {
        return address;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getTzoffset() {
        return tzoffset;
    }

    public String getDescription() {
        return description;
    }

    public DayRecording[] getDays() {
        return days;
    }

    public Alert[] getAlerts() {
        return alerts;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    private static <T> List<T> convertJsonArrayToListOfObjects(JSONArray array, Function<JSONObject, T> converter) {
        if(array == null) {
            return Collections.emptyList();
        }
        List<T> results = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            results.add(converter.apply(object));
        }
        return results;
    }
}
