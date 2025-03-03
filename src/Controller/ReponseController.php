<?php

namespace App\Controller;

use App\Entity\Reponse;
use App\Entity\Reclamation;
use App\Form\ReponseType;
use App\Repository\ReponseRepository;
use Doctrine\ORM\EntityManagerInterface;
use App\Repository\ReclamationRepository;
use Symfony\Component\Validator\Validator\ValidatorInterface; 
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/reponse')]
final class ReponseController extends AbstractController
{
    

    #[Route(name: 'app_reponse_index', methods: ['GET'])]
    public function index(ReponseRepository $reponseRepository, ReclamationRepository $reclamationRepository): Response
    {
        return $this->render('reponse/index.html.twig', [
            'reclamations' => $reclamationRepository->findAll(),
            'reponses' => $reponseRepository->findAll(),
        ]);
    }

    #[Route('/Anew/{ReclamationId}', name: 'app_reponse_new', methods: ['GET', 'POST'])]
    public function Anew(Request $request, EntityManagerInterface $entityManager, ReclamationRepository $reclamationRepository, int $ReclamationId): Response
    {
        $reclamation = $entityManager->getRepository(Reclamation::class)->find($ReclamationId);
        $reponse = new Reponse();
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $reponse->setCreatedAt(new \DateTimeImmutable());
            $reponse->setActeur('Admin');
            $reclamation->setUpdatedAt(new \DateTime());
            $reclamation->setStatut('Résolu');
            $reclamation->addReponse($reponse);
            $entityManager->persist($reclamation);
            $entityManager->flush();
            
            $reponse->setReclamation($reclamation);
            $entityManager->persist($reponse);
            $entityManager->flush();

            

            return $this->redirectToRoute('app_reponse_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reponse/new.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    }

    #[Route('/Cnew/{ReclamationId}', name: 'app_reponse_new_client', methods: ['GET', 'POST'])]
    public function Cnew(Request $request, EntityManagerInterface $entityManager, ReclamationRepository $reclamationRepository, int $ReclamationId, ValidatorInterface $validator): Response
    {
        $reclamation = $entityManager->getRepository(Reclamation::class)->find($ReclamationId);
        $reponse = new Reponse();
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        $errors = $validator->validate($reclamation);

        if (count($errors) > 0) {
            foreach ($errors as $error) {
                $this->addFlash('danger', $error->getMessage());
            }
            return $this->render('reclamation/new.html.twig', [
                'reclamation' => $reclamation,
                'form' => $form->createView(),
            ]);
        }

        if ($form->isValid()) {
            $reponse->setCreatedAt(new \DateTimeImmutable());
            $reponse->setActeur('Reclameur');
            $reclamation->setUpdatedAt(new \DateTime());
            $reclamation->addReponse($reponse);
            $entityManager->persist($reclamation);
            $entityManager->flush();
            
            $reponse->setReclamation($reclamation);
            $entityManager->persist($reponse);
            $entityManager->flush();

            return $this->redirectToRoute('app_reclamation_show', ['id' => $reponse->getReclamation()->getId()], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reponse/new.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_reponse_show', methods: ['GET'])]
    public function show(Reponse $reponse): Response
    {
        return $this->render('reponse/show.html.twig', [
            'reponse' => $reponse,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_reponse_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            return $this->redirectToRoute('app_reponse_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reponse/edit.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_reponse_delete', methods: ['POST'])]
    public function Adelete(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $reponse->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($reponse);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_reclamation_show', ['id' => $reponse->getReclamation()->getId()], Response::HTTP_SEE_OTHER);
    }
}
