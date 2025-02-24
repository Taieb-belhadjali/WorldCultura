<?php
namespace App\Controller;

use App\Service\StripeService;
use App\Entity\Order;
use App\Entity\OrderItem;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

class PaymentController extends AbstractController
{
    private EntityManagerInterface $entityManager;
    private StripeService $stripeService;

    public function __construct(EntityManagerInterface $entityManager, StripeService $stripeService)
    {
        $this->entityManager = $entityManager;
        $this->stripeService = $stripeService;
    }

    #[Route('/payment/create', name: 'payment_create', methods: ['POST'])]
    public function createPaymentSession(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true); // Décodage du contenu JSON de la requête

        // Vérifie que le panier est bien envoyé
        if (!isset($data['cart']) || empty($data['cart'])) {
            return new JsonResponse(['error' => 'Le panier est vide ou mal formaté.'], Response::HTTP_BAD_REQUEST);
        }

        $cart = $data['cart']; // Récupère les informations du panier

        // Crée un tableau des items pour Stripe
        $lineItems = [];
        foreach ($cart as $item) {
            // Vérifie que chaque élément contient les informations nécessaires
            if (!isset($item['product']['name']) || !isset($item['product']['price']) || !isset($item['quantity'])) {
                return new JsonResponse(['error' => 'Produit mal formaté dans le panier.'], Response::HTTP_BAD_REQUEST);
            }

            $lineItems[] = [
                'price_data' => [
                    'currency' => 'eur', // Utilise l'Euro comme devise
                    'product_data' => [
                        'name' => $item['product']['name'],
                    ],
                    'unit_amount' => $item['product']['price'] * 100, // Stripe attend le montant en centimes
                ],
                'quantity' => $item['quantity'],
            ];
        }

        // Crée la session de paiement Stripe
        try {
            $session = $this->stripeService->createCheckoutSession($lineItems); // Appel à ton service Stripe
            return new JsonResponse(['sessionId' => $session->id]); // Retourne l'ID de la session Stripe
        } catch (\Exception $e) {
            // Capture l'exception et retourne une erreur
            return new JsonResponse(['error' => 'Erreur lors de la création de la session de paiement.'], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    #[Route('/checkout/success', name: 'checkout_success')]
    public function checkoutSuccess(SessionInterface $session): Response
    {
        $cart = $session->get('cart', []); // Récupère le panier de la session
        if (empty($cart)) {
            $this->addFlash('error', 'Votre panier est vide ou le paiement a déjà été traité.');
            return $this->redirectToRoute('customer_products'); // Redirige si le panier est vide
        }

        // Création de la commande
        $order = new Order();
        $order->setStatus('PAID');
        $order->setCreatedAt(new \DateTimeImmutable());

        $totalPrice = 0;
        foreach ($cart as $item) {
            $product = $item['product'] ?? null;
            if (!$product || !isset($product['id'])) {
                $this->addFlash('error', 'Produit invalide détecté dans le panier.');
                return $this->redirectToRoute('cart'); // Redirige si un produit est invalide
            }

            // Vérifie si le produit existe dans la base de données
            $productEntity = $this->entityManager->getRepository(Product::class)->find($product['id']);
            if (!$productEntity) {
                $this->addFlash('error', 'Produit introuvable dans la base de données.');
                return $this->redirectToRoute('cart');
            }

            $orderItem = new OrderItem();
            $orderItem->setCustomerOrder($order);
            $orderItem->setProduct($productEntity);
            $orderItem->setQuantity($item['quantity']);
            $orderItem->setPrice($productEntity->getPrice());

            $this->entityManager->persist($orderItem);
            $totalPrice += $productEntity->getPrice() * $item['quantity']; // Calcule le total de la commande
        }

        // Ajout du prix total à la commande
        $order->setTotalPrice($totalPrice);
        $this->entityManager->persist($order);
        $this->entityManager->flush(); // Sauvegarde la commande et ses éléments

        // Suppression du panier de la session
        $session->remove('cart');

        return $this->render('checkout/success.html.twig'); // Affiche la page de succès
    }

    #[Route('/checkout/cancel', name: 'checkout_cancel')]
    public function checkoutCancel(): Response
    {
        return $this->render('checkout/cancel.html.twig'); // Affiche la page d'annulation
    }
}
