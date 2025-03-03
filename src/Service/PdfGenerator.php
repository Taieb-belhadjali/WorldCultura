<?php
// src/Service/PdfGenerator.php
namespace App\Service;

use Dompdf\Dompdf;
use Dompdf\Options;

class PdfGenerator
{
    private Dompdf $dompdf;

    public function __construct()
    {
        // Configurez les options de Dompdf
        $options = new Options();
        $options->set('defaultFont', 'Helvetica');
        // Activez les images distantes si besoin
        $options->set('isRemoteEnabled', true);

        $this->dompdf = new Dompdf($options);
    }

    /**
     * Génère un PDF à partir du contenu HTML fourni.
     *
     * @param string $html Le contenu HTML à transformer en PDF
     * @return string Le contenu binaire du PDF généré
     */
    public function generatePdf(string $html): string
    {
        // Charger le HTML
        $this->dompdf->loadHtml($html);

        // Définir le format et l'orientation du papier
        $this->dompdf->setPaper('A4', 'portrait');

        // Générer le PDF
        $this->dompdf->render();

        // Retourner le contenu PDF
        return $this->dompdf->output();
    }
}
