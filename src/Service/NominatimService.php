<?php
namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class NominatimService
{
    private HttpClientInterface $client;
    private string $baseUrl = "https://nominatim.openstreetmap.org/search";

    public function __construct(HttpClientInterface $client)
    {
        $this->client = $client;
    }

    public function searchPlaces(string $query): array
    {
        $response = $this->client->request('GET', $this->baseUrl, [
            'query' => [
                'q' => $query,
                'format' => 'json',
                'addressdetails' => 1,
                'limit' => 5, // Limite le nombre de résultats
            ]
        ]);

        return $response->toArray();
    }
}
