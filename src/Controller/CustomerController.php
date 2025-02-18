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
        // Récupérer des produits similaires (tous les produits sauf celui-ci)
        $products = $this->entityManager->getRepository(Product::class)->findBy([], ['name' => 'ASC']);
        
        // Filtrer les produits similaires pour ne pas inclure le produit actuel
        $similarProducts = array_filter($products, fn($similarProduct) => $similarProduct !== $product);

        return $this->render('customer/product_details.html.twig', [
            'product' => $product,
            'products' => $similarProducts, // Passer les produits similaires
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
                'product' => $product,
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

        foreach ($cart as $item) {
            $total += $item['product']->getPrice() * $item['quantity'];
        }

        return $this->render('customer/cart.html.twig', [
            'cart' => $cart,
            'total' => $total,
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
    public function updateQuantity(Request $request, $productId, SessionInterface $session): Response
    {
        $quantity = $request->request->get('quantity');
        
        // Ensure the quantity is a valid number and greater than 0
        if ($quantity < 1) {
            $quantity = 1;
        }
        
        // Retrieve the current cart from the session
        $cart = $session->get('cart', []);
        
        // Update the quantity of the specified product in the cart
        if (isset($cart[$productId])) {
            $cart[$productId]['quantity'] = $quantity;
        }
        
        // Save the updated cart back to the session
        $session->set('cart', $cart);
        
        // Redirect back to the cart page
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

        // Créer une nouvelle commande
        $order = new Order();
        $order->setStatus('PENDING');
        $order->setCreatedAt(new \DateTimeImmutable());

        // Calculer le total de la commande
        $totalPrice = 0;
        foreach ($cart as $item) {
            $totalPrice += $item['product']->getPrice() * $item['quantity'];
        }
        $order->setTotalPrice($totalPrice);

        $this->entityManager->persist($order);

        foreach ($cart as $item) {
            $orderItem = new OrderItem();
            $orderItem->setCustomerOrder($order);
            $orderItem->setProduct($item['product']);
            $orderItem->setQuantity($item['quantity']);
            $orderItem->setPrice($item['product']->getPrice());

            $this->entityManager->persist($orderItem);
        }

        $this->entityManager->flush();

        // Vider le panier
        $session->remove('cart');

        $this->addFlash('success', 'Votre commande a été passée avec succès !');

        return $this->redirectToRoute('customer_products');
    }
}
