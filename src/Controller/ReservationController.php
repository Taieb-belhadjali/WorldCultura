<?php

namespace App\Controller;

use App\Entity\Reservation;
use App\Entity\Rehla;
use App\Form\ReservationType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ReservationController extends AbstractController
{
    // Création d'une nouvelle réservation
    #[Route('/reservation/new/{id}', name: 'reservation_new')]
    public function new(Rehla $rehla, Request $request, EntityManagerInterface $em): Response
    {
        $reservation = new Reservation();
        $reservation->setRehla($rehla);

        $form = $this->createForm(ReservationType::class, $reservation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($reservation);
            $em->flush();

            $this->addFlash('success', 'Réservation effectuée avec succès');
            return $this->redirectToRoute('rehla_list_front'); // Redirection vers la page /rehla/list/front
        }

        return $this->render('reservation/new.html.twig', [
            'form' => $form->createView(),
            'rehla' => $rehla
        ]);
    }

    // Affichage de la liste des réservations
    #[Route('/reservation', name: 'reservation_index')]
    public function index(EntityManagerInterface $em): Response
    {
        $reservations = $em->getRepository(Reservation::class)->findAll();

        return $this->render('reservation/index.html.twig', [
            'reservations' => $reservations,
        ]);
    }

    // Suppression d'une réservation
    #[Route('/reservation/delete/{id}', name: 'reservation_delete')]
    public function delete(Reservation $reservation, EntityManagerInterface $em): Response
    {
        $em->remove($reservation);
        $em->flush();

        $this->addFlash('success', 'Réservation supprimée avec succès');
        return $this->redirectToRoute('reservation_index');
    }
}
