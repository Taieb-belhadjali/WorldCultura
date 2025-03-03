<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;

class PaymentController extends AbstractController
{
    // Route pour la page de succès du paiement
    #[Route('/payment/success', name: 'payment_success')]
    public function success(): Response
    {
        return $this->render('payment/success.html.twig', [
            'message' => 'Le paiement a été effectué avec succès !',
        ]);
    }

    // Route pour la page d'annulation du paiement
    #[Route('/payment/cancel', name: 'payment_cancel')]
    public function cancel(): Response
    {
        return $this->render('payment/cancel.html.twig', [
            'message' => 'Le paiement a été annulé.',
        ]);
    }
}
