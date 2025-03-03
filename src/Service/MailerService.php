<?php

namespace App\Service;

use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;

class MailerService
{
    private MailerInterface $mailer;
    private string $replyTo;

    public function __construct(MailerInterface $mailer, string $replyTo = 'kingamine242@gmail.com')

    {
        $this->mailer = $mailer;
        $this->replyTo = $replyTo;
    }

    public function setReplyTo(string $replyTo): void
    {
        $this->replyTo = $replyTo;
    }

    public function sendEmail(string $to,string $subject,string $content): void 
    {
        $email = (new Email())
         ->from('kingamine242@gmail.com')
        ->to($to)
         ->send($this->replyTo)
        ->subject($subject)
        ->html($content);
        $this->mailer->send($email);
    }
    public function testEmail()
    {
        $this->sendEmail(
        'kingamine242@gmail.com', // Remplace par ton adresse de test@jhrez
        'Test Symfony Mailer',
        '<p>Ceci est un test d\'envoi d\'email avec Symfony Mailer et Gmail.</p>'
        );
    }
}