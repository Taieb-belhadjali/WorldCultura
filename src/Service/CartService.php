<?php
namespace App\Service;

use App\Entity\Product;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class CartService
{
    private $session;

    
    public function __construct(RequestStack $requestStack)
    {
        $this->session = $requestStack->getSession();
        if (!$this->session->isStarted()) {
            $this->session->start();
        }
    }

    public function getCart(): array
    {
        return $this->session->get('cart', []);
    }

    public function addToCart(Product $product, int $quantity): void
    {
        $cart = $this->getCart();
        $productId = $product->getId();

        if (isset($cart[$productId])) {
            $cart[$productId]['quantity'] += $quantity;
        } else {
            $cart[$productId] = [
                'product' => $product,
                'quantity' => $quantity,
                'total' => $product->getPrice() * $quantity
            ];
        }

        $this->session->set('cart', $cart);
    }

    public function getTotal(): float
    {
        $total = 0;
        foreach ($this->getCart() as $item) {
            $total += $item['total'];
        }
        return $total;
    }

    public function clearCart(): void
    {
        $this->session->remove('cart');
    }
}
