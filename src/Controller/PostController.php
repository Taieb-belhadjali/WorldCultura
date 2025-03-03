<?php

namespace App\Controller;

use App\Entity\Post;
use App\Form\PostType;
use App\Repository\PostRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\String\Slugger\SluggerInterface;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

#[Route('/post')]
final class PostController extends AbstractController
{
    #[Route(name: 'app_poste_index', methods: ['GET'])]
public function imdex(
    PostRepository $postRepository,
    PaginatorInterface $paginator,
    Request $request
): Response {
    // Get and validate parameters
    $query = $request->query->get('q', '');
    $sort = in_array($request->query->get('sort'), ['newest', 'oldest']) 
        ? $request->query->get('sort') 
        : 'newest';
    $daysBefore = $request->query->getInt('daysBefore', 0);
    $currentPage = max(1, $request->query->getInt('page', 1));

    // Build filtered query
    $qb = $postRepository->createQueryBuilder('p')
        ->orderBy('p.createDate', $sort === 'oldest' ? 'ASC' : 'DESC');

    if (!empty($query)) {
        $qb->andWhere('p.title LIKE :query')
           ->setParameter('query', '%' . addcslashes($query, '%_') . '%');
    }

    if ($daysBefore > 0) {
        $qb->andWhere('p.createDate >= :date')
           ->setParameter('date', new \DateTimeImmutable("-$daysBefore days"));
    }

    // Paginate with query preservation
    $pagination = $paginator->paginate(
        $qb->getQuery(),
        $currentPage,
        5,
        [
            'query' => $request->query->all(),
            'pageParameterName' => 'page'
        ]
    );

    // AJAX response handling
    if ($request->isXmlHttpRequest()) {
        return $this->render('post/index.html.twig', [
            'pagination' => $pagination,
            'is_ajax' => true
        ]);
    }

    // Full page response
    return $this->render('post/index.html.twig', [
        'pagination' => $pagination,
        'is_ajax' => false
    ]);
}

    #[Route('/new', name: 'app_post_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $post = new Post();
        $form = $this->createForm(PostType::class, $post);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('imageFile')->getData();
            
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('images_directory'), // This must be set in services.yaml
                        $newFilename
                    );
                    $post->setImage($newFilename);
                } catch (FileException $e) {
                    $this->addFlash('error', 'Image upload failed!');
                }
            }

            $entityManager->persist($post);
            $entityManager->flush();

            return $this->redirectToRoute('app_post_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('post/new.html.twig', [
            'post' => $post,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_post_show', methods: ['GET'])]
    public function show(Post $post): Response
    {
        return $this->render('post/show.html.twig', [
            'post' => $post,
            'comments' => $post->getComments(), // Ajout des commentaires
        ]);
    }
    #[Route('/post/search', name: 'app_post_search', methods: ['GET'])]
public function search(Request $request, PostRepository $postRepository): Response
{
    $query = $request->query->get('q', '');
    $posts = $query ? $postRepository->searchByTitle($query) : $postRepository->findAll();

    return $this->render('post/index.html.twig', [
        'posts' => $posts,
        'query' => $query,
    ]);
}

    #[Route('/{id}/edit', name: 'app_post_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Post $post, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $form = $this->createForm(PostType::class, $post);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('imageFile')->getData();
            
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('images_directory'),
                        $newFilename
                    );
                    $post->setImage($newFilename);
                } catch (FileException $e) {
                    $this->addFlash('error', 'Image upload failed!');
                }
            }

            $entityManager->flush();

            return $this->redirectToRoute('app_post_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('post/edit.html.twig', [
            'post' => $post,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_post_delete', methods: ['POST'])]
    public function delete(Request $request, Post $post, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$post->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($post);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_post_index', [], Response::HTTP_SEE_OTHER);
    }







    #[Route(name: 'app_post_index', methods: ['GET'])]
public function index(PostRepository $postRepository, PaginatorInterface $paginator, Request $request): Response
{
    $query = $postRepository->createQueryBuilder('p')
        ->orderBy('p.createDate', 'DESC') // Sort posts by newest first
        ->getQuery();

    $pagination = $paginator->paginate(
        $query,
        $request->query->getInt('page', 1), // Get the current page number (default: 1)
        5 // Number of posts per page
    );
    
    return $this->render('post/index.html.twig', [
        'pagination' => $pagination, // Send paginated posts to Twig
    ]);
}



/////////////////likeee
#[Route('/{id}/like', name: 'app_post_like', methods: ['POST'])]
public function like(Post $post, EntityManagerInterface $entityManager, SessionInterface $session): Response
{
    $likedPosts = $session->get('liked_posts', []);

    if (in_array($post->getId(), $likedPosts)) {
        // Remove like if already liked
        $post->removeLike($post->getId());
        $likedPosts = array_diff($likedPosts, [$post->getId()]);
    } else {
        // Add like and remove dislike if exists
        $post->addLike($post->getId());
        $post->removeDislike($post->getId());
        $likedPosts[] = $post->getId();
    }

    $session->set('liked_posts', $likedPosts);
    $entityManager->flush();

    return $this->json([
        'likes' => $post->getLikesCount(),
        'dislikes' => $post->getDislikesCount(),
    ]);
}

#[Route('/{id}/dislike', name: 'app_post_dislike', methods: ['POST'])]
public function dislike(Post $post, EntityManagerInterface $entityManager, SessionInterface $session): Response
{
    $dislikedPosts = $session->get('disliked_posts', []);

    if (in_array($post->getId(), $dislikedPosts)) {
        // Remove dislike if already disliked
        $post->removeDislike($post->getId());
        $dislikedPosts = array_diff($dislikedPosts, [$post->getId()]);
    } else {
        // Add dislike and remove like if exists
        $post->addDislike($post->getId());
        $post->removeLike($post->getId());
        $dislikedPosts[] = $post->getId();
    }

    $session->set('disliked_posts', $dislikedPosts);
    $entityManager->flush();

    return $this->json([
        'likes' => $post->getLikesCount(),
        'dislikes' => $post->getDislikesCount(),
    ]);
}


}
