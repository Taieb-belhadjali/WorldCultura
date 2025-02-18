<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class OrderItemController extends AbstractController
{
    #[Route('/order/item', name: 'app_order_item')]
    public function index(): Response
    {
        return $this->render('order_item/index.html.twig', [
            'controller_name' => 'OrderItemController',
        ]);
    }
}
