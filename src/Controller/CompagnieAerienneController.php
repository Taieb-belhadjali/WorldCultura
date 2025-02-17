<?php

namespace App\Controller;

use App\Entity\CompagnieAerienne;
use App\Form\CompagnieAerienneType;
use App\Repository\CompagnieAerienneRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/compagnie/aerienne')]
final class CompagnieAerienneController extends AbstractController
{
    #[Route(name: 'app_compagnie_aerienne_index', methods: ['GET'])]
    public function index(CompagnieAerienneRepository $compagnieAerienneRepository): Response
    {
        return $this->render('compagnie_aerienne/index.html.twig', [
            'compagnie_aeriennes' => $compagnieAerienneRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_compagnie_aerienne_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $compagnieAerienne = new CompagnieAerienne();
        $form = $this->createForm(CompagnieAerienneType::class, $compagnieAerienne);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Gestion du fichier image
            $file = $form->get('logoFile')->getData();
            if ($file) {
                $newFilename = uniqid().'.'.$file->guessExtension();
                $file->move($this->getParameter('logos_directory'), $newFilename);
                $compagnieAerienne->setLogo($newFilename);
            }

            $entityManager->persist($compagnieAerienne);
            $entityManager->flush();

            return $this->redirectToRoute('app_compagnie_aerienne_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('compagnie_aerienne/new.html.twig', [
            'compagnie_aerienne' => $compagnieAerienne,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_compagnie_aerienne_show', methods: ['GET'])]
    public function show(CompagnieAerienne $compagnieAerienne): Response
    {
        return $this->render('compagnie_aerienne/show.html.twig', [
            'compagnie_aerienne' => $compagnieAerienne,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_compagnie_aerienne_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, CompagnieAerienne $compagnieAerienne, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CompagnieAerienneType::class, $compagnieAerienne);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Gestion du fichier image pour modification
            $file = $form->get('logoFile')->getData();
            if ($file) {
                $newFilename = uniqid().'.'.$file->guessExtension();
                $file->move($this->getParameter('logos_directory'), $newFilename);
                $compagnieAerienne->setLogo($newFilename);
            }

            $entityManager->flush();

            return $this->redirectToRoute('app_compagnie_aerienne_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('compagnie_aerienne/edit.html.twig', [
            'compagnie_aerienne' => $compagnieAerienne,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_compagnie_aerienne_delete', methods: ['POST'])]
    public function delete(Request $request, CompagnieAerienne $compagnieAerienne, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$compagnieAerienne->getId(), $request->request->get('_token'))) {
            $entityManager->remove($compagnieAerienne);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_compagnie_aerienne_index', [], Response::HTTP_SEE_OTHER);
    }
}
