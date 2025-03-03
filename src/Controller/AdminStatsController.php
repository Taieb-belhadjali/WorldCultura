<?php

namespace App\Controller;

use App\Service\StatsService;
use Khill\Lavacharts\Lavacharts;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class AdminStatsController extends AbstractController
{
    

    public function dashboard(StatsService $statsService, Lavacharts $lava): Response
    {
        $commentsStats = $statsService->getCommentsStatsByPost();


        
                  $groupedStats = [];
                  foreach ($commentsStats as $stat) {
                      if (!isset($groupedStats[$stat['post_title']])) {
                          $groupedStats[$stat['post_title']] = 0;
                      }
                      
                      $groupedStats[$stat['post_title']] += $stat['comment_count'];
                  }

                  $dataTable = $lava->DataTable();
                  $dataTable->addStringColumn('Post Title')
                    ->addNumberColumn('Comments');

                  foreach ($groupedStats as $postTitle => $commentCount) {
                    $dataTable->addRow([$postTitle, $commentCount]);
                    }

        $lava->ColumnChart('comments', $dataTable, [
            'title' => 'Comments per Post',
            'height' => 400,
            'width' => 900
        ]);

        return $this->render('admin/stats.html.twig', [
            'lava' => $lava,
        ]);
    }
}
