<?php

namespace App\Entity;

use App\Repository\ReservationRepository;
use Doctrine\ORM\Mapping as ORM;
use App\Entity\Rehla;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ReservationRepository::class)]
class Reservation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: "integer")]
    private ?int $id = null;

    // Relation avec Rehla
    #[ORM\ManyToOne(targetEntity: Rehla::class)]
    #[ORM\JoinColumn(nullable: false)]
    #[Assert\NotNull(message: "La relation avec le voyage est obligatoire.")]
    private ?Rehla $rehla = null;

    #[ORM\Column(type: "string", length: 255)]
    #[Assert\NotBlank(message: "Le nom d'utilisateur est obligatoire.")]
    #[Assert\Length(
        min: 3,
        max: 25,
        minMessage: "Le nom d'utilisateur doit contenir au moins {{ limit }} caractères.",
        maxMessage: "Le nom d'utilisateur ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $userName = null;

    #[ORM\Column(type: "string", length: 255)]
    #[Assert\NotBlank(message: "L'email est obligatoire.")]
    #[Assert\Email(message: "L'email doit être valide.")]
    private ?string $email = null;

    #[ORM\Column(type: "string", length: 20)]
    #[Assert\NotBlank(message: "Le contact est obligatoire.")]
    #[Assert\Regex(
        pattern: "/^\d+$/",
        message: "Le contact doit contenir uniquement des chiffres."
    )]
    private ?string $contact = null;

    // Ajout de l'ID utilisateur
    #[ORM\Column(type: "integer", nullable: true)]
    private ?int $userId = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getRehla(): ?Rehla
    {
        return $this->rehla;
    }

    public function setRehla(Rehla $rehla): self
    {
        $this->rehla = $rehla;
        return $this;
    }

    public function getUserName(): ?string
    {
        return $this->userName;
    }

    public function setUserName(string $userName): self
    {
        $this->userName = $userName;
        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): self
    {
        $this->email = $email;
        return $this;
    }

    public function getContact(): ?string
    {
        return $this->contact;
    }

    public function setContact(string $contact): self
    {
        $this->contact = $contact;
        return $this;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(?int $userId): self
    {
        $this->userId = $userId;
        return $this;
    }
}
