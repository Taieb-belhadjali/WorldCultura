<?php

namespace App\Controller;

use App\Entity\Order;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/order')]
class OrderController extends AbstractController
{
    private EntityManagerInterface $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    // 1. Lire toutes les commandes
    #[Route('/', name: 'order_index', methods: ['GET'])]
    public function index(): Response
    {
        $orders = $this->entityManager->getRepository(Order::class)->findAll();

        return $this->json($orders);
    }

    // 2. Ajouter une commande
    #[Route('/create', name: 'order_create', methods: ['POST'])]
    public function create(Request $request): Response
    {
        $data = json_decode($request->getContent(), true);

        $order = new Order();
        $order->setCreatedAt(new \DateTimeImmutable());
        $order->setStatus($data['status'] ?? 'pending');
        $order->setTotalPrice($data['totalPrice'] ?? 0);

        $this->entityManager->persist($order);
        $this->entityManager->flush();

        return $this->json(['message' => 'Order created successfully!', 'id' => $order->getId()]);
    }

    // 3. Lire une commande spécifique
    #[Route('/{id}', name: 'order_show', methods: ['GET'])]
    public function show(int $id): Response
    {
        $order = $this->entityManager->getRepository(Order::class)->find($id);

        if (!$order) {
            return $this->json(['error' => 'Order not found'], 404);
        }

        return $this->json($order);
    }

    // 4. Mettre à jour une commande
    #[Route('/{id}/update', name: 'order_update', methods: ['PUT'])]
    public function update(Request $request, int $id): Response
    {
        $order = $this->entityManager->getRepository(Order::class)->find($id);

        if (!$order) {
            return $this->json(['error' => 'Order not found'], 404);
        }

        $data = json_decode($request->getContent(), true);

        if (isset($data['status'])) {
            $order->setStatus($data['status']);
        }
        if (isset($data['totalPrice'])) {
            $order->setTotalPrice($data['totalPrice']);
        }

        $this->entityManager->flush();

        return $this->json(['message' => 'Order updated successfully']);
    }

    // 5. Supprimer une commande
    #[Route('/{id}/delete', name: 'order_delete', methods: ['DELETE'])]
    public function delete(int $id): Response
    {
        $order = $this->entityManager->getRepository(Order::class)->find($id);

        if (!$order) {
            return $this->json(['error' => 'Order not found'], 404);
        }

        $this->entityManager->remove($order);
        $this->entityManager->flush();

        return $this->json(['message' => 'Order deleted successfully']);
    }
}
