package com.docutools.visualcrossing;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * <a href="https://www.visualcrossing.com/weather-api">Visual Crossing Weather</a> API client that executes
 * {@link VisualCrossingRequest}s against the configured API and returns {@link VisualCrossingResponse}s.
 *
 * @since 2021-09-13
 * @author amp
 */
public class VisualCrossingClient {

    private static final String DEFAULT_PROTOCOL = "HTTPS";
    private static final String DEFAULT_HOST = "weather.visualcrossing.com";
    private static final String DEFAULT_BASE_PATH = "/VisualCrossingWebServices/rest/services/timeline";

    private final String protocol;
    private final String host;
    private final String basePath;
    private final String apiKey;

    public VisualCrossingClient(String protocol, String host, String basePath, final String apiKey) {
        this.protocol = protocol;
        this.host = host;
        this.basePath = basePath;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("apiKey must not be null or empty");
        }
        this.apiKey = apiKey.trim();
    }

    public VisualCrossingResponse execute(VisualCrossingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot execute NULL request.");
        }

        HttpURLConnection connection = request.openConnection(protocol, host, basePath, apiKey);
        try {
            connection.connect();
            int responseCode = connection.getResponseCode();
            String content = readContentAsString(connection);
            if (responseCode != 200) {
                throw new VisualCrossingException(String.format("Unexpected response code %d (content=%s)", responseCode, content));
            }
            String contentType = connection.getHeaderField("Content-Type");
            if (!"application/json".equals(contentType)) {
                throw new VisualCrossingException(String.format("Unsupported response content type %s", contentType));
            }
            JSONObject resultJson = new JSONObject(content);
            return new VisualCrossingResponse(resultJson);
        } catch (IOException e) {
            throw new VisualCrossingException("Failed to executed request.", e);
        } finally {
            connection.disconnect();
        }
    }

    private static String readContentAsString(URLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            do {
                line = in.readLine();
                if (line != null)
                    sb.append(line).append("\n");
            } while (line != null);
            return sb.toString();
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

        public VisualCrossingClient build() {
            if(apiKey == null) {
                throw new VisualCrossingException("apiKey required");
            }
            return new VisualCrossingClient(protocol, host, basePath, apiKey);
        }
    }
}