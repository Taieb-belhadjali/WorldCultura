<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250212152159 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE rehla (id INT AUTO_INCREMENT NOT NULL, agence_id INT NOT NULL, depart VARCHAR(255) NOT NULL, destination VARCHAR(255) NOT NULL, depart_date DATETIME NOT NULL, arrival_date DATETIME NOT NULL, price DOUBLE PRECISION NOT NULL, INDEX IDX_FCF1AC7ED725330D (agence_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE trip (id INT AUTO_INCREMENT NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE rehla ADD CONSTRAINT FK_FCF1AC7ED725330D FOREIGN KEY (agence_id) REFERENCES compagnie_aerienne (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE rehla DROP FOREIGN KEY FK_FCF1AC7ED725330D');
        $this->addSql('DROP TABLE rehla');
        $this->addSql('DROP TABLE trip');
    }
}
