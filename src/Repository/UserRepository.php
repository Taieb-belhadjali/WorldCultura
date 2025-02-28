<?php
namespace App\Repository;

use App\Entity\User;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Security\Core\Exception\UnsupportedUserException;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\PasswordUpgraderInterface;

/**
 * @extends ServiceEntityRepository<User>
 */
class UserRepository extends ServiceEntityRepository implements PasswordUpgraderInterface
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, User::class);
    }

     /**
     * Recherche des utilisateurs selon un terme de recherche et un ordre de tri.
     *
     * @param string|null $searchTerm
     * @param string $sortOrder
     * @param string $role
     * @return User[]
     */
    public function findBySearchTermAndSort(?string $searchTerm,?string $role, string $sortOrder = 'ASC'): array
    {
        $qb = $this->createQueryBuilder('u');

        // If search term is provided, filter users based on 'Nom', 'Prenom', or 'email'
        if ($searchTerm) {
            $qb->andWhere('u.Nom LIKE :searchTerm OR u.Prenom LIKE :searchTerm OR u.email LIKE :searchTerm')
                ->setParameter('searchTerm', '%' . $searchTerm . '%');
        }

        if ($role) {
            $qb->andWhere('u.roles LIKE :role')
                ->setParameter('role','%"'.$role.'"%');
        }

        // Order results by 'Nom' field (or change to another field as needed)
        $qb->orderBy('u.Nom', $sortOrder);

        return $qb->getQuery()->getResult();
    }

    /**
     * Used to upgrade (rehash) the user's password automatically over time.
     */
    public function upgradePassword(PasswordAuthenticatedUserInterface $user, string $newHashedPassword): void
    {
        if (!$user instanceof User) {
            throw new UnsupportedUserException(sprintf('Instances of "%s" are not supported.', $user::class));
        }

        $user->setPassword($newHashedPassword);
        $this->getEntityManager()->persist($user);
        $this->getEntityManager()->flush();
    }


    /**
     * Récupère tous les utilisateurs triés par adresse e-mail en ordre alphabétique.
     *
     * @return User[]
     */
    public function findAllSortedByEmail(): array
    {
        return $this->createQueryBuilder('u')
            ->orderBy('u.email', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Retourne les statistiques sur le Nombre d'administrateurs et d'utilisateurs.
     *
     * @return array
     */
    public function getUserStatistics(): array
    {
        $qb = $this->createQueryBuilder('u')
            ->select('COUNT(u.id) as total, 
                      SUM(CASE WHEN u.roles LIKE :adminRole THEN 1 ELSE 0 END) as adminCount, 
                      SUM(CASE WHEN u.roles LIKE :userRole THEN 1 ELSE 0 END) as userCount')
            ->setParameter('adminRole', '%"ROLE_ADMIN"%')
            ->setParameter('userRole', '%"ROLE_USER"%');

        return $qb->getQuery()->getSingleResult();
    }
}
