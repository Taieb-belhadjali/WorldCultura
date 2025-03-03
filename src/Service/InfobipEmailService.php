<?php

// src/Service/InfobipEmailService.php
namespace App\Service;

use GuzzleHttp\Client;

class InfobipEmailService
{
    private $client;
    private $apiKey;

    public function __construct()
    {
        $this->client = new Client();
        $this->apiKey = $_ENV['INFOBIP_API_KEY'];
    }

    public function sendEmail($to, $subject, $message)
    {
        $url = 'https://api.infobip.com/email/1/send';
        
        $response = $this->client->post($url, [
            'headers' => [
                'Authorization' => 'App ' . $this->apiKey,
                'Content-Type' => 'application/json',
            ],
            'json' => [
                'from' => 'no-reply@tonsite.com',  // Remplace par l'adresse expéditeur
                'to' => $to,
                'subject' => $subject,
                'text' => $message,
            ],
        ]);

        return $response->getBody()->getContents();
    }
}
