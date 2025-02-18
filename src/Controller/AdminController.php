<?php

namespace App\Controller;

use App\Repository\EventRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Post;
use App\Entity\Commentaire;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Request;


class AdminController extends AbstractController
{
        #[Route('/admin', name: 'app_admin')]
    public function index(EventRepository $eventRepository): Response
    {
        return $this->render('event/admin.html.twig', [
            'events' => $eventRepository->findAll(),

        ]);
    }

}
