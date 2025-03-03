<?php

namespace App\Form;

use App\Entity\Reclamation;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Validator\Constraints\NotBlank ;
use Symfony\Component\Validator\Constraints\Length ;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ReclamationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('user_name' )
            ->add('type', ChoiceType::class, [
                'choices'  => [
                    'Vol' => 'vol',
                    'Événement' => 'evenement',
                    'Autre' => 'autre',
                ],
                'placeholder' => 'Sélectionnez un type',
            ])
    
            ->add('description' , TextareaType::class,[
                'label' => 'Description',
            ])
            
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Reclamation::class,
        ]);
    }

    


    
}
