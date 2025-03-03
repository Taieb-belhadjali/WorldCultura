<?php
namespace App\Service;

class BadWordFilterService
{
    private array $badWords = [
        'fuck', 'shit', 'as ba', 'merde', 'putain','niga' // Ajoute tes mots ici
    ];

    public function filterText(string $text): string
    {
        foreach ($this->badWords as $badWord) {
            $pattern = '/\b' . preg_quote($badWord, '/') . '\b/i';
            $text = preg_replace($pattern, str_repeat('*', mb_strlen($badWord)), $text);
        }

        return $text;
    }
}
