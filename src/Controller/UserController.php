<?php

namespace App\Controller;

use App\Entity\User;
use App\Form\UserType;
use App\Repository\UserRepository;
use App\Service\MailerService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Dompdf\Dompdf;
use Dompdf\Options;

#[Route('/user')]
final class UserController extends AbstractController
{
    private $passwordHasher;
    private $entityManager;
    private $mailer;

    public function __construct(UserPasswordHasherInterface $passwordHasher, EntityManagerInterface $entityManager, MailerService $mailer)
    {
        $this->passwordHasher = $passwordHasher;
        $this->entityManager = $entityManager;
        $this->mailer = $mailer;
    }
    
    #[Route(name: 'app_user_index', methods: ['GET'])]
    public function index(Request $request, UserRepository $userRepository): Response
    {
       // Get search term and sort order from URL parameters
    $searchTerm = $request->query->get('searchTerm', null);
    $role = $request->query->get('role', null);  // Get the selected role from the query parameter
    $sortOrder = $request->query->get('sortOrder', 'ASC'); // Default to ASC if no sortOrder is specified

    // Fetch users from the repository using the search term and sort order
    $users = $userRepository->findBySearchTermAndSort($searchTerm, $role, $sortOrder);

    $counts = $userRepository->getUserStatistics();  // This should return the counts (total, adminCount, userCount)

    return $this->render('user/index.html.twig', [
        'users' => $users,
        'counts' => $counts,  // Pass counts to the template
        'searchTerm' => $searchTerm,
        'selectedRole' => $role,
        'sortOrder' => $sortOrder,
    ]);
    }

    #[Route('/new', name: 'app_user_new', methods: ['GET', 'POST'])]
    public function new(Request $request): Response
    {
        $user = new User();
        $form = $this->createForm(UserType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = $user->getPassword();
            $encoded = $this->passwordHasher->hashPassword($user, $plainPassword);
            $user->setPassword($encoded);
            $this->entityManager->persist($user);
            $this->entityManager->flush();

            // Envoi d'un email de confirmation
            $this->mailer->sendEmail(user->getEmail, "Bienvenue", "Votre compte a été créé avec succès.");

            return $this->redirectToRoute('app_user_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('user/new.html.twig', [
            'user' => $user,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_user_show', methods: ['GET'])]
    public function show(User $user): Response
    {
        return $this->render('user/show.html.twig', [
            'user' => $user,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_user_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, User $user): Response
    {
        $form = $this->createForm(UserType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = $user->getPassword();
            $encoded = $this->passwordHasher->hashPassword($user, $plainPassword);
            $user->setPassword($encoded);
            $this->entityManager->flush();

            return $this->redirectToRoute('app_user_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('user/edit.html.twig', [
            'user' => $user,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_user_delete', methods: ['POST'])]
public function delete(Request $request, User $user, EntityManagerInterface $entityManager): Response
{
    if ($this->isCsrfTokenValid('delete' . $user->getId(), $request->request->get('_token'))) {
        // Remove related records in reset_password_request
        $conn = $entityManager->getConnection();
        $conn->executeStatement('DELETE FROM reset_password_request WHERE user_id = :userId', ['userId' => $user->getId()]);

        // Now delete the user
        $entityManager->remove($user);
        $entityManager->flush();
    }

    return $this->redirectToRoute('app_user_index', [], Response::HTTP_SEE_OTHER);
}
#[Route('/{id}/export-pdf', name: 'app_user_export_pdf')]
public function exportPdf(User $user): Response
{
    // Set Dompdf options
    $options = new Options();
    $options->set('isHtml5ParserEnabled', true);
    $options->set('isPhpEnabled', true);

    // Initialize Dompdf with options
    $dompdf = new Dompdf($options);

    // Render the HTML view
    $html = $this->renderView('user/export_form.html.twig', ['user' => $user]);

    // Check if HTML is empty (debugging)
    if (empty($html)) {
        throw new \Exception("Exported HTML is empty.");
    }

    $dompdf->loadHtml($html);
    $dompdf->setPaper('A4', 'portrait'); // Set paper size
    $dompdf->render();

    // Output PDF to a file for debugging
    file_put_contents('debug.pdf', $dompdf->output());

    // Return response with PDF content
    return new Response($dompdf->output(), 200, [
        'Content-Type' => 'application/pdf',
        'Content-Disposition' => 'inline; filename="Profile.pdf"',
    ]);
}
}

