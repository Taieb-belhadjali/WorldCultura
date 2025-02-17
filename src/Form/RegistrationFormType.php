<?php

namespace App\Form;

use App\Entity\User;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\RepeatedType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\IsTrue;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\EqualTo;

class RegistrationFormType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('nom', TextType::class, [
                'label' => 'Nom',
                'constraints' => [
                    new NotBlank(['message' => 'Please enter your last name.']),
                    new Length([
                        'max' => 255,
                        'maxMessage' => 'Your last name cannot exceed {{ limit }} characters.',
                    ]),
                ],
            ])
            ->add('prenom', TextType::class, [
                'label' => 'Prénom',
                'constraints' => [
                    new NotBlank(['message' => 'Please enter your first name.']),
                    new Length([
                        'max' => 255,
                        'maxMessage' => 'Your first name cannot exceed {{ limit }} characters.',
                    ]),
                ],
            ])
            ->add('email', EmailType::class, [
                'label' => 'Email Address',
                'constraints' => [
                    new NotBlank(['message' => 'Please enter your email.']),
                    //new Email(['message' => 'Please enter a valid email address.']),
                ],
            ])
            ->add('plainPassword', RepeatedType::class, [
                'type' => PasswordType::class,
                'mapped' => false,
                'first_options' => ['label' => 'Password'],
                'second_options' => ['label' => 'Confirm Password'],
                'constraints' => [
                    new NotBlank(['message' => 'Please enter a password.']),
                    new Length([
                        'min' => 8,
                        'minMessage' => 'Your password should be at least {{ limit }} characters.',
                        'max' => 4096,
                    ]),
                ],
            ])
            
            ->add('agreeTerms', CheckboxType::class, [
                'mapped' => false,
                'constraints' => [
                    new IsTrue(['message' => 'You must agree to our terms.']),
                ],
                'label' => 'I agree to the terms and conditions',
            ])
            ->add('submit', SubmitType::class, ['label' => 'Register']);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => User::class,
        ]);
    }
}
