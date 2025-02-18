<?php

namespace App\Entity;

use App\Repository\PostRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\HttpFoundation\File\File;
use Vich\UploaderBundle\Mapping\Annotation as Vich;
use Symfony\Component\Validator\Constraints as Assert;


#[ORM\Entity(repositoryClass: PostRepository::class)]
#[Vich\Uploadable] // Enable VichUploader
class Post
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le title is blank.")]
    #[Assert\Length(min: 5, max: 25, minMessage: "Title should be at least {{ limit }} characters long.", maxMessage: "Title should not exceed {{ limit }} characters.")]
    private ?string $title = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "the content is blank.")]
    #[Assert\Length(min: 10, minMessage: "content should be at least {{ limit }} characters.")]
    private ?string $content = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $image = null; // Store only filename

    #[Vich\UploadableField(mapping: "post_images", fileNameProperty: "image")]
    private ?File $imageFile = null; // Temporary file property for uploads

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Date is blank")]
    #[Assert\Regex(pattern: "/\d{2}-\d{2}-\d{4}/", message: "the date should be DD-MM-YYYY.")]
    private ?string $createDate = null;

    
    /**
     * @var Collection<int, Comment>
     */
    #[ORM\OneToMany(targetEntity: Comment::class, mappedBy: 'post', orphanRemoval: true)]
    private Collection $comments;

    public function __construct()
    {
        $this->comments = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getTitle(): ?string
    {
        return $this->title;
    }

    public function setTitle(string $title): static
    {
        $this->title = $title;
        return $this;
    }

    public function getContent(): ?string
    {
        return $this->content;
    }

    public function setContent(string $content): static
    {
        $this->content = $content;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;
        return $this;
    }

    public function getImageFile(): ?File
    {
        return $this->imageFile;
    }

    public function setImageFile(?File $imageFile = null): void
    {
        $this->imageFile = $imageFile;

    }

    public function getCreateDate(): ?string
    {
        return $this->createDate;
    }

    public function setCreateDate(string $createDate): static
    {
        $this->createDate = $createDate;
        return $this;
    }

    public function getUpdatedAt(): ?\DateTimeInterface
    {
        return $this->updatedAt;
    }

    public function setUpdatedAt(?\DateTimeInterface $updatedAt): void
    {
        $this->updatedAt = $updatedAt;
    }
    #[ORM\Column(type: 'datetime', nullable: true)]
    private ?\DateTimeInterface $updatedAt = null;

    /**
     * @return Collection<int, Comment>
     */
    public function getComments(): Collection
    {
        return $this->comments;
    }

    public function addComment(Comment $comment): static
    {
        if (!$this->comments->contains($comment)) {
            $this->comments->add($comment);
            $comment->setPost($this);
        }
        return $this;
    }

    public function removeComment(Comment $comment): static
    {
        if ($this->comments->removeElement($comment)) {
            if ($comment->getPost() === $this) {
                $comment->setPost(null);
            }
        }
        return $this;
    }
    
}
