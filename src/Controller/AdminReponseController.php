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

#[Route('/reponseAdmin')]
final class AdminReponseController extends AbstractController
{
    // page indexe seullement pour l'admin
    #[Route(name: 'app_Areponse_index', methods: ['GET'])]
    public function index(ReponseRepository $reponseRepository , ReclamationRepository $reclamtionRepository ): Response
    {
        return $this->render('reponseAdmin/index.html.twig', [
            'reclamations' => $reclamtionRepository->findAll(),
            'reponses' => $reponseRepository->findAll(),
        ]);
    }

    // route pour creation de reponse par l'admin
    #[Route('/Anew/{ReclamationId}', name: 'app_Areponse_new', methods: ['GET', 'POST'])]
    public function Anew(Request $request, EntityManagerInterface $entityManager , ReclamationRepository $reclamtionRepository , int $ReclamationId , ValidatorInterface $validator): Response
    {
        $reclamation = $entityManager->getRepository(Reclamation::class)->find($ReclamationId);
        $reponse = new Reponse();
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        $errors = $validator->validate($reclamation);

        

        if ($form->isSubmitted() && $form->isValid()) {
            $reponse->setCreatedAt(new \DateTimeImmutable());
            $reponse->setActeur('Admin');
            $reclamation->setUpdatedAt(new \DateTime());
            $reclamation-> setStatut('Résolu');
            $reclamation->addReponse($reponse);
            $entityManager->persist($reclamation);
            $entityManager->flush();
            
            $reponse->setReclamation($reclamation);
            $entityManager->persist($reponse);
            $entityManager->flush();

            return $this->redirectToRoute('app_Areponse_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reponseAdmin/new.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    } 

    

    // route pour afficher une reponse seullement pour l'admin
    #[Route('/{id}', name: 'app_Areponse_show', methods: ['GET'])]
    public function show( EntityManagerInterface $entityManager , int $id ): Response
    {
        $reclamation = $entityManager->getRepository(Reclamation::class)->find($id);

        
        return $this->render('reponseAdmin/show.html.twig', [
            'reclamation' => $reclamation,  
           
        ]);
    }

    // route pour modifier une reponse pour client/ admin
    #[Route('/{id}/edit', name: 'app_Areponse_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_Areponse_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reponseAdmin/edit.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    }

    

    

    // route pour supprimer une reponse pour l'admin
    #[Route('/{id}', name: 'app_Areponse_delete', methods: ['POST'])]
    public function Adelete(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$reponse->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($reponse);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_Areponse_show', ['id' => $reponse->getReclamation()->getId()], Response::HTTP_SEE_OTHER);
    }
}
