<?php

namespace App\Controller;

use App\Entity\Order;
use App\Entity\OrderItem;
use App\Entity\Product;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\Annotation\Route;

class CustomerController extends AbstractController
{
    private EntityManagerInterface $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    // 1. Voir tous les produits
    #[Route('/products', name: 'customer_products', methods: ['GET'])]
    public function listProducts(): Response
    {
        $products = $this->entityManager->getRepository(Product::class)->findAll();

        return $this->render('customer/products.html.twig', [
            'products' => $products,
        ]);
    }

    // Voir les pages produits
    #[Route('/customer/product/{id}', name: 'customer_product_details')]
    public function productDetails(Product $product): Response
    {
        $similarProducts = $this->entityManager->getRepository(Product::class)
            ->createQueryBuilder('p')
            ->where('p.id != :productId')
            ->setParameter('productId', $product->getId())
            ->setMaxResults(5) // Limite des produits similaires
            ->getQuery()
            ->getResult();

        return $this->render('customer/product_details.html.twig', [
            'product' => $product,
            'products' => $similarProducts,
        ]);
    }

    // 2. Ajouter un produit au panier
    #[Route('/add-to-cart/{productId}', name: 'add_to_cart', methods: ['POST'])]
    public function addToCart(int $productId, SessionInterface $session): Response
    {
        $product = $this->entityManager->getRepository(Product::class)->find($productId);

        if (!$product) {
            $this->addFlash('error', 'Produit introuvable.');
            return $this->redirectToRoute('customer_products');
        }

        $cart = $session->get('cart', []);

        if (isset($cart[$productId])) {
            $cart[$productId]['quantity']++;
        } else {
            $cart[$productId] = [
                'productId' => $productId,
                'quantity' => 1,
            ];
        }

        $session->set('cart', $cart);
        $this->addFlash('success', 'Produit ajouté au panier !');

        return $this->redirectToRoute('customer_products');
    }

    // 3. Voir le panier
    #[Route('/cart', name: 'cart', methods: ['GET'])]
    public function cart(SessionInterface $session): Response
    {
        $cart = $session->get('cart', []);
        $total = 0;
        $products = [];

        foreach ($cart as $productId => $item) {
            $product = $this->entityManager->getRepository(Product::class)->find($productId);

            if ($product) {
                $products[] = [
                    'product' => $product,
                    'quantity' => $item['quantity'],
                ];
                $total += $product->getPrice() * $item['quantity'];
            } else {
                // Supprimer les produits inexistants du panier
                unset($cart[$productId]);
            }
        }

        // Mettre à jour le panier dans la session
        $session->set('cart', $cart);

        // Récupérer la clé publique Stripe
        $stripePublicKey = 'pk_test_51QvzSGRuvIW9TZS5G8JqLZNI1Kt946YbEaes5mHLhi8eSu7Tr3ymGIQNy0YP4npVLFHWhe803VorTi5zdnKINuck00DlYTwlt2';

        return $this->render('customer/cart.html.twig', [
            'cart' => $products,
            'total' => $total,
            'stripe_public_key' => $stripePublicKey,
        ]);
    }

    // 4. Retirer un produit du panier
    #[Route('/remove-from-cart/{productId}', name: 'remove_from_cart', methods: ['POST'])]
    public function removeFromCart(int $productId, SessionInterface $session): Response
    {
        $cart = $session->get('cart', []);

        if (isset($cart[$productId])) {
            unset($cart[$productId]);
            $session->set('cart', $cart);
            $this->addFlash('success', 'Produit retiré du panier.');
        } else {
            $this->addFlash('error', 'Produit non trouvé dans le panier.');
        }

        return $this->redirectToRoute('cart');
    }

    // 5. Mettre à jour la quantité d'un produit dans le panier
    #[Route('/update-cart-quantity/{productId}', name: 'update_cart_quantity', methods: ['POST'])]
    public function updateCartQuantity(Request $request, int $productId, SessionInterface $session): Response
    {
        $cart = $session->get('cart', []);
        $action = $request->request->get('action');

        if (isset($cart[$productId])) {
            if ($action === 'increment') {
                $cart[$productId]['quantity']++;
            } elseif ($action === 'decrement' && $cart[$productId]['quantity'] > 1) {
                $cart[$productId]['quantity']--;
            }
        }

        $session->set('cart', $cart);

        return $this->redirectToRoute('cart');
    }

    // 6. Valider le panier et créer une commande
    #[Route('/checkout', name: 'checkout', methods: ['POST'])]
    public function checkout(SessionInterface $session): Response
    {
        $cart = $session->get('cart', []);

        if (empty($cart)) {
            $this->addFlash('error', 'Votre panier est vide.');
            return $this->redirectToRoute('cart');
        }

        $order = new Order();
        $order->setStatus('PENDING');
        $order->setCreatedAt(new \DateTimeImmutable());
        $this->entityManager->persist($order);

        $totalPrice = 0;
        foreach ($cart as $productId => $item) {
            $product = $this->entityManager->getRepository(Product::class)->find($productId);

            if (!$product) {
                $this->addFlash('error', "Le produit avec l'ID $productId est introuvable.");
                return $this->redirectToRoute('cart');
            }

            // Vérification du stock
            if ($product->getStock() < $item['quantity']) {
                $this->addFlash('error', "Le produit {$product->getName()} n'a pas assez de stock.");
                return $this->redirectToRoute('cart');
            }

            $product->setStock($product->getStock() - $item['quantity']);

            $orderItem = new OrderItem();
            $orderItem->setCustomerOrder($order);
            $orderItem->setProduct($product);
            $orderItem->setQuantity($item['quantity']);
            $orderItem->setPrice($product->getPrice());

            $this->entityManager->persist($orderItem);

            $totalPrice += $product->getPrice() * $item['quantity'];
        }

        $order->setTotalPrice($totalPrice);
        $this->entityManager->flush();

        // Vider le panier
        $session->remove('cart');

        $this->addFlash('success', 'Votre commande a été passée avec succès !');

        // Rediriger vers la page des produits après paiement ou validation
        return $this->redirectToRoute('customer_products');
    }
}
