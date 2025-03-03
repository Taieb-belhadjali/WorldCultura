<?php

namespace App\Controller;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Service\MailerService;

class TestController extends AbstractController
{
    #[Route('/test/email', name: 'test_email', methods: ['GET', 'POST'])]
    public function testEmail(Request $request, MailerService $mailerService): Response
    {
        if ($request->isMethod('POST')) {
            $email = $request->request->get('email');

            if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
                $mailerService->sendEmail($email, 'Test Email', '<p>Ceci est un test.</p>');

                return $this->render('test.html.twig', [
                    'success' => 'Email envoyé avec succès à ' . $email,
                ]);
            }

            return $this->render('test.html.twig', [
                'error' => 'Adresse email invalide.',
            ]);
        }

        return $this->render('test.html.twig');
    }
}