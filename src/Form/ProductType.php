<?php

namespace App\Form;

use App\Entity\Product;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\CollectionType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\File;

class ProductType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('name', TextType::class, [
                'label' => 'Nom du produit',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le nom du produit est requis.']),
                    new Assert\Length(['min' => 3, 'minMessage' => 'Le nom du produit doit contenir au moins 3 caractères.']),
                ],
            ])
            ->add('description', TextType::class, [
                'label' => 'Description',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La description est requise.']),
                    new Assert\Length(['min' => 10, 'minMessage' => 'La description doit contenir au moins 10 caractères.']),
                ],
            ])
            ->add('price', NumberType::class, [
                'label' => 'Prix',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le prix est requis.']),
                    new Assert\Positive(['message' => 'Le prix doit être un nombre positif.']),
                ],
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Image (fichier)',
                'required' => false,
                'mapped' => false, // Non mappé à l'entité Product
                'constraints' => [
                    new File([
                        'maxSize' => '5M',
                        'mimeTypes' => ['image/jpeg', 'image/png', 'image/gif'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPEG, PNG, GIF).',
                    ])
                ],
            ])
            ->add('tags', TextType::class, [
                'required' => false,
                'label' => 'Tags (séparés par des virgules)',
                'attr' => ['placeholder' => 'Exemple: tag1, tag2'],
            ])
            ->add('category', ChoiceType::class, [
                'label' => 'Catégorie',
                'choices' => [
                    'Électronique' => 'Électronique',
                    'Livres' => 'Livres',
                    'Vêtements' => 'Vêtements',
                    'Maison' => 'Maison',
                    'Autres' => 'Autres',
                ],
                'placeholder' => 'Choisissez une catégorie',
                'required' => true,
            ])
            ->add('stock', NumberType::class, [
                'label' => 'Stock',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La quantité en stock est requise.']),
                    new Assert\PositiveOrZero(['message' => 'La quantité en stock doit être un nombre positif ou égal à zéro.']),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Product::class, // Lier directement à Product
        ]);
    }
}
