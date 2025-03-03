<?php

namespace App\Controller;

use App\Service\StripeService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StripeWebhookController extends AbstractController
{
    private $stripeService;
    private $entityManager;

    public function __construct(StripeService $stripeService, EntityManagerInterface $entityManager)
    {
        $this->stripeService = $stripeService;
        $this->entityManager = $entityManager;
    }

    #[Route('/payment/webhook', name: 'stripe_webhook', methods: ['POST'])]
    public function handleWebhook(Request $request): Response
    {
        // Secret de votre webhook Stripe
        $endpointSecret = 'whsec_0e9826e2bef696fee2cd945153824c203d7106da8efdfb8ea2024fb4c6f81877'; // Remplacez par votre secret réel

        // Récupérer le contenu brut du webhook
        $payload = $request->getContent();
        $sigHeader = $request->headers->get('Stripe-Signature');

        try {
            // Vérification de l'authenticité du webhook
            $event = \Stripe\Webhook::constructEvent(
                $payload,
                $sigHeader,
                $endpointSecret
            );

            // Gestion des événements Stripe
            if ($event->type === 'checkout.session.completed') {
                $session = $event->data->object;

                // Appeler le service pour créer la commande
                $this->stripeService->createOrderFromSession($session);
            }

            return new Response('Webhook reçu avec succès', Response::HTTP_OK);
        } catch (\UnexpectedValueException $e) {
            // Mauvais payload
            return new Response('Payload invalide', Response::HTTP_BAD_REQUEST);
        } catch (\Stripe\Exception\SignatureVerificationException $e) {
            // Signature invalide
            return new Response('Signature invalide', Response::HTTP_BAD_REQUEST);
        }
    }
}
