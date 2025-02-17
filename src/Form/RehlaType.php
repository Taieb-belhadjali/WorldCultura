<?php

namespace App\Form;

use App\Entity\CompagnieAerienne;
use App\Entity\Rehla;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class RehlaType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('depart')
            ->add('destination')
            ->add('depart_date', null, [
                'widget' => 'single_text',
            ])
            ->add('arrival_date', null, [
                'widget' => 'single_text',
            ])
            ->add('price')
            ->add('agence', EntityType::class, [
                'class' => CompagnieAerienne::class,
                'choice_label' => 'nom',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Rehla::class,
        ]);
    }
}
