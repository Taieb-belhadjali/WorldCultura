<?php

namespace App\Entity;

use App\Repository\CompagnieAerienneRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CompagnieAerienneRepository::class)]
class CompagnieAerienne
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le nom est obligatoire.")]
    private ?string $nom = null;

    #[ORM\Column(length: 255)]
    private ?string $logo = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "La description est obligatoire.")]
    private ?string $description = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le contact du responsable est obligatoire.")]
    #[Assert\Regex(
        pattern: "/^\d+$/",
        message: "Le contact du responsable doit contenir uniquement des chiffres."
    )]
    private ?string $contact_du_responsable = null;

    /**
     * @var Collection<int, Vole>
     */
    private Collection $voles;

    public function __construct()
    {
        $this->voles = new ArrayCollection();
    }

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

    public function getLogo(): ?string
    {
        return $this->logo;
    }

    public function setLogo(string $logo): static
    {
        $this->logo = $logo;
        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;
        return $this;
    }

    public function getContactDuResponsable(): ?string
    {
        return $this->contact_du_responsable;
    }

    public function setContactDuResponsable(string $contact_du_responsable): static
    {
        $this->contact_du_responsable = $contact_du_responsable;
        return $this;
    }

    /**
     * @return Collection<int, Vole>
     */
    public function getVoles(): Collection
    {
        return $this->voles;
    }

    public function addVole(Vole $vole): static
    {
        if (!$this->voles->contains($vole)) {
            $this->voles->add($vole);
            $vole->setCompagnieAerienne($this);
        }
        return $this;
    }

    public function removeVole(Vole $vole): static
    {
        if ($this->voles->removeElement($vole)) {
            // set the owning side to null (unless already changed)
            if ($vole->getCompagnieAerienne() === $this) {
                $vole->setCompagnieAerienne(null);
            }
        }
        return $this;
    }
}
