<?php
namespace App\Service;

use Stripe\Checkout\Session;
use Stripe\StripeClient;

class StripeService
{
    private $stripe;

    public function __construct(string $stripeSecretKey)
    {
        // Configure la clé secrète Stripe
        $this->stripe = new StripeClient($stripeSecretKey);
    }

    public function createCheckoutSession(array $lineItems)
    {
        try {
            // Crée la session de paiement avec les éléments
            $session = $this->stripe->checkout->sessions->create([
                'payment_method_types' => ['card'],
                'line_items' => $lineItems,
                'mode' => 'payment',
                'success_url' => 'http://localhost:8000/checkout/success',
                'cancel_url' => 'http://localhost:8000/checkout/cancel',
            ]);

            return $session;
        } catch (\Exception $e) {
            // Capture l'exception et la retourne
            throw new \Exception('Erreur Stripe: ' . $e->getMessage());
        }
    }
}
