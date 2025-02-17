<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class TestTempController extends AbstractController
{
    #[Route('/test/temp', name: 'app_test_temp')]
    public function base(): Response
    {
        return $this->render('base.html.twig', [
            'controller_name' => 'TestTempController',
        ]);
    }

    #[Route('/test/temp/about', name: 'app_test_temp_about')]
    public function about(): Response
    {
        return $this->render('front/about.html.twig', [
            'controller_name' => 'TestTempController',
        ]);
    }

    #[Route('/test/temp/contact', name: 'app_test_temp_contact')]
    public function contact(): Response
    {
        return $this->render('front/contact.html.twig', [
            'controller_name' => 'TestTempController',
        ]);
    }  
    
    #[Route('/test/temp/booking', name: 'app_test_temp_booking')]
    public function booking(): Response
    {
        return $this->render('front/booking.html.twig', [
            'controller_name' => 'TestTempController',
        ]);
    }  
}
