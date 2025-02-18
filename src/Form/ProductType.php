<?php
namespace App\Form;

use App\Entity\Product;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints as Assert;

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
                    'required' => false,  // Désactive la vérification par défaut du navigateur
                ],
            ])
            ->add('description', TextType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La description est requise.']),
                    new Assert\Length(['min' => 10, 'minMessage' => 'La description doit contenir au moins 10 caractères.']),
                ],
                'attr' => [
                    'required' => false,  // Désactive la vérification par défaut du navigateur
                ],
            ])
            ->add('price', NumberType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le prix est requis.']),
                    new Assert\Positive(['message' => 'Le prix doit être un nombre positif.']),
                ],
                'attr' => [
                    'required' => false,  // Désactive la vérification par défaut du navigateur
                ],
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Image (fichier)',
                'required' => false,
                'mapped' => false,
                'data_class' => null,
                'constraints' => [
                    new Assert\Image(['maxSize' => '5M', 'mimeTypes' => ['image/png', 'image/jpeg'], 'mimeTypesMessage' => 'Veuillez télécharger une image valide (PNG ou JPEG).']),
                ],
                'attr' => [
                    'required' => false,  // Désactive la vérification par défaut du navigateur
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Product::class,
        ]);
    }
}
