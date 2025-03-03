<?php

namespace App\Service;

use Doctrine\ORM\EntityManagerInterface;

class StatsService
{
    private $em;

    public function __construct(EntityManagerInterface $em)
    {
        $this->em = $em;
    }


    public function getCommentsStatsByPost(): array
    {
        return $this->em->createQuery(
            "SELECT p.id as post_id, p.title as post_title, COUNT(c.id) as comment_count 
            FROM App\Entity\Comment c
            JOIN c.post p 
            GROUP BY p.id
            ORDER BY comment_count DESC"
        )->getResult();
    }
}

