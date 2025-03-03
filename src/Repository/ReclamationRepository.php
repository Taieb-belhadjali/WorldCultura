<?php

namespace App\Repository;

use App\Entity\Reclamation;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Reclamation>
 */
class ReclamationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Reclamation::class);
    }

    /**
     * Custom method to search reclamations based on a search term
     * 
     * @param string $searchTerm
     * @return Reclamation[] Returns an array of Reclamation objects
     */
    public function findBySearchTerm(string $searchTerm = ''): array
    {
        $queryBuilder = $this->createQueryBuilder('r');

        // Si un terme de recherche est fourni, ajouter des conditions à la requête
        if (!empty($searchTerm)) {
            $queryBuilder->where('r.user_name LIKE :searchTerm')
                        ->orWhere('r.type LIKE :searchTerm')
                        ->orWhere('r.description LIKE :searchTerm')
                        ->orWhere('r.statut LIKE :searchTerm')
                        ->setParameter('searchTerm', '%' . $searchTerm . '%');
        }

        // Ajouter un tri personnalisé basé sur le statut
        $queryBuilder->addSelect(
            "CASE 
                WHEN r.statut = 'Résolu' THEN 1 
                WHEN r.statut = 'En cours' THEN 2 
                WHEN r.statut = 'En attente' THEN 3 
                ELSE 4 
            END AS HIDDEN sortOrder"
        )
        ->orderBy('sortOrder', 'ASC'); // Trier par l'ordre défini

        return $queryBuilder->getQuery()->getResult();
    }

    public function findByAdminSearchTerm(string $searchTerm = ''): array
    {
        $queryBuilder = $this->createQueryBuilder('r');

        // Si un terme de recherche est fourni, ajouter des conditions à la requête
        if (!empty($searchTerm)) {
            $queryBuilder->where('r.user_name LIKE :searchTerm')
                        ->orWhere('r.type LIKE :searchTerm')
                        ->orWhere('r.description LIKE :searchTerm')
                        ->orWhere('r.statut LIKE :searchTerm')
                        ->setParameter('searchTerm', '%' . $searchTerm . '%');
        }

        // Ajouter un tri personnalisé basé sur le statut
        $queryBuilder->addSelect(
            "CASE 
                WHEN r.statut = 'En attente' THEN 1 
                WHEN r.statut = 'En cours' THEN 2 
                WHEN r.statut = 'Résolu' THEN 3
                ELSE 4 
            END AS HIDDEN sortOrder"
        )
        ->orderBy('sortOrder', 'ASC'); // Trier par l'ordre défini

        return $queryBuilder->getQuery()->getResult();
    }


}
