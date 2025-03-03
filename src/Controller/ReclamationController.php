<?php
namespace App\Controller;

use App\Entity\Reclamation;
use App\Form\ReclamationType;
use App\Form\SearchReclamationType;
use App\Repository\ReclamationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;   
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Knp\Component\Pager\PaginatorInterface;
use App\Service\BadWordFilterService;

#[Route('/reclamation')]
final class ReclamationController extends AbstractController
{
    private BadWordFilterService $badWordFilter;

    public function __construct(BadWordFilterService $badWordFilter)
    {
        $this->badWordFilter = $badWordFilter;
    }
    

    #[Route(name: 'app_reclamation_index', methods: ['GET'])]
    public function index(ReclamationRepository $reclamationRepository, Request $request, PaginatorInterface $paginator): Response
    {
        $searchTerm = $request->query->get('search', ''); 

        $reclamationsQuery = $reclamationRepository->findBySearchTerm($searchTerm);

        $form = $this->createForm(SearchReclamationType::class, null, [
            'action' => $this->generateUrl('app_reclamation_index'),
            'method' => 'GET',
        ]);

        $form->handleRequest($request);

        $reclamations = $paginator->paginate(
            $reclamationsQuery,
            $request->query->getInt('page', 1),
            10
        );

        return $this->render('reclamation/index.html.twig', [
            'form' => $form->createView(),
            'reclamations' => $reclamations,
        ]);
    }

    #[Route('/new', name: 'app_reclamation_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator): Response
    {
        $reclamation = new Reclamation();
        
        $form = $this->createForm(ReclamationType::class, $reclamation);
        $form->handleRequest($request);

        // Appliquer le filtre de mots interdits sur le champ de contenu sans toucher à l'entité
        $content = $reclamation->getDescription();
        if ($content) {
            $filteredContent = $this->badWordFilter->filterText($content);
            $reclamation->setDescription($filteredContent);
        }

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
            $reclamation->setCreatedAt(new \DateTime());
            $reclamation->setUpdatedAt(new \DateTime());
            $reclamation->setStatut('En attente');

            $entityManager->persist($reclamation);
            $entityManager->flush();

            $this->addFlash('success', 'Réclamation créée avec succès !');

            return $this->redirectToRoute('app_reclamation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reclamation/new.html.twig', [
            'reclamation' => $reclamation,
            'form' => $form->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_reclamation_show', methods: ['GET'])]
    public function show(Reclamation $reclamation): Response
    {
        return $this->render('reclamation/show.html.twig', [
            'reclamation' => $reclamation,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_reclamation_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Reclamation $reclamation, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ReclamationType::class, $reclamation);
        $form->handleRequest($request);

        // Appliquer le filtre de mots interdits sur le champ de contenu sans toucher à l'entité
        $content = $reclamation->getDescription();
        if ($content) {
            $filteredContent = $this->badWordFilter->filterText($content);
            $reclamation->setDescription($filteredContent);
        }

        if ($form->isSubmitted() && $form->isValid()) {
            $reclamation->setUpdatedAt(new \DateTime());
            $entityManager->flush();

            return $this->redirectToRoute('app_reclamation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('reclamation/edit.html.twig', [
            'reclamation' => $reclamation,
            'form' => $form->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_reclamation_delete', methods: ['POST'])]
    public function delete(Request $request, Reclamation $reclamation, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $reclamation->getId(), $request->request->get('_token'))) {
            $entityManager->remove($reclamation);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_reclamation_index', [], Response::HTTP_SEE_OTHER);
    }
}
