<?php
namespace App\DataFixtures;

use App\Entity\Product;
use Doctrine\Persistence\ObjectManager;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Faker\Factory;

class ProductFixtures extends Fixture
{
    public function load(ObjectManager $manager): void
    {
        $faker = Factory::create(); // Créer une instance de Faker

        $categories = ['Électronique', 'Livres', 'Maison', 'Vêtements', 'Autres']; // Liste des catégories
        $tagsList = ['Promotion', 'Nouveau', 'Best-seller', 'Solde', 'Exclusif']; // Liste des tags

        for ($i = 0; $i < 10; $i++) { // Générer 10 produits
            $product = new Product();
            $product->setName($faker->word()); // Nom aléatoire
            $product->setDescription($faker->paragraph()); // Description aléatoire
            $product->setPrice($faker->randomFloat(2, 5, 100)); // Prix aléatoire entre 5 et 100
            $product->setImage('default-image.jpg'); // Image par défaut

            // Assigner une catégorie aléatoire parmi celles définies
            $category = $faker->randomElement($categories); // Catégorie aléatoire
            $product->setCategory($category); // Assigner la catégorie au produit

            // Assigner une quantité de stock aléatoire
            $stock = $faker->numberBetween(0, 100); // Stock aléatoire entre 0 et 100
            $product->setStock($stock); // Assigner la quantité de stock

            // Assigner des tags aléatoires
            $tags = $faker->randomElements($tagsList, $faker->numberBetween(1, 3)); // Choisir entre 1 et 3 tags aléatoires
            $product->setTags(implode(', ', $tags)); // Les tags séparés par des virgules
            $product->setUpdatedAt(new \DateTime()); // Date actuelle pour "updatedAt"

            $manager->persist($product); // Persister le produit
        }

        $manager->flush(); // Sauvegarder tous les produits
    }
}
