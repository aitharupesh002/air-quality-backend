package com.aqms.backend.service;

import com.aqms.backend.dto.LiveReading;
import com.aqms.backend.exception.ExternalServiceException;
import com.aqms.backend.exception.ResourceNotFoundException;
import com.aqms.backend.util.AqiCategories;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Fetches live air quality from the free Open-Meteo APIs (no API key needed
 * for non-commercial use). Air quality data is CAMS ENSEMBLE model output.
 */
@Service
public class LiveAirQualityService {

    private static final String AIR_QUALITY_URL =
            "https://air-quality-api.open-meteo.com/v1/air-quality"
                    + "?latitude={lat}&longitude={lon}"
                    + "&current=us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone"
                    + "&timezone=auto";

    private static final String GEOCODING_URL =
            "https://geocoding-api.open-meteo.com/v1/search?name={name}&count=1&language=en&format=json";

    private final RestClient restClient;

    public LiveAirQualityService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /**
     * Live reading for exact coordinates (used by "Use my location").
     * city / region / country are only labels and may be null.
     */
    public LiveReading fetchByCoordinates(double lat, double lon, String city, String region, String country) {
        JsonNode root = get(AIR_QUALITY_URL, lat, lon);
        JsonNode current = root.path("current");
        Double usAqi = number(current, "us_aqi");

        if (current.isMissingNode() || usAqi == null) {
            throw new ExternalServiceException("No air quality data is available for this location right now.");
        }

        int aqi = (int) Math.round(usAqi);
        String label = (city == null || city.isBlank()) ? "Current location" : city.trim();

        return new LiveReading(
                label,
                region,
                country,
                lat,
                lon,
                aqi,
                AqiCategories.of(aqi),
                number(current, "pm2_5"),
                number(current, "pm10"),
                number(current, "carbon_monoxide"),
                number(current, "nitrogen_dioxide"),
                number(current, "sulphur_dioxide"),
                number(current, "ozone"),
                current.path("time").asText(null)
        );
    }

    /**
     * Live reading for a city name: looks up its coordinates first, then
     * fetches the air quality there.
     */
    public LiveReading fetchByCity(String cityName) {
        String name = cityName.trim();
        JsonNode root = get(GEOCODING_URL, name);
        JsonNode results = root.path("results");

        if (!results.isArray() || results.size() == 0) {
            throw new ResourceNotFoundException("No place found for \"" + name + "\". Check the spelling.");
        }

        JsonNode place = results.get(0);
        return fetchByCoordinates(
                place.path("latitude").asDouble(),
                place.path("longitude").asDouble(),
                place.path("name").asText(name),
                place.path("admin1").asText(null),
                place.path("country").asText(null)
        );
    }

    private JsonNode get(String url, Object... uriVariables) {
        JsonNode body;
        try {
            body = restClient.get().uri(url, uriVariables).retrieve().body(JsonNode.class);
        } catch (RestClientException e) {
            throw new ExternalServiceException(
                    "Could not reach the air quality service. Please try again in a moment.", e);
        }
        if (body == null) {
            throw new ExternalServiceException("The air quality service returned an empty response.");
        }
        return body;
    }

    /** Returns the field as a number rounded to 1 decimal, or null if missing/null. */
    private static Double number(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isNumber()) {
            return null;
        }
        return Math.round(value.asDouble() * 10.0) / 10.0;
    }

}
