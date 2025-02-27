<?php

namespace App\Form;

use App\Entity\Product;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
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
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le nom du produit est requis.']),
                    new Assert\Length(['min' => 3, 'minMessage' => 'Le nom du produit doit contenir au moins 3 caractères.']),
                ],
                'attr' => [
                    'required' => false,
                ],
            ])
            ->add('description', TextType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La description est requise.']),
                    new Assert\Length(['min' => 10, 'minMessage' => 'La description doit contenir au moins 10 caractères.']),
                ],
                'attr' => [
                    'required' => false,
                ],
            ])
            ->add('price', NumberType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le prix est requis.']),
                    new Assert\Positive(['message' => 'Le prix doit être un nombre positif.']),
                ],
                'attr' => [
                    'required' => false,
                ],
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Image (fichier)',
                'required' => false,
                'mapped' => false, // S'assurer que l'image n'est pas mappée à l'entité
                'data_class' => null,  // Pas de classe associée pour ce champ
                'constraints' => [
                    new File([
                        'maxSize' => '5M',
                        'mimeTypes' => ['image/jpeg', 'image/png', 'image/gif'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPEG, PNG, GIF).',
                    ])
                ],
            ])
            ->add('tags', CollectionType::class, [
                'entry_type' => TextType::class,
                'entry_options' => ['label' => false],
                'allow_add' => true,
                'by_reference' => false,
                'label' => 'Tags',
            ])
            ->add('stock', NumberType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La quantité en stock est requise.']),
                    new Assert\PositiveOrZero(['message' => 'La quantité en stock doit être un nombre positif ou égal à zéro.']),
                ],
                'attr' => [
                    'required' => false,
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
