<?php

namespace App\Controller;

use App\Entity\Reservation;
use App\Entity\Rehla;
use App\Form\ReservationType;
use App\Service\PdfGenerator;
use App\Service\StripeService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Writer\PngWriter;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel\ErrorCorrectionLevelLow;

class ReservationController extends AbstractController
{
    #[Route('/reservation/new/{id}', name: 'reservation_new')]
    public function new(Rehla $rehla, Request $request, EntityManagerInterface $em): Response
    {
        $reservation = new Reservation();
        $reservation->setRehla($rehla);
        $userId = 1; // Remplacer par l'utilisateur authentifié
        $reservation->setUserId($userId);

        $form = $this->createForm(ReservationType::class, $reservation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Persister la réservation dans la base de données
            $em->persist($reservation);
            $em->flush();

            // Ajout du flash message
            $this->addFlash('success', 'Votre réservation a été effectuée avec succès !');

            // Rediriger vers la page des réservations de l'utilisateur
            return $this->redirectToRoute('reservation_user');
        }

        return $this->render('reservation/new.html.twig', [
            'form'  => $form->createView(),
            'rehla' => $rehla
        ]);
    }

    #[Route('/reservation', name: 'reservation_index')]
    public function index(EntityManagerInterface $em): Response
    {
        $reservations = $em->getRepository(Reservation::class)->findAll();
        return $this->render('reservation/index.html.twig', [
            'reservations' => $reservations,
        ]);
    }

    #[Route('/reservation/delete/{id}', name: 'reservation_delete')]
    public function delete(Reservation $reservation, EntityManagerInterface $em): Response
    {
        $em->remove($reservation);
        $em->flush();
        $this->addFlash('success', 'Réservation supprimée avec succès');
        return $this->redirectToRoute('reservation_index');
    }

    #[Route('/reservation/user', name: 'reservation_user')]
    public function userReservations(EntityManagerInterface $em): Response
    {
        $userId = 1; // Remplacer par l'utilisateur connecté
        $reservations = $em->getRepository(Reservation::class)->findBy(['userId' => $userId]);
        return $this->render('reservation/user_reservations.html.twig', [
            'reservations' => $reservations,
        ]);
    }

    #[Route('/reservation/{id}/pdf', name: 'generate_pdf')]
    public function generatePdf($id, EntityManagerInterface $em, PdfGenerator $pdfGenerator): Response
    {
        $reservation = $em->getRepository(Reservation::class)->find($id);
        if (!$reservation) {
            throw $this->createNotFoundException('Réservation non trouvée');
        }
        $html = $this->renderView('reservation/ticket_pdf.html.twig', [
            'reservation' => $reservation,
        ]);
        $pdfContent = $pdfGenerator->generatePdf($html);
        return new Response($pdfContent, 200, [
            'Content-Type' => 'application/pdf',
            'Content-Disposition' => 'inline; filename="ticket_reservation.pdf"',
        ]);
    }

    #[Route('/reservation/qrcode/{id}', name: 'reservation_qrcode')]
    public function generateQrCode(Reservation $reservation): Response
    {
        $qrCode = Builder::create()
            ->writer(new PngWriter())
            ->data('Réservation ID: ' . $reservation->getId() . ' - Utilisateur: ' . $reservation->getUserId())
            ->size(300)
            ->margin(10)
            ->encoding(new Encoding('UTF-8'))
            ->errorCorrectionLevel(new ErrorCorrectionLevelLow())
            ->build();
    
        return new Response($qrCode->getString(), 200, [
            'Content-Type' => $qrCode->getMimeType(),
        ]);
    }

    #[Route('/reservation/payment/{id}', name: 'payment_checkout')]
    public function checkout(Reservation $reservation, StripeService $stripeService): Response
    {
        $lineItems = [
            [
                'price_data' => [
                    'currency' => 'eur',
                    'product_data' => [
                        'name' => 'Réservation : ' . $reservation->getRehla()->getDepart() . ' à ' . $reservation->getRehla()->getDestination(),
                    ],
                    'unit_amount' => $reservation->getRehla()->getPrice() * 100,  // Prix en centimes
                ],
                'quantity' => 1,
            ],
        ];

        $session = $stripeService->createCheckoutSession($lineItems);

        return $this->redirect($session->url);
    }
}
