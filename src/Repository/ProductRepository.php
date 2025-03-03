<?php

namespace App\Repository;

use App\Entity\Product;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Doctrine\ORM\Tools\Pagination\Paginator;
use Knp\Component\Pager\PaginatorInterface;

/**
 * @extends ServiceEntityRepository<Product>
 */
class ProductRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Product::class);
    }

    //    /**
    //     * @return Product[] Returns an array of Product objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('p')
    //            ->andWhere('p.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('p.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Product
    //    {
    //        return $this->createQueryBuilder('p')
    //            ->andWhere('p.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
 // Exemple dans ProductRepository.php
public function findBySomeCriteria(Product $product)
{
    // Exemple de logique pour récupérer des produits similaires (par catégorie, par exemple)
    return $this->createQueryBuilder('p')
        ->andWhere('p.category = :category')
        ->setParameter('category', $product->getCategory())
        ->andWhere('p.id != :id')  // Éviter de récupérer le produit actuel
        ->setParameter('id', $product->getId())
        ->getQuery()
        ->getResult();
}
public function findByKeyword($keyword)
{
    return $this->createQueryBuilder('p')
        ->where('p.name LIKE :keyword OR p.description LIKE :keyword')
        ->setParameter('keyword', '%'.$keyword.'%')
        ->getQuery()
        ->getResult();
}
public function findByCategoryAndSort(?string $category, ?string $sortField, ?string $sortDirection)
{
    $qb = $this->createQueryBuilder('p');

    // Filtrage par catégorie
    if ($category) {
        $qb->andWhere('p.category = :category')
           ->setParameter('category', $category);
    }

    // Application du tri
    if ($sortField && $sortDirection) {
        $qb->orderBy($sortField, $sortDirection);
    }

    return $qb->getQuery()->getResult();
}

// src/Repository/ProductRepository.php

public function findAllCategories()
{
    $queryBuilder = $this->createQueryBuilder('p')
        ->select('DISTINCT p.category')
        ->where('p.category IS NOT NULL')
        ->orderBy('p.category', 'ASC');

    return $queryBuilder->getQuery()->getResult();
}
// src/Repository/ProductRepository.php

public function findByCategoryAndSortWithPagination(
    ?string $category,
    ?string $sortField,
    ?string $sortDirection,
    int $page,
    int $limit
) {
    $qb = $this->createQueryBuilder('p');

    // Filtrage par catégorie
    if ($category) {
        $qb->andWhere('p.category = :category')
           ->setParameter('category', $category);
    }

    // Application du tri
    if ($sortField && $sortDirection) {
        $qb->orderBy('p.' . $sortField, $sortDirection);
    }

    // Pagination
    $qb->setFirstResult(($page - 1) * $limit)
       ->setMaxResults($limit);

    $paginator = new \Doctrine\ORM\Tools\Pagination\Paginator($qb);

    return $paginator;
}
public function findByCategoryAndSearchWithPagination(
    ?string $category,
    ?string $keyword,
    ?string $sortField,
    ?string $sortDirection,
    int $page,
    int $limit
) {
    $qb = $this->createQueryBuilder('p');

    // Filtrage par catégorie
    if ($category) {
        $qb->andWhere('p.category = :category')
           ->setParameter('category', $category);
    }

    // Recherche par mot-clé (nom ou description du produit)
    if ($keyword) {
        $qb->andWhere('p.name LIKE :keyword OR p.description LIKE :keyword')
           ->setParameter('keyword', '%' . $keyword . '%');
    }

    // Application du tri
    if ($sortField && $sortDirection) {
        $qb->orderBy('p.' . $sortField, $sortDirection);
    }

    // Pagination
    $qb->setFirstResult(($page - 1) * $limit)
       ->setMaxResults($limit);

    $paginator = new Paginator($qb);

    return $paginator;
}




}
