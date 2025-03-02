<?php
namespace App\Controller;

use App\Repository\OrderRepository;
use App\Repository\ProductRepository;
use App\Repository\OrderItemRepository; // Ajout du repository OrderItem
use EasyCorp\Bundle\EasyAdminBundle\Controller\AbstractDashboardController;
use EasyCorp\Bundle\EasyAdminBundle\Config\Dashboard;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class DashboardController extends AbstractDashboardController
{
    private $orderRepository;
    private $productRepository;
    private $orderItemRepository; // Déclaration du repository OrderItem

    public function __construct(OrderRepository $orderRepository, ProductRepository $productRepository, OrderItemRepository $orderItemRepository)
    {
        $this->orderRepository = $orderRepository;
        $this->productRepository = $productRepository;
        $this->orderItemRepository = $orderItemRepository; // Initialisation du repository OrderItem
    }

    #[Route('/admin', name: 'admin_dashboard')]
    public function index(): Response
    {
        // Récupère le total des commandes
        $totalOrders = $this->orderRepository->count([]);

        // Récupère le nombre de commandes en attente
        $pendingOrders = $this->orderRepository->count([
            'status' => 'PENDING', // Modifie cela selon ta façon de gérer le statut
        ]);

        // Récupère le nombre total de produits
        $totalProducts = $this->productRepository->count([]);

        // Récupère les produits les plus vendus
        $topSellingProducts = $this->orderItemRepository->findTopSellingProducts(); // Méthode à définir

        // Passe les variables à la vue
        return $this->render('dashboard/index.html.twig', [
            'totalOrders' => $totalOrders,
            'pendingOrders' => $pendingOrders,
            'totalProducts' => $totalProducts,
            'topSellingProducts' => $topSellingProducts, // Ajoute les produits les plus vendus
        ]);
    }

    public function configureDashboard(): Dashboard
    {
        return Dashboard::new()
            ->setTitle('WorldCultura Admin');
    }
}
