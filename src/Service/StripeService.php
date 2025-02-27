<?php

namespace App\Service;

use App\Entity\Order;
use App\Entity\OrderItem;
use App\Entity\Product;
use Stripe\Checkout\Session;
use Stripe\Stripe;
use Symfony\Component\DependencyInjection\ParameterBag\ParameterBagInterface;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\RequestStack;

class StripeService
{
    private $stripePublicKey;
    private $stripeSecretKey;
    private $requestStack;
    private $entityManager;

    public function __construct(ParameterBagInterface $params, EntityManagerInterface $entityManager, RequestStack $requestStack)
    {
        $this->stripePublicKey = $params->get('stripe_public_key');
        $this->stripeSecretKey = $params->get('stripe_secret_key');
        $this->entityManager = $entityManager;
        $this->requestStack = $requestStack;

        Stripe::setApiKey($this->stripeSecretKey);
    }

    // Crée une session de paiement Stripe
    public function createCheckoutSession(array $cart)
    {
        $lineItems = [];

        // Vérifier que le panier contient des produits valides
        foreach ($cart as $item) {
            if (empty($item['product']['name']) || empty($item['product']['price']) || empty($item['quantity'])) {
                throw new \Exception('Un élément du panier est mal formaté.');
            }

            $lineItems[] = [
                'price_data' => [
                    'currency' => 'eur',
                    'product_data' => [
                        'name' => $item['product']['name'],
                    ],
                    'unit_amount' => $item['product']['price'] * 100, // Convertir en centimes
                ],
                'quantity' => $item['quantity'],
            ];
        }

        if (empty($lineItems)) {
            throw new \Exception("Aucun élément dans le panier.");
        }

        // URL de succès et d'annulation
        $successUrl = 'http://127.0.0.1:8000/success?session_id={CHECKOUT_SESSION_ID}';
        $cancelUrl = 'http://127.0.0.1:8000/cancel';

        // Créer la session Stripe
        $session = Session::create([
            'payment_method_types' => ['card'],
            'line_items' => $lineItems,
            'mode' => 'payment',
            'success_url' => $successUrl,
            'cancel_url' => $cancelUrl,
        ]);

        // Stocker les line_items dans la session
        $sessionStorage = $this->requestStack->getSession();
        $sessionStorage->set('line_items', $lineItems);

        return $session;
    }

    // Récupérer la session Stripe à partir de l'ID de session
    public function retrieveCheckoutSession($sessionId)
    {
        try {
            $session = Session::retrieve($sessionId);
        } catch (\Stripe\Exception\ApiErrorException $e) {
            throw new \Exception('Erreur lors de la récupération de la session Stripe : ' . $e->getMessage());
        }

        return $session;
    }

    // Créer une commande à partir de la session Stripe après paiement réussi
    public function createOrderFromSession($session)
    {
        // Utiliser une valeur par défaut si l'email est manquant
        $customerEmail = $session->customer_email ?? 'inconnu';

        // Convertir le montant total en euros
        $amountTotal = $session->amount_total / 100;

        // Créer la commande
        $order = new Order();
        $order->setTotalPrice($amountTotal);
        $order->setStatus(Order::STATUS_PENDING);
        $order->setCreatedAt(new \DateTimeImmutable()); // Date de création

        $this->entityManager->persist($order);

        // Récupération des line_items depuis la session HTTP
        $sessionStorage = $this->requestStack->getSession();
        if (!$sessionStorage->has('line_items')) {
            throw new \Exception('La session Stripe ne contient pas d\'éléments.');
        }

        $lineItems = $sessionStorage->get('line_items');

        // Ajouter les éléments de commande à la base de données
        foreach ($lineItems as $item) {
            $orderItem = new OrderItem();
            $orderItem->setCustomerOrder($order);

            // Recherche du produit associé
            $productName = $item['price_data']['product_data']['name'] ?? 'Produit inconnu';
            $product = $this->entityManager->getRepository(Product::class)->findOneBy(['name' => $productName]);

            if ($product) {
                $orderItem->setProduct($product);
                $orderItem->setProductName($product->getName()); // Optionnel
            } else {
                $orderItem->setProductName($productName);
            }

            $orderItem->setQuantity($item['quantity']);
            $orderItem->setPrice($item['price_data']['unit_amount'] / 100);

            $this->entityManager->persist($orderItem);
        }

        $this->entityManager->flush();

        return $order;
    }

    // Gérer le webhook pour l'URL de succès Stripe
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
                $this->createOrderFromSession($session);
            }

            return new Response('Webhook reçu avec succès', Response::HTTP_OK);
        } catch (\UnexpectedValueException $e) {
            // Mauvais payload
            $this->get('logger')->error('Payload invalide', ['exception' => $e]);
            return new Response('Payload invalide', Response::HTTP_BAD_REQUEST);
        } catch (\Stripe\Exception\SignatureVerificationException $e) {
            // Signature invalide
            $this->get('logger')->error('Signature invalide', ['exception' => $e]);
            return new Response('Signature invalide', Response::HTTP_BAD_REQUEST);
        } catch (\Exception $e) {
            // Erreur générale
            $this->get('logger')->error('Erreur lors du traitement du webhook', ['exception' => $e]);
            return new Response('Erreur serveur', Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}
