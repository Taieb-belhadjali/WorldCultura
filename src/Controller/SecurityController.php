<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class SecurityController extends AbstractController
{
    #[Route(path: '/Login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
         if ($this->getUser()) {
             return $this->redirectToRoute('app_test_temp');
         }

        // get the login error if there is one
        $error = $authenticationUtils->getLastAuthenticationError();
        // last username entered by the user
        $lastUsername = $authenticationUtils->getEmail();

        return $this->render('/templates/base.html.twig', ['last_username' => $lastUsername, 'error' => $error]);
    }

    #[Route('/redirect_after_login', name: 'app_redirect_after_login')]
    public function redirectAfterLogin(): Response
    {
        return $this->redirectToRoute('app_test_temp');
    }

    #[Route(path: '/logout', name: 'app_logout')]
    public function logout(): void
    {
    }
}
