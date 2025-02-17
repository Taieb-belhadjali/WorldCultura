<?php

namespace App\Entity;

use App\Repository\RehlaRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: RehlaRepository::class)]
class Rehla
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(inversedBy: 'rehlas')]
    #[ORM\JoinColumn(nullable: false)]
    #[Assert\NotNull(message: "L'agence est obligatoire.")]
    private ?CompagnieAerienne $agence = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le champ départ est obligatoire.")]
    #[Assert\Length(
        min: 3,
        max: 255,
        minMessage: "Le lieu de départ doit contenir au moins {{ limit }} caractères.",
        maxMessage: "Le lieu de départ ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $depart = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le champ destination est obligatoire.")]
    #[Assert\Length(
        min: 3,
        max: 255,
        minMessage: "La destination doit contenir au moins {{ limit }} caractères.",
        maxMessage: "La destination ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $destination = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    #[Assert\NotNull(message: "La date de départ est obligatoire.")]
    #[Assert\Type(\DateTimeInterface::class, message: "La date de départ doit être une date valide.")]
    #[Assert\GreaterThan("today", message: "La date de départ doit être dans le futur.")]
    private ?\DateTimeInterface $depart_date = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    #[Assert\NotNull(message: "La date d'arrivée est obligatoire.")]
    #[Assert\Type(\DateTimeInterface::class, message: "La date d'arrivée doit être une date valide.")]
    #[Assert\GreaterThan(propertyPath: "depart_date", message: "La date d'arrivée doit être postérieure à la date de départ.")]
    private ?\DateTimeInterface $arrival_date = null;

    #[ORM\Column]
    #[Assert\NotNull(message: "Le prix est obligatoire.")]
    #[Assert\Positive(message: "Le prix doit être un nombre positif.")]
    #[Assert\LessThan(
        value: 10000,
        message: "Le prix ne peut pas dépasser {{ value }}."
    )]
    private ?float $price = null;

    #[ORM\OneToMany(mappedBy: 'rehla', targetEntity: Reservation::class, orphanRemoval: true)]
    private Collection $reservations;

    public function __construct()
    {
        $this->reservations = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getAgence(): ?CompagnieAerienne
    {
        return $this->agence;
    }

    public function setAgence(?CompagnieAerienne $agence): static
    {
        $this->agence = $agence;
        return $this;
    }

    public function getDepart(): ?string
    {
        return $this->depart;
    }

    public function setDepart(string $depart): static
    {
        $this->depart = $depart;
        return $this;
    }

    public function getDestination(): ?string
    {
        return $this->destination;
    }

    public function setDestination(string $destination): static
    {
        $this->destination = $destination;
        return $this;
    }

    public function getDepartDate(): ?\DateTimeInterface
    {
        return $this->depart_date;
    }

    public function setDepartDate(\DateTimeInterface $depart_date): static
    {
        $this->depart_date = $depart_date;
        return $this;
    }

    public function getArrivalDate(): ?\DateTimeInterface
    {
        return $this->arrival_date;
    }

    public function setArrivalDate(\DateTimeInterface $arrival_date): static
    {
        $this->arrival_date = $arrival_date;
        return $this;
    }

    public function getPrice(): ?float
    {
        return $this->price;
    }

    public function setPrice(float $price): static
    {
        $this->price = $price;
        return $this;
    }

    /**
     * @return Collection<int, Reservation>
     */
    public function getReservations(): Collection
    {
        return $this->reservations;
    }

    public function addReservation(Reservation $reservation): static
    {
        if (!$this->reservations->contains($reservation)) {
            $this->reservations->add($reservation);
            $reservation->setRehla($this);
        }
        return $this;
    }

    public function removeReservation(Reservation $reservation): static
    {
        if ($this->reservations->removeElement($reservation)) {
            // Définir le côté propriétaire à null si nécessaire
            if ($reservation->getRehla() === $this) {
                $reservation->setRehla(null);
            }
        }
        return $this;
    }
}
