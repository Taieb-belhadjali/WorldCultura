<?php

namespace App\Form;

use App\Entity\Reservation;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;

class ReservationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
         $builder
              ->add('userName', TextType::class, [
                  'label' => 'Votre nom'
              ])
              ->add('email', EmailType::class, [
                  'label' => 'Votre email'
              ])
              ->add('contact', TextType::class, [
                  'label' => 'Votre contact'
              ]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
         $resolver->setDefaults([
             'data_class' => Reservation::class,
         ]);
    }
}
