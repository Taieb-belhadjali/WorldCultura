<?php

namespace App\Controller;

use App\Entity\OrderItem;
use App\Entity\Order;
use App\Entity\Product;
use App\Form\OrderItemType;
use App\Repository\OrderItemRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/order-item')]
class OrderItemController extends AbstractController
{
    #[Route('/new', name: 'order_item_form_new', methods: ['GET', 'POST'])]
    public function newForm(Request $request, EntityManagerInterface $entityManager): Response
    {
        $orderItem = new OrderItem();
        $form = $this->createForm(OrderItemType::class, $orderItem);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($orderItem);
            $entityManager->flush();

            $this->addFlash('success', 'Order item created successfully!');
            return $this->redirectToRoute('order_item_index'); // Change this route to your list route if needed.
        }

        return $this->render('order_item/new.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/new', name: 'order_item_new', methods: ['POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $data = json_decode($request->getContent(), true);

        $order = $entityManager->getRepository(Order::class)->find($data['orderId']);
        $product = $entityManager->getRepository(Product::class)->find($data['productId']);

        if (!$order || !$product) {
            return $this->json(['error' => 'Invalid Order or Product ID'], Response::HTTP_BAD_REQUEST);
        }

        $orderItem = new OrderItem();
        $orderItem->setCustomerOrder($order);
        $orderItem->setProduct($product);
        $orderItem->setQuantity($data['quantity']);
        $orderItem->setPrice($product->getPrice() * $data['quantity']);

        $entityManager->persist($orderItem);
        $entityManager->flush();

        return $this->json(['message' => 'Order item created successfully'], Response::HTTP_CREATED);
    }

    #[Route('/{id}', name: 'order_item_show', methods: ['GET'])]
    public function show(OrderItem $orderItem): Response
    {
        return $this->json($orderItem);
    }

    #[Route('/{id}/edit', name: 'order_item_form_edit', methods: ['GET', 'POST'])]
    public function editForm(Request $request, OrderItem $orderItem, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(OrderItemType::class, $orderItem);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            $this->addFlash('success', 'Order item updated successfully!');
            return $this->redirectToRoute('order_item_index'); // Change this route to your list route if needed.
        }

        return $this->render('order_item/edit.html.twig', [
            'form' => $form->createView(),
            'orderItem' => $orderItem,
        ]);
    }

    #[Route('/{id}', name: 'order_item_delete', methods: ['POST'])]
public function delete(Request $request, OrderItem $orderItem, EntityManagerInterface $entityManager): Response
{
    if ($this->isCsrfTokenValid('delete' . $orderItem->getId(), $request->request->get('_token'))) {
        $entityManager->remove($orderItem);
        $entityManager->flush();

        $this->addFlash('success', 'Order item deleted successfully!');
    }

    return $this->redirectToRoute('order_item_index');
}
    #[Route('/', name: 'order_item_index', methods: ['GET'])]
public function index(OrderItemRepository $orderItemRepository): Response
{
    return $this->render('order_item/index.html.twig', [
        'orderItems' => $orderItemRepository->findAll(),
    ]);
}
}
