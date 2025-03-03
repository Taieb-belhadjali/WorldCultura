<?php
namespace App\DataFixtures;

use App\Entity\Order;
use App\Entity\OrderItem;
use App\Entity\Product;
use Doctrine\Persistence\ObjectManager;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Faker\Factory;

class AppFixtures extends Fixture
{
    public function load(ObjectManager $manager): void // Ajout de ": void"
    {
        // Crée un objet Faker pour générer des données aléatoires
        $faker = Factory::create();

        // Récupérer tous les produits existants dans la base de données
        $products = $manager->getRepository(Product::class)->findAll();

        // Vérifier si des produits existent
        if (empty($products)) {
            throw new \LogicException('No products found. Please ensure Product fixtures are loaded first.');
        }

        // Générer des commandes
        for ($i = 1; $i <= 5; $i++) {
            $order = new Order();
            $order->setStatus('pending');
            $order->setTotalPrice(0); // Le total sera calculé plus tard
            $order->setCreatedAt(new \DateTimeImmutable());

            // Ajouter des OrderItems
            for ($j = 1; $j <= rand(1, 3); $j++) {
                $product = $faker->randomElement($products); // Sélectionner un produit aléatoire
                $orderItem = new OrderItem();
                $orderItem->setProduct($product);
                $orderItem->setQuantity(rand(1, 5)); // Quantité aléatoire
                $orderItem->setPrice($product->getPrice());

                // Calculer le total de la commande
                $order->setTotalPrice($order->getTotalPrice() + ($orderItem->getPrice() * $orderItem->getQuantity()));
                $order->addOrderItem($orderItem);

                $manager->persist($orderItem);
            }

            $manager->persist($order);
        }

        // Sauvegarder toutes les entités créées
        $manager->flush();
    }
}

