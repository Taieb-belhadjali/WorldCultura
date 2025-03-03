<?php
// src/Controller/StatistiqueController.php
namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Repository\ReservationRepository;

class StatistiqueController extends AbstractController
{
    #[Route('/statistiques', name: 'statistiques')]
    public function index(ReservationRepository $reservationRepository): Response
    {
        // Récupération des données nécessaires
        $topCompagnies = $reservationRepository->getTopCompagniesAeriennes(); // Format: [ { 'compagnie': 'Nom', 'totalReservations': 123 } ]
        $topDestinations = $reservationRepository->getTopDestinations(); // Format: [ { 'destination': 'Lieu', 'totalReservations': 123 } ]
        $chiffreAffaireParCompagnie = $reservationRepository->getChiffreAffaireParCompagnie(); // Format: [ { 'compagnie': 'Nom', 'chiffreAffaire': 1234.56 } ]

        // Récupérer les totaux
        $totalReservations = $reservationRepository->getTotalReservations();
        $totalChiffreAffaire = $reservationRepository->getTotalChiffreAffaire();

        // Passer les données à la vue
        return $this->render('statistique/index.html.twig', [
            'topCompagnies' => $topCompagnies,
            'topDestinations' => $topDestinations,
            'chiffreAffaireParCompagnie' => $chiffreAffaireParCompagnie,
            'totalReservations' => $totalReservations,
            'totalChiffreAffaire' => $totalChiffreAffaire,
        ]);
    }
}
