<?php
namespace App\DataFixtures;

use App\Entity\Product;
use Doctrine\Persistence\ObjectManager;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Faker\Factory;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\File\File;
use Faker\Provider\Image;
use Faker\Provider\Lorem;

class ProductFixtures extends Fixture
{
    public function load(ObjectManager $manager): void
    {
        $faker = Factory::create(); // Create a Faker instance

        for ($i = 0; $i < 10; $i++) { // Generate 10 products
            $product = new Product();
            $product->setName($faker->word()); // Random product name
            $product->setDescription($faker->paragraph()); // Random product description
            $product->setPrice($faker->randomFloat(2, 5, 100)); // Random price between 5 and 100
            $product->setImage('default-image.jpg');  // Assigner une image par défaut

            $manager->persist($product); // Persist the product

            // Optionally set categories or other fields as needed:
            // $product->setCategory($this->getReference('some-category')); // If you have categories
        }

        $manager->flush(); // Save all products
    }
}
