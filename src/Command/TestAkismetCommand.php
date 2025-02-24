<?php
namespace App\Command;

use App\Service\AkismetService;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(name: 'app:test-akismet')]
class TestAkismetCommand extends Command
{
    private AkismetService $akismetService;

    public function __construct(AkismetService $akismetService)
    {
        parent::__construct();
        $this->akismetService = $akismetService;
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $spamDetected = $this->akismetService->isSpam(
            "This is a spam comment with a suspicious link: buy-cheap-drugs.com",
            "127.0.0.1",
            "Mozilla/5.0"
        );

        $output->writeln($spamDetected ? "⚠️ Spam détecté !" : "✅ Pas de spam.");
        return Command::SUCCESS;
    }
}
