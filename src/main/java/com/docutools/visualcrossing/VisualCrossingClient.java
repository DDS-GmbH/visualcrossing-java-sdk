package com.docutools.visualcrossing;

import com.docutools.visualcrossing.gson.RecordTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

/**
 * <a href="https://www.visualcrossing.com/weather-api">Visual Crossing Weather</a> API client that executes
 * {@link VisualCrossingRequest}s against the configured API and returns {@link VisualCrossingResponse}s.
 *
 * @since 2021-09-13
 * @author amp
 */
public record VisualCrossingClient(String protocol,
                                   String host,
                                   String basePath,
                                   String apiKey,
                                   HttpClient httpClient,
                                   Function<String, VisualCrossingResponse> parser) {

    private static final String DEFAULT_PROTOCOL = "HTTPS";
    private static final String DEFAULT_HOST = "weather.visualcrossing.com";
    private static final String DEFAULT_BASE_PATH = "/VisualCrossingWebServices/rest/services/timeline";

    public VisualCrossingResponse execute(VisualCrossingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot execute NULL request.");
        }

        var httpRequest = HttpRequest.newBuilder(request.toURI(protocol, host, basePath, apiKey))
                .GET()
                .build();

        try {
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            var statusCode = httpResponse.statusCode();
            var body = httpResponse.body();
            var contentType = httpResponse.headers().firstValue("content-type").orElse("");

            if (statusCode != 200) {
                throw new VisualCrossingException("Unexpected status code %d (body=%s)".formatted(statusCode, body));
            }
            if (!"application/json".equals(contentType)) {
                throw new VisualCrossingException("Unsupported response content type %s".formatted(contentType));
            }

            try {
                return parser.apply(body);
            } catch (Exception e) {
                throw new VisualCrossingException("Could not parse response %s".formatted(contentType), e);
            }
        } catch (IOException e) {
            throw new VisualCrossingException("Could not execute visual crossing request %s".formatted(request), e);
        } catch (InterruptedException interrupt) {
            Thread.currentThread().interrupt();
            return null; // DEAD
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String protocol = DEFAULT_PROTOCOL;
        private String host = DEFAULT_HOST;
        private String basePath = DEFAULT_BASE_PATH;
        private String apiKey;
        private HttpClient httpClient;
        private Function<String, VisualCrossingResponse> jsonParser;

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder basePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder jsonParser(Function<String, VisualCrossingResponse> jsonParser) {
            this.jsonParser = jsonParser;
            return this;
        }

        public VisualCrossingClient build() {
            if(apiKey == null) {
                throw new VisualCrossingException("apiKey required");
            }
            if(httpClient == null) {
                httpClient = HttpClient.newBuilder().build();
            }
            if(jsonParser == null) {
                jsonParser = str -> {
                    var gson = new GsonBuilder()
                            .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                            .create();
                    return gson.fromJson(str, VisualCrossingResponse.class);
                };
            }
            return new VisualCrossingClient(protocol, host, basePath, apiKey, httpClient, jsonParser);
        }
    }
}