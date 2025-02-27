<?php

namespace App\Controller;

use App\Entity\Product;
use App\Service\StripeService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Routing\Annotation\Route;

class CustomerController extends AbstractController
{
    private $entityManager;
    private $stripeService;
    private $requestStack;

    public function __construct(EntityManagerInterface $entityManager, StripeService $stripeService, RequestStack $requestStack)
    {
        $this->entityManager = $entityManager;
        $this->stripeService = $stripeService;
        $this->requestStack = $requestStack;
    }

    #[Route('/products', name: 'app_products')]
    public function listProducts(): Response
    {
        $products = $this->entityManager->getRepository(Product::class)->findAll();

        return $this->render('customer/products.html.twig', [
            'products' => $products,
        ]);
    }

    #[Route('/product/{id}', name: 'app_product_details')]
    public function productDetails(Product $product): Response
    {
        return $this->render('customer/product_details.html.twig', [
            'product' => $product,
        ]);
    }

    #[Route('/cart/add/{id}', name: 'app_add_to_cart')]
    public function addToCart(Product $product): Response
    {
        $cart = $this->requestStack->getSession()->get('cart', []);

        if (isset($cart[$product->getId()])) {
            $cart[$product->getId()]['quantity']++;
        } else {
            $cart[$product->getId()] = ['quantity' => 1, 'product' => $product];
        }

        $this->requestStack->getSession()->set('cart', $cart);

        $this->addFlash('success', 'Produit ajouté au panier avec succès.');
        return $this->redirectToRoute('app_products');
    }

    #[Route('/cart', name: 'app_cart')]
    public function showCart(): Response
    {
        $cart = $this->requestStack->getSession()->get('cart', []);
        $total = $this->calculateCartTotal($cart);

        return $this->render('customer/cart.html.twig', [
            'stripe_public_key' => 'pk_test_51QvzSGRuvIW9TZS5G8JqLZNI1Kt946YbEaes5mHLhi8eSu7Tr3ymGIQNy0YP4npVLFHWhe803VorTi5zdnKINuck00DlYTwlt2',
            'cart' => $cart,
            'total' => $total,
        ]);
    }

    #[Route('/cart/update/{productId}', name: 'update_cart_quantity')]
    public function updateCartQuantity(int $productId, Request $request): Response
    {
        $cart = $this->requestStack->getSession()->get('cart', []);

        if (isset($cart[$productId])) {
            $action = $request->request->get('action');

            if ($action === 'increment') {
                $cart[$productId]['quantity']++;
            } elseif ($action === 'decrement' && $cart[$productId]['quantity'] > 1) {
                $cart[$productId]['quantity']--;
            }

            $this->requestStack->getSession()->set('cart', $cart);
            $this->addFlash('success', 'Quantité mise à jour avec succès.');
        }

        return $this->redirectToRoute('app_cart');
    }

    #[Route('/cart/remove/{productId}', name: 'remove_from_cart')]
    public function removeFromCart(int $productId): Response
    {
        $cart = $this->requestStack->getSession()->get('cart', []);

        if (isset($cart[$productId])) {
            unset($cart[$productId]);
        }

        $this->requestStack->getSession()->set('cart', $cart);
        $this->addFlash('success', 'Produit supprimé du panier avec succès.');

        return $this->redirectToRoute('app_cart');
    }

    #[Route('/checkout', name: 'app_checkout')]
    public function checkout(): Response
    {
        $cart = $this->requestStack->getSession()->get('cart', []);
        $total = $this->calculateCartTotal($cart);

        try {
            $session = $this->stripeService->createCheckoutSession($cart);
            return $this->redirect($session->url);
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création de la session Stripe.');
            return $this->redirectToRoute('app_cart');
        }
    }

    #[Route('/success', name: 'app_success')]
    public function success(Request $request): Response
    {
        $sessionId = $request->get('session_id');
        if (!$sessionId) {
            throw new \Exception('Session ID is missing');
        }

        try {
            $session = $this->stripeService->retrieveCheckoutSession($sessionId);
            $order = $this->stripeService->createOrderFromSession($session);
            $this->requestStack->getSession()->remove('cart');

            return $this->render('checkout/success.html.twig', [
                'order' => $order,
            ]);
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement du paiement.');
            return $this->redirectToRoute('app_cart');
        }
    }

    #[Route('/cancel', name: 'app_cancel')]
    public function cancel(): Response
    {
        return $this->render('checkout/cancel.html.twig');
    }

    private function calculateCartTotal(array $cart): float
    {
        $total = 0;
        foreach ($cart as $item) {
            if (isset($item['product']) && $item['product'] instanceof Product) {
                $total += $item['product']->getPrice() * $item['quantity'];
            }
        }
        return $total;
    }
}
