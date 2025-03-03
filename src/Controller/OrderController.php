<?php
namespace App\Controller;

use App\Entity\Order;
use App\Form\OrderType; // If you have a form class for the order
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/order')]
class OrderController extends AbstractController
{
    private EntityManagerInterface $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    // 1. Lire toutes les commandes (List Orders)
    #[Route('/', name: 'order_index', methods: ['GET'])]
    public function index(): Response
    {
        $orders = $this->entityManager->getRepository(Order::class)->findAll();

        return $this->render('order/index.html.twig', [
            'orders' => $orders,
        ]);
    }

    // 2. Créer une commande (Add Order)
    #[Route('/create', name: 'order_create', methods: ['POST'])]
public function create(Request $request): Response
{
    // Get form data
    $status = $request->get('status');
    $totalPrice = (float)$request->get('totalPrice');
    
    // Create new Order instance
    $order = new Order();
    $order->setStatus($status);
    $order->setTotalPrice($totalPrice);
    $order->setCreatedAt(new \DateTimeImmutable());  // Auto-generated creation date

    // Persist order to the database
    $this->entityManager->persist($order);
    $this->entityManager->flush();

    // Redirect to order index (list of orders)
    return $this->redirectToRoute('order_index');
}


#[Route('/create', name: 'order_create_form', methods: ['GET'])]
public function showCreateForm(): Response
{
    return $this->render('order/new.html.twig');
}

    // 3. Voir une commande spécifique (View Order)
    #[Route('/{id}', name: 'order_show', methods: ['GET'])]
    public function show(int $id): Response
    {
        $order = $this->entityManager->getRepository(Order::class)->find($id);

        if (!$order) {
            return $this->json(['error' => 'Order not found'], 404);
        }

        return $this->render('order/show.html.twig', [
            'order' => $order,
        ]);
    }

    // 4. Modifier une commande (Edit Order)
    #[Route('/{id}/edit', name: 'order_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, int $id): Response
    {
        $order = $this->entityManager->getRepository(Order::class)->find($id);

        if (!$order) {
            return $this->json(['error' => 'Order not found'], 404);
        }

        $form = $this->createForm(OrderType::class, $order); // Create the form for editing the order

        if ($request->isMethod('POST')) {
            $form->handleRequest($request);
            if ($form->isValid()) {
                $this->entityManager->flush(); // Save changes

                return $this->redirectToRoute('order_index'); // Redirect after editing
            }
        }

        return $this->render('order/edit.html.twig', [
            'form' => $form->createView(),
            'order' => $order,
        ]);
    }

    // 5. Supprimer une commande (Delete Order)
    #[Route('/{id}/delete', name: 'order_delete', methods: ['DELETE', 'POST'])]
public function delete(int $id, Request $request): Response
{
    $order = $this->entityManager->getRepository(Order::class)->find($id);

    if (!$order) {
        throw $this->createNotFoundException('Order not found.');
    }

    $submittedToken = $request->request->get('_token');

    if ($this->isCsrfTokenValid('delete' . $order->getId(), $submittedToken)) {
        $this->entityManager->remove($order);
        $this->entityManager->flush();

        $this->addFlash('success', 'Order deleted successfully.');
    } else {
        $this->addFlash('error', 'Invalid CSRF token.');
    }

    return $this->redirectToRoute('order_index');
}

}
