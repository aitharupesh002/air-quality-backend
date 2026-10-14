package com.aqms.backend.controller;

import com.aqms.backend.dto.LiveReading;
import com.aqms.backend.exception.BadRequestException;
import com.aqms.backend.service.LiveAirQualityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Live (not saved) air quality lookups. The frontend shows the result and
 * can then save it through the normal POST /api/records endpoint.
 */
@RestController
@RequestMapping("/api/live")
public class LiveAirQualityController {

    private final LiveAirQualityService service;

    public LiveAirQualityController(LiveAirQualityService service) {
        this.service = service;
    }

    // GET /api/live?lat=17.38&lon=78.48&city=Hyderabad   (city is optional, label only)
    @GetMapping
    public LiveReading byCoordinates(@RequestParam("lat") double lat,
                                     @RequestParam("lon") double lon,
                                     @RequestParam(value = "city", required = false) String city) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new BadRequestException(
                    "Latitude must be between -90 and 90, and longitude between -180 and 180.");
        }
        return service.fetchByCoordinates(lat, lon, city, null, null);
    }

    // GET /api/live/search?city=Hyderabad
    @GetMapping("/search")
    public LiveReading byCity(@RequestParam("city") String city) {
        if (city.isBlank()) {
            throw new BadRequestException("Enter a city name.");
        }
        return service.fetchByCity(city);
    }

}
