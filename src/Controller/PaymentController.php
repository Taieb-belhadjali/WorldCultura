<?php
namespace App\Controller;

use App\Service\StripeService;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Doctrine\ORM\EntityManagerInterface;
use App\Entity\Order;
use App\Entity\OrderItem;

class PaymentController extends AbstractController
{
    private $stripeService;
    private $entityManager;

    public function __construct(StripeService $stripeService, EntityManagerInterface $entityManager)
    {
        $this->stripeService = $stripeService;
        $this->entityManager = $entityManager;
    }

    #[Route('/payment/create', name: 'payment_create', methods: ['POST'])]
    public function create(Request $request)
    {
        $data = json_decode($request->getContent(), true);
        $cartItems = $data['cart'];

        // Créer la session de paiement avec Stripe
        $session = $this->stripeService->createCheckoutSession($cartItems);

        if (!$session) {
            return $this->json(['error' => 'Erreur lors de la création de la session de paiement.'], 500);
        }

        return $this->json(['sessionId' => $session->id]);
    }

    #[Route('/payment/webhook', name: 'payment_webhook', methods: ['POST'])]
    public function webhook(Request $request)
    {
        // Secret de votre webhook Stripe récupéré depuis le fichier .env
        $endpointSecret = $_ENV['STRIPE_ENDPOINT_SECRET']; // Utilisation de la variable d'environnement

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
            $this->get('logger')->error('Erreur Webhook Stripe : ' . $e->getMessage());
            return new Response('Payload invalide', Response::HTTP_BAD_REQUEST);
        } catch (\Stripe\Exception\SignatureVerificationException $e) {
            // Signature invalide
            $this->get('logger')->error('Erreur Webhook Stripe : ' . $e->getMessage());
            return new Response('Signature invalide', Response::HTTP_BAD_REQUEST);
        }
    }

    public function createOrderFromSession($session)
    {
        // Récupérer les informations de la session Stripe
        $customerEmail = $session->customer_email;
        $amountTotal = $session->amount_total / 100; // Convertir en unité monétaire (ex: euros)

        // Créer la commande
        $order = new Order();
        $order->setCustomerEmail($customerEmail);
        $order->setTotalAmount($amountTotal);
        $order->setStatus(Order::STATUS_PENDING); // Statut de la commande initialement en attente

        // Enregistrer la commande
        $this->entityManager->persist($order);
        $this->entityManager->flush();

        // Enregistrer les éléments de la commande (OrderItems)
        foreach ($session->line_items as $item) {
            $orderItem = new OrderItem();
            $orderItem->setOrder($order);
            $orderItem->setProductName($item->description);
            $orderItem->setQuantity($item->quantity);
            $orderItem->setUnitPrice($item->amount_total / 100); // Assurer que le prix est en unités monétaires
            $orderItem->setTotalPrice($item->amount_total / 100); // Total price du produit

            $this->entityManager->persist($orderItem);
        }

        // Valider l'ensemble des enregistrements dans la base de données
        $this->entityManager->flush();

        return $order;
    }
}
