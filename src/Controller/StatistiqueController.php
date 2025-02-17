<?php
// src/Controller/StatistiqueController.php
namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route; // Utilisation de l'attribut Route
use App\Repository\ReservationRepository;

class StatistiqueController extends AbstractController
{
    #[Route('/statistiques', name: 'statistiques')] // Définition de la route avec un attribut
    public function index(ReservationRepository $reservationRepository): Response
    {
        // Récupère les données nécessaires depuis le repository
        $topCompagnies = $reservationRepository->getTopCompagniesAeriennes();
        $topDestinations = $reservationRepository->getTopDestinations();
        $chiffreAffaireParCompagnie = $reservationRepository->getChiffreAffaireParCompagnie();

        // Rend la vue Twig avec les données
        return $this->render('statistique/index.html.twig', [
            'topCompagnies' => $topCompagnies,
            'topDestinations' => $topDestinations,
            'chiffreAffaireParCompagnie' => $chiffreAffaireParCompagnie,
        ]);
    }
}