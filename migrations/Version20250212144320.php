<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250212144320 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE vole (id INT AUTO_INCREMENT NOT NULL, compagnie_aerienne_id INT NOT NULL, depart VARCHAR(255) NOT NULL, destination VARCHAR(255) NOT NULL, depart_date DATETIME NOT NULL, arrival_date DATETIME NOT NULL, INDEX IDX_D80B1D3D9086501E (compagnie_aerienne_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE vole ADD CONSTRAINT FK_D80B1D3D9086501E FOREIGN KEY (compagnie_aerienne_id) REFERENCES compagnie_aerienne (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE vole DROP FOREIGN KEY FK_D80B1D3D9086501E');
        $this->addSql('DROP TABLE vole');
    }
}
