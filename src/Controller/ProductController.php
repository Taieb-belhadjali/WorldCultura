<?php

namespace App\Controller;

use App\Entity\Product;
use App\Repository\ProductRepository;
use App\Form\ProductType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\File\File;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/product')]
class ProductController extends AbstractController
{
    #[Route('/', name: 'product_index', methods: ['GET'])]
    public function index(ProductRepository $productRepository): Response
    {
        $products = $productRepository->findAll();

        return $this->render('product/index.html.twig', [
            'products' => $products,
        ]);
    }

    #[Route('/product/new', name: 'product_new', methods: ['GET', 'POST'])]
public function new(Request $request, EntityManagerInterface $entityManager): Response
{
    $product = new Product();
    $form = $this->createForm(ProductType::class, $product);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $imageFile = $form->get('imageFile')->getData();
        // Gestion du fichier image
        if ($imageFile) {
            $newFilename = uniqid() . '.' . $imageFile->guessExtension();

            try {
                $imageFile->move(
                    $this->getParameter('uploads_directory'),
                    $newFilename
                );
                $product->setImage($newFilename); // Assigner l'image au produit
            } catch (FileException $e) {
                $this->addFlash('error', 'Erreur lors du téléchargement de l\'image.');
                return $this->render('product/new.html.twig', [
                    'form' => $form->createView(),
                ]);
            }
        }

        $product->setCreatedAt(new \DateTime());
        $entityManager->persist($product);
        $entityManager->flush();

        $this->addFlash('success', 'Produit créé avec succès !');
        return $this->redirectToRoute('product_index');
    }

    return $this->render('product/new.html.twig', [
        'form' => $form->createView(),
    ]);

    }

    #[Route('/show/{id}', name: 'product_show', methods: ['GET'])]
    public function show(ProductRepository $productRepository, int $id): Response
    {
        $product = $productRepository->find($id);

        if (!$product) {
            throw $this->createNotFoundException('Produit non trouvé.');
        }
        // $products = $productRepository->findBySomeCriteria($product);  // Remplace cette ligne par ta logique pour récupérer des produits similaires


        return $this->render('product/show.html.twig', [
            'product' => $product,
            // 'products' => $products,
        ]);
    }

    #[Route('/{id}/edit', name: 'product_form_edit', methods: ['GET', 'POST'])]
    public function editForm(Request $request, ProductRepository $productRepository, int $id, EntityManagerInterface $entityManager): Response
    {
        $product = $productRepository->find($id);

        if (!$product) {
            throw $this->createNotFoundException('Produit non trouvé.');
        }

        if ($product->getImage()) {
            $product->setImageFile(
                new File($this->getParameter('uploads_directory') . '/' . $product->getImage())
            );
        }

        $form = $this->createForm(ProductType::class, $product);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('imageFile')->getData();
            if ($imageFile) {
                $newFilename = uniqid() . '.' . $imageFile->guessExtension();
                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
                    $this->addFlash('error', 'Erreur lors du téléchargement de l\'image.');
                }

                $product->setImage($newFilename);
            }

            $entityManager->flush();

            $this->addFlash('success', 'Produit modifié avec succès !');
            return $this->redirectToRoute('product_index');
        }

        return $this->render('product/edit.html.twig', [
            'form' => $form->createView(),
            'product' => $product,
        ]);
    }

    #[Route('/product/delete/{id}', name: 'product_delete', methods: ['POST'])]
    public function delete(Request $request, ProductRepository $productRepository, int $id, EntityManagerInterface $entityManager): Response
    {
        $product = $productRepository->find($id);

        if (!$product) {
            throw $this->createNotFoundException('Produit non trouvé.');
        }

        if ($this->isCsrfTokenValid('delete' . $product->getId(), $request->request->get('_token'))) {
            if (!$product->getOrderItems()->isEmpty()) {
                $this->addFlash('danger', 'Impossible de supprimer ce produit car il est lié à une ou plusieurs commandes.');
                return $this->redirectToRoute('product_index');
            }

            if ($product->getImage()) {
                $imagePath = $this->getParameter('uploads_directory') . '/' . $product->getImage();
                if (file_exists($imagePath)) {
                    unlink($imagePath);
                }
            }

            $entityManager->remove($product);
            $entityManager->flush();

            $this->addFlash('success', 'Produit supprimé avec succès !');
        }

        return $this->redirectToRoute('product_index');
    }
}
