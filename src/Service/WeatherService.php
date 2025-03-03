<?php

namespace App\Service;

use GuzzleHttp\Client;

class WeatherService
{
    private Client $client;
    private string $apiKey;

    public function __construct()
    {
        $this->client = new Client();
        $this->apiKey = '03591636190d3efe3abea67f698552c3'; // Remplace par ta clé API OpenWeatherMap
    }

    public function getWeather(string $city): array
    {
        $url = "http://api.openweathermap.org/data/2.5/weather?q={$city}&appid={$this->apiKey}&units=metric&lang=fr";

        try {
            $response = $this->client->get($url);
            $data = json_decode($response->getBody(), true);
            return $data;
        } catch (\Exception $e) {
            return ['error' => 'Impossible de récupérer la météo'];
        }
    }
}
