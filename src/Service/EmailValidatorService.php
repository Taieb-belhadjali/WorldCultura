<?php

namespace App\Service;

use GuzzleHttp\Client;

class EmailValidatorService
{
    private $apiKey;

    public function __construct(string $apiKey)
    {
        $this->apiKey = $apiKey;
    }

    public function validateEmail(string $email): bool
    {
        $client = new Client();
        $response = $client->request('GET', 'https://api.neverbounce.com/v4/single/check', [
            'query' => [
                'key' => $this->apiKey,
                'email' => $email,
            ]
        ]);

        $data = json_decode($response->getBody()->getContents(), true);

        return isset($data['result']) && $data['result'] === 'valid';
    }
}
