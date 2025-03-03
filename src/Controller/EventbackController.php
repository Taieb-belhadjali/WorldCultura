<?php

namespace App\Controller;

use App\Entity\Event;
use App\Form\Event1Type;
use App\Repository\EventRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Filesystem\Filesystem;
use Dompdf\Dompdf;
use Dompdf\Options;

#[Route('/eventback')]
final class EventbackController extends AbstractController
{ #[Route('/event/pdf/{id}', name: 'event_pdf')]
    public function generatePdf(int $id, EntityManagerInterface $entityManager): Response
    {
        // Récupérer l'événement depuis la base de données
        $event = $entityManager->getRepository(Event::class)->find($id);
        if (!$event) {
            throw $this->createNotFoundException('Événement non trouvé');
        }

        // Options de configuration pour Dompdf
        $pdfOptions = new Options();
        $pdfOptions->set('defaultFont', 'Arial');
        $pdfOptions->set('isRemoteEnabled', true); // Permet de charger des images via un chemin "file://"

        // Initialiser Dompdf avec les options
        $dompdf = new Dompdf($pdfOptions);

        // Construire le chemin absolu de l'image sur le serveur
        $imagePath = $this->getParameter('event_images_directory') . '/' . $event->getImage();

        if (!file_exists($imagePath)) {
            $absoluteImagePath = null;
        } else {
            // Pour Windows, on utilise realpath et on remplace les antislashs
            $absoluteImagePath = 'file:///' . str_replace('\\', '/', realpath($imagePath));
        }

        // Générer le HTML depuis le template Twig
        $html = $this->renderView('eventPDF.html.twig', [
            'event'                         => $event,
            'event_image_absolute_path'     => $absoluteImagePath,
        ]);

        // Charger le HTML dans Dompdf et générer le PDF
        $dompdf->loadHtml($html);
        $dompdf->setPaper('A4', 'portrait');
        $dompdf->render();
        $pdfContent = $dompdf->output();

        // Retourner le PDF en réponse HTTP
        return new Response(
            $pdfContent,
            200,
            [
                'Content-Type'        => 'application/pdf',
                'Content-Disposition' => 'inline; filename="event_' . $event->getId() . '.pdf"',
            ]
        );
    }
    
    
    #[Route(name: 'app_eventback_index', methods: ['GET'])]
    public function index(EventRepository $eventRepository): Response
    {
        return $this->render('eventback/index.html.twig', [
            'events' => $eventRepository->findAll(),
        ]);
    }
    

    #[Route('/new', name: 'app_eventback_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $event = new Event();
        $form = $this->createForm(Event1Type::class, $event);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form->get('file')->getData(); // Assure-toi que le nom du champ correspond à ton formulaire
    
            if ($file) {
                $newFilename = uniqid().'.'.$file->guessExtension();
                $file->move(
                    $this->getParameter('event_images_directory'), // Vérifie que ce paramètre est bien configuré
                    $newFilename
                );
                $event->setImage($newFilename);
            }
            $entityManager->persist($event);
            $entityManager->flush();

            return $this->redirectToRoute('app_eventback_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('eventback/new.html.twig', [
            'event' => $event,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_eventback_show', methods: ['GET'])]
    public function show(Event $event): Response
    {
        return $this->render('eventback/show.html.twig', [
            'event' => $event,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_eventback_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Event $event, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(Event1Type::class, $event);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form->get('file')->getData();

            if ($file) {
                // Supprimer l'ancienne image si elle existe
                if ($event->getImage()) {
                    $oldFilePath = $this->getParameter('event_images_directory') . '/' . $event->getImage();
                    if (file_exists($oldFilePath)) {
                        unlink($oldFilePath);
                    }
                }
    
                // Sauvegarder la nouvelle image
                $newFilename = uniqid().'.'.$file->guessExtension();
                $file->move(
                    $this->getParameter('event_images_directory'),
                    $newFilename
                );
                $event->setImage($newFilename);
            }
            $entityManager->flush();

            return $this->redirectToRoute('app_eventback_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('eventback/edit.html.twig', [
            'event' => $event,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_eventback_delete', methods: ['POST'])]
    public function delete(Request $request, Event $event, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$event->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($event);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_eventback_index', [], Response::HTTP_SEE_OTHER);
    }
}
