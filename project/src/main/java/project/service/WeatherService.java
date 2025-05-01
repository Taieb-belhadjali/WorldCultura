package project.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.json.JSONException;

public class WeatherService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String OPEN_WEATHER_MAP_API_KEY = "03591636190d3efe3abea67f698552c3"; // Remplace par ta clé API
    private static final String OPEN_WEATHER_MAP_API_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String UNITS = "metric";
    private static final String LANGUAGE = "fr";

    public CompletableFuture<String> getWeatherForCity(String city) {
        String fullUrl = OPEN_WEATHER_MAP_API_URL + city + "&appid=" + OPEN_WEATHER_MAP_API_KEY + "&units=" + UNITS + "&lang=" + LANGUAGE;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseWeatherJson);
    }

    private String parseWeatherJson(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has("weather") && jsonObject.getJSONArray("weather").length() > 0) {
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                if (jsonObject.has("main")) {
                    double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                    return String.format("%s, %.1f °C", description, temperature);
                } else {
                    return description;
                }
            } else if (jsonObject.has("cod") && jsonObject.getInt("cod") != 200) {
                return "Erreur météo: " + jsonObject.getString("message");
            } else {
                return "Météo non disponible";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "Erreur lors du traitement des données météo";
        }
    }
}