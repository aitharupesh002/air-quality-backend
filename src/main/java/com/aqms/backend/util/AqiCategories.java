package com.aqms.backend.util;

/**
 * Maps a US AQI number to its health category name.
 */
public final class AqiCategories {

    private AqiCategories() {
    }

    public static String of(int aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

}
