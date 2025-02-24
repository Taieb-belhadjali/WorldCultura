<?php

namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class AkismetService
{
    private string $apiKey;
    private string $blogUrl;
    private HttpClientInterface $httpClient;

    public function __construct(string $apiKey, string $blogUrl, HttpClientInterface $httpClient)
    {
        $this->apiKey = $apiKey;
        $this->blogUrl = $blogUrl;
        $this->httpClient = $httpClient;
    }

    public function isSpam(string $comment, string $userIp, string $userAgent, ?string $author = null, ?string $email = null, ?string $url = null): bool
    {
        $url = "https://{$this->apiKey}.rest.akismet.com/1.1/comment-check";

        $response = $this->httpClient->request('POST', $url, [
            'body' => [
                'blog' => $this->blogUrl,
                'user_ip' => $userIp,
                'user_agent' => $userAgent,
                'comment_content' => $comment,
                'comment_author' => $author,
                'comment_author_email' => $email,
                'comment_author_url' => $url,
            ],
        ]);

        return trim($response->getContent()) === "true"; // true = spam, false = not spam
    }

    private array $spamWords = ['spam', 'fake', 'scam', 'badword']; // 🔥 Liste des mots interdits

    public function checkSpam(string $comment): bool
    {
        foreach ($this->spamWords as $word) {
            if (stripos($comment, $word) !== false) {
                return true; // 🚨 C'est du spam !
            }
        }
        return false; // ✅ Pas de spam
    }


}
