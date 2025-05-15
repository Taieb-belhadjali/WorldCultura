# WorldCultura – Symfony Backend

WorldCultura est une plateforme innovante dédiée à la découverte des pays et à la promotion de l’échange culturel. Elle permet aux utilisateurs de réserver des vols, d’acheter des produits locaux, de consulter des événements culturels, de partager des avis sur un blog et de gérer leurs réclamations.

---

## Aperçu

Ce backend Symfony prend en charge les fonctionnalités suivantes :

- Gestion des utilisateurs avec rôles (administrateurs,client)
- Réservation de vols internationaux
- Achat de produits locaux importés ou artisanaux
- Consultation d’événements culturels (festivals, expositions…)
- Blog participatif pour partager des expériences de voyage
- Système de gestion des réclamations

---

## Fonctionnalités

- Système d’authentification sécurisé avec rôles personnalisés
- Réservation de vols avec gestion d’historique
- Boutique multi-pays : ajout au panier, commande, suivi
- Blog communautaire : création, lecture, mise à jour et suppression d’articles/commentaires
- Calendrier des événements culturels par pays
- Système de réclamations : soumission, suivi et réponse
- Tableau de bord administrateur pour la gestion du contenu, des utilisateurs et des commandes

---

## Stack Technique

### Backend

- PHP 8.1.25
- Symfony 6.4
- Doctrine ORM
- MySQL
- Composer (gestionnaire de dépendances)


---

### Autres Outils

- Symfony Maker Bundle (génération de code rapide)
- JWT Authentication Bundle (authentification sécurisée)
- Symfony UX / Twig pour les vues optionnelles

---

## Structure du Répertoire

```
/src
├── Controller/      # Contrôleurs des routes API
├── Entity/          # Entités Doctrine (User, Flight, Product, Event, BlogPost, Complaint…)
├── Repository/      # Requêtes personnalisées sur la base de données
├── Security/        # Gestion des tokens et rôles
/config              # Fichiers de configuration
/migrations          # Fichiers de migration de base de données
/public              # Point d’entrée du backend
```

---

## Démarrage Rapide

1. **Cloner le dépôt :**
    ```bash
    git clone https://github.com/votre-utilisateur/worldcultura-backend.git
    cd worldcultura-backend
    ```

2. **Installer les dépendances :**
    ```bash
    composer install
    ```

3. **Configurer l’environnement :**
    ```bash
    cp .env .env.local
    # Modifier .env.local avec vos paramètres de base de données
    ```

4. **Créer la base de données et les tables :**
    ```bash
    php bin/console doctrine:database:create
    php bin/console doctrine:migrations:migrate
    ```

5. **Lancer le serveur de développement :**
    ```bash
    symfony server:start
    ```

---
![image](https://github.com/user-attachments/assets/ce8b8db1-07a8-4a61-bf53-6d6b1f4067a0)


## Remerciements

Merci à toute l’équipe de développement pour la conception de ce projet innovant.  
Un projet imaginé pour encourager l’exploration culturelle et faciliter les échanges internationaux.

**WorldCultura – Voyager, Découvrir, Connecter.**

