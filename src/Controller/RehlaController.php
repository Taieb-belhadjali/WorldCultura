<?php

namespace App\Controller;

use App\Entity\Rehla;
use App\Form\RehlaType;
use App\Repository\RehlaRepository;
use App\Repository\CompagnieAerienneRepository;
use App\Service\WeatherService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/rehla')]
final class RehlaController extends AbstractController
{
    private WeatherService $weatherService;

    public function __construct(WeatherService $weatherService)
    {
        $this->weatherService = $weatherService;
    }

    // Affiche toutes les rehlas (page d'index)
    #[Route(name: 'app_rehla_index', methods: ['GET'])]
    public function index(RehlaRepository $rehlaRepository): Response
    {
        $rehlas = $rehlaRepository->findBy([], ['id' => 'ASC']);
        return $this->render('rehla/index.html.twig', [
            'rehlas' => $rehlas,
        ]);
    }

    // Ajoute une nouvelle rehla
    #[Route('/new', name: 'app_rehla_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $rehla = new Rehla();
        $form = $this->createForm(RehlaType::class, $rehla);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($rehla);
            $entityManager->flush();

            return $this->redirectToRoute('app_rehla_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('rehla/new.html.twig', [
            'rehla' => $rehla,
            'form' => $form->createView(),
        ]);
    }

    // Affiche une rehla spécifique
    #[Route('/{id}', name: 'app_rehla_show', methods: ['GET'])]
    public function show(Rehla $rehla): Response
    {
        return $this->render('rehla/show.html.twig', [
            'rehla' => $rehla,
        ]);
    }

    // Édite une rehla existante
    #[Route('/{id}/edit', name: 'app_rehla_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Rehla $rehla, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(RehlaType::class, $rehla);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            return $this->redirectToRoute('app_rehla_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('rehla/edit.html.twig', [
            'rehla' => $rehla,
            'form' => $form->createView(),
        ]);
    }

    // Supprime une rehla
    #[Route('/{id}', name: 'app_rehla_delete', methods: ['POST'])]
    public function delete(Request $request, Rehla $rehla, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $rehla->getId(), $request->request->get('_token'))) {
            $entityManager->remove($rehla);
            $entityManager->flush();
            $this->addFlash('success', 'Rehla supprimée avec succès.');
        } else {
            $this->addFlash('error', 'Token CSRF invalide.');
        }

        return $this->redirectToRoute('app_rehla_index', [], Response::HTTP_SEE_OTHER);
    }

    // Affiche la liste front avec la météo pour chaque rehla
    #[Route('/list/front', name: 'rehla_list_front', methods: ['GET'])]
    public function listFront(
        RehlaRepository $rehlaRepository,
        CompagnieAerienneRepository $agenceRepository
    ): Response {
        $rehlas = $rehlaRepository->findBy([], ['id' => 'ASC']);
        $agences = $agenceRepository->findAll();

        // Ajout de la météo à chaque rehla
        foreach ($rehlas as $rehla) {
            $weather = $this->weatherService->getWeather($rehla->getDestination());
            $rehla->weather = $weather;
        }

        return $this->render('rehla/list_front.html.twig', [
            'rehlas' => $rehlas,
            'agences' => $agences,
        ]);
    }
}
