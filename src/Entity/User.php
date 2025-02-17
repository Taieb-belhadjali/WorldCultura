<?php

namespace App\Entity;

use App\Repository\UserRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: UserRepository::class)]
#[ORM\Table(name: '`user`')]
#[ORM\UniqueConstraint(name: 'UNIQ_IDENTIFIER_EMAIL', fields: ['email'])]
#[UniqueEntity(fields: ['email'], message: 'There is already an account with this email')]
class User implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255, nullable: false)]
    #[Assert\NotBlank(message: 'Please enter your last name.')]
    #[Assert\Length(max: 255, maxMessage: 'Your last name cannot exceed {{ limit }} characters.')]
    private ?string $nom = null;

    #[Assert\NotBlank(message: 'Please enter your first name.')]
    #[Assert\Length(max: 255, maxMessage: 'Your first name cannot exceed {{ limit }} characters.')]
    #[ORM\Column(length: 255, nullable: false)]
    private ?string $prenom = null;

    #[Assert\NotBlank(message: 'Please enter your email.')]
    #[Assert\Email(message: 'Please enter a valid email address.')]
    #[ORM\Column(length: 180, unique: true, nullable: false)]
    private ?string $email = null;

    #[ORM\Column]
    private array $roles = [];

    private ?bool $agreeTerms = null;

    /**
     * @var string The hashed password
     */
    #[Assert\NotBlank(message: 'Please enter a password.')]
    #[Assert\Length(
        min: 8,
        minMessage: 'Your password should be at least {{ limit }} characters.',
        max: 4096,
    )]
    #[ORM\Column(nullable: false)]
    private ?string $password = null;

    #[ORM\Column]
    private bool $isVerified = false;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;
        return $this;
    }

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): static
    {
        $this->prenom = $prenom;
        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): static
    {
        $this->email = $email;
        return $this;
    }

    public function getUserIdentifier(): string
    {
        return (string) $this->email;
    }

    public function getRoles(): array
    {
        $roles = $this->roles;
        $roles[] = 'ROLE_USER'; // Ensure every user has at least ROLE_USER
        return array_unique($roles); // Avoid duplicate roles
    }

    public function setRoles(array $roles): static
    {
        $this->roles = $roles;
        return $this;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(string $password): static
    {
        $this->password = $password;
        return $this;
    }

    public function eraseCredentials(): void
    {
        // Clear any sensitive data if stored
    }

    public function isVerified(): bool
    {
        return $this->isVerified;
    }

    public function setIsVerified(bool $isVerified): static
    {
        $this->isVerified = $isVerified;
        return $this;
    }

    public function getAgreeTerms(): ?bool
    {
        return $this->agreeTerms;
    }

    public function setAgreeTerms(bool $agreeTerms): static
    {
        $this->agreeTerms = $agreeTerms;
        return $this;
    }
}
