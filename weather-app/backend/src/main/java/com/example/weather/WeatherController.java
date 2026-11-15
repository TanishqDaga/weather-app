package com.example.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Looks up a city and its current weather using the free Open-Meteo API (no API key needed). */
@RestController
@RequestMapping("/api")
public class WeatherController {

    private final RestTemplate http = new RestTemplate();

    @GetMapping("/weather")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> weather(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam String city) {

        if (!AuthController.isValid(auth)) {
            return ResponseEntity.status(401).body(Map.of("error", "Please log in"));
        }

        try {
            // 1) City name -> coordinates
            String geoUrl = UriComponentsBuilder
                    .fromHttpUrl("https://geocoding-api.open-meteo.com/v1/search")
                    .queryParam("name", city)
                    .queryParam("count", 1)
                    .build().toUriString();
            Map<String, Object> geo = http.getForObject(geoUrl, Map.class);
            List<Map<String, Object>> results = geo == null ? null : (List<Map<String, Object>>) geo.get("results");
            if (results == null || results.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "City not found"));
            }
            Map<String, Object> place = results.get(0);

            // 2) Coordinates -> current weather
            String wxUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                    .queryParam("latitude", place.get("latitude"))
                    .queryParam("longitude", place.get("longitude"))
                    .queryParam("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code")
                    .build().toUriString();
            Map<String, Object> wx = http.getForObject(wxUrl, Map.class);
            Map<String, Object> current = (Map<String, Object>) wx.get("current");

            Map<String, Object> out = new HashMap<>();
            out.put("city", place.get("name"));
            out.put("country", place.get("country"));
            out.put("temperature", current.get("temperature_2m"));
            out.put("humidity", current.get("relative_humidity_2m"));
            out.put("windSpeed", current.get("wind_speed_10m"));
            out.put("condition", describe(((Number) current.get("weather_code")).intValue()));
            return ResponseEntity.ok(out);

        } catch (Exception e) {
            return ResponseEntity.status(502).body(Map.of("error", "Weather service unavailable. Try again."));
        }
    }

    private String describe(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77) return "Snow";
        if (code >= 80 && code <= 82) return "Rain showers";
        if (code >= 95) return "Thunderstorm";
        return "Unknown";
    }
}
