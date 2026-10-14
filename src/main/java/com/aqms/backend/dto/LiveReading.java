package com.aqms.backend.dto;

/**
 * A live air quality reading for one place, fetched from the Open-Meteo API.
 * Pollutant values are in micrograms per cubic metre; any of them may be null
 * if the data source has no value for that pollutant at that location.
 */
public record LiveReading(
        String city,
        String region,
        String country,
        double latitude,
        double longitude,
        int aqi,
        String category,
        Double pm25,
        Double pm10,
        Double co,
        Double no2,
        Double so2,
        Double o3,
        String observedAt
) {
}
