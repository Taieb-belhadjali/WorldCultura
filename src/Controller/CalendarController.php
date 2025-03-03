<?php

namespace App\Controller;

use App\Repository\EventRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class CalendarController extends AbstractController
{
    #[Route('/calendar', name: 'app_calendar')]
    public function index(EventRepository $eventRepository): Response
    {
        $events = $eventRepository->findAll(); // Récupérer tous les événements

        $formattedEvents = [];
        foreach ($events as $event) {
            $formattedEvents[] = [
                'title' => $event->getName(), // Nom de l'événement
                'start' => $event->getDateDebut()->format('Y-m-d H:i:s'),
                'end' => $event->getDateFin()->format('Y-m-d H:i:s'),
            ];
        }

        return $this->render('TattiCalendar/calendar.html.twig', [
            'events' => json_encode($formattedEvents),
        ]);
    }
}
