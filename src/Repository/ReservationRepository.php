<?php
// src/Repository/ReservationRepository.php
namespace App\Repository;

use App\Entity\Reservation;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Reservation>
 */
class ReservationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Reservation::class);
    }

    public function getTopCompagniesAeriennes(): array
    {
        return $this->createQueryBuilder('r')
            ->select('c.nom as compagnie, COUNT(r.id) as totalReservations')
            ->join('r.rehla', 'rehla') // Jointure avec Rehla
            ->join('rehla.agence', 'c') // Jointure avec CompagnieAerienne via la propriété "agence"
            ->groupBy('c.nom')
            ->orderBy('totalReservations', 'DESC')
            ->setMaxResults(5)
            ->getQuery()
            ->getResult();
    }

    public function getTopDestinations(): array
    {
        return $this->createQueryBuilder('r')
            ->select('rehla.destination, COUNT(r.id) as totalReservations')
            ->join('r.rehla', 'rehla') // Jointure avec Rehla
            ->groupBy('rehla.destination')
            ->orderBy('totalReservations', 'DESC')
            ->setMaxResults(5)
            ->getQuery()
            ->getResult();
    }

    public function getChiffreAffaireParCompagnie(): array
    {
        return $this->createQueryBuilder('r')
            ->select('c.nom as compagnie, SUM(rehla.price) as chiffreAffaire')
            ->join('r.rehla', 'rehla') // Jointure avec Rehla
            ->join('rehla.agence', 'c') // Jointure avec CompagnieAerienne via la propriété "agence"
            ->groupBy('c.nom')
            ->getQuery()
            ->getResult();
    }
}