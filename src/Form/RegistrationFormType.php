<?php

namespace App\Form;

use App\Entity\User;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Karser\Recaptcha3Bundle\Form\Recaptcha3Type;
use Karser\Recaptcha3Bundle\Validator\Constraints\Recaptcha3;

class RegistrationFormType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', TextType::class)
            ->add('prenom', TextType::class)
            ->add('email')
            ->add('agreeTerms', CheckboxType::class, [
                'mapped' => false,
            ])
            ->add('password', PasswordType::class, [
                'mapped' => true,
                'attr' => ['autocomplete' => 'new-password'],
            ])
        ;
        $builder->add('captcha', Recaptcha3Type::class, [
            'constraints' => [new Recaptcha3()],
            'mapped' => false,
            'action_name' => 'register',
            //'script_nonce_csp' => $nonceCSP,
            'locale' => 'en',
        ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => User::class,
        ]);
    }
}
