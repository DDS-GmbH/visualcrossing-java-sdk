package com.docutools.visualcrossing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VisualCrossingRequest {

    private final String location;
    private final String timeframe;
    private final Map<String, String> params;

    public VisualCrossingRequest(String location, String timeframe, Map<String, String> params) {
        this.location = location;
        this.timeframe = timeframe;
        this.params = params;
    }

    HttpURLConnection openConnection(final String protocol, final String host, final String basePath, final String apiKey) {
        String path = String.format("%s/%s", basePath, encodeValue(location));
        path += buildTimeframePathPart();
        path += buildQueryString(apiKey);
        try {
            URL url = new URL(protocol, host, path);
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new VisualCrossingException(
                    String.format("Could not open connection to API server: protocol=%s, host=%s, path=%s",
                            protocol, host, path), e);
        }
    }

    private String buildTimeframePathPart() {
        return timeframe != null? "/" + timeframe : "";
    }

    private String buildQueryString(final String apiKey) {
        List<String> queryParams = new ArrayList<>();
        queryParams.add("key="+apiKey);
        this.params.forEach((name, value)-> queryParams.add(name + "=" + value));
        String queryString = String.join("&", queryParams);
        return "?" + queryString;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
        private static final DateTimeFormatter TIMEFRAME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        private String location;
        private Instant timestamp;
        private LocalDate from;
        private LocalDate to;
        private VisualCrossingSections[] include;
        private VisualCrossingSupportedLanguages lang;
        private String[] elements;
        private VisualCrossingUnitGroups unitGroup;

        public Builder address(String address) {
            this.location = address;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder timeframe(LocalDate from, LocalDate to) {
            this.from = from;
            this.to = to;
            return this;
        }

        public Builder lang(VisualCrossingSupportedLanguages lang) {
            this.lang = lang;
            return this;
        }

        public Builder elements(String...elements) {
            this.elements = elements;
            return this;
        }

        public Builder include(VisualCrossingSections...sections) {
            this.include = sections;
            return this;
        }

        public Builder unitGroup(VisualCrossingUnitGroups unitGroup) {
            this.unitGroup = unitGroup;
            return this;
        }

        public VisualCrossingRequest build() {
            if(location == null || location.trim().isEmpty()) {
                throw new VisualCrossingException("Cannot build request, missing location parameter (e.g. address).");
            }

            String timeframe = "";
            if(timestamp != null) {
                timeframe = TIMESTAMP_FMT.format(timestamp.atZone(ZoneId.systemDefault()));
            }
            if(this.from != null && this.to != null) {
                timeframe = TIMEFRAME_FMT.format(this.from) + "/" + TIMEFRAME_FMT.format(this.to);
            }

            Map<String, String> queryParams = new HashMap<>();
            if(lang != null)
                queryParams.put("lang", lang.toString().toLowerCase());
            queryParams.put("elements", joinObjectArrayToString(elements));
            queryParams.put("include", joinObjectArrayToString(include));
            if(unitGroup != null)
                queryParams.put("unitGroup", Objects.toString(unitGroup).toLowerCase());

            return new VisualCrossingRequest(location, timeframe, queryParams);
        }

        private static String joinObjectArrayToString(Object[] objects) {
            if(objects == null)
                return "";
            return Arrays.stream(objects)
                    .filter(Objects::nonNull)
                    .map(Objects::toString)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(","));
        }
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new VisualCrossingException("Host system does not support UTF-8.", e);
        }
    }
}
