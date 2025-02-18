<?php
namespace App\Form;

use App\Entity\Order;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\DateTimeType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Positive;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class OrderType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('status', ChoiceType::class, [
            'choices' => [
                'Delivery' => 'DELIVERY',
                'Completed' => 'COMPLETED',
            ],
            'label' => '📌 Statut',
            'attr' => ['class' => 'form-select'],
            ])
            ->add('totalPrice', NumberType::class, [
                'label' => '💳 Prix total',
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Saisir le prix total',
                    'required' => false,   
                ],
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le prix total est obligatoire.']),
                    new Assert\Positive(['message' => 'Le prix total doit être positif.']),
                ],])
            ->add('createdAt', DateTimeType::class, [
                'widget' => 'single_text',
                'data' => new \DateTimeImmutable(),
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Order::class,
        ]);
    }
}
