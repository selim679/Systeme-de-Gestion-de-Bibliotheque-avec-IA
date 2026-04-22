Mini-Projet : Système de Gestion de Bibliothèque avec IA

Description du Projet

Ce projet est un système de gestion de bibliothèque développé avec Spring Boot, exposant des API RESTful pour la gestion des livres, des auteurs, des membres et des emprunts. Une fonctionnalité clé de cette application est l'intégration d'un chatbot basé sur l'API Groq (utilisant un modèle de langage comme Llama 3) pour offrir des recommandations et des recherches de livres intelligentes aux utilisateurs.

Le projet a été réalisé dans le cadre du module Web Services et API pour l’Intelligence Artificielle (4ème IA) et vise à démontrer la capacité à concevoir et développer une application backend robuste, intégrant des traitements métier complexes et des fonctionnalités d'IA, tout en respectant les bonnes pratiques de développement et de documentation API.

Fonctionnalités Principales

•
Gestion des Livres : CRUD complet pour les livres, incluant le titre, l'ISBN, la date de publication, le genre, et le nombre d'exemplaires.

•
Gestion des Auteurs : CRUD pour les auteurs, avec leurs informations biographiques.

•
Gestion des Membres : CRUD pour les membres de la bibliothèque.

•
Gestion des Emprunts : Enregistrement des emprunts et des retours, avec calcul automatique des pénalités en cas de retard.

•
Relations Complexes : Implémentation de relations Many-to-Many (Livre-Auteur) et One-to-Many (Livre-Emprunt, Membre-Emprunt).

•
Recherche Avancée de Livres : API de recherche multicritères (titre, auteur, genre, ISBN, disponibilité).

•
Statistiques : API pour récupérer les livres les plus empruntés et les auteurs les plus populaires.

•
Chatbot de Recommandation/Recherche (IA) : Intégration de l'API Groq pour un chatbot intelligent capable d'interpréter les requêtes utilisateur et de suggérer des livres pertinents basés sur le catalogue de la bibliothèque.

•
Documentation API : Toutes les API sont documentées via Swagger UI (Springdoc-OpenAPI) pour faciliter la compréhension et les tests.

Technologies Utilisées

•
Backend : Java 17+, Spring Boot 3.x

•
Base de Données : H2 Database (en mémoire pour le développement)

•
ORM : Spring Data JPA, Hibernate

•
API REST : Spring Web

•
Gestion de Projet : Maven

•
Documentation API : Springdoc-OpenAPI (Swagger UI)

•
Intelligence Artificielle : API Groq (modèle Llama 3)

•
Outils de Développement : IntelliJ IDEA, Git, GitHub

Architecture Applicative

L'application suit une architecture en couches classique pour les applications Spring Boot :

•
Contrôleurs (Controllers) : Gèrent les requêtes HTTP entrantes et renvoient les réponses.

•
Services (Services) : Contiennent la logique métier de l'application.

•
Dépôts (Repositories) : Gèrent l'accès aux données et les opérations CRUD avec la base de données.

•
Entités (Entities) : Représentent les objets persistants dans la base de données.

•
DTOs (Data Transfer Objects) : Utilisés pour transférer les données entre les couches et les clients API.

L'intégration de l'IA se fait via un GroqService qui interagit avec l'API externe de Groq, puis interprète les résultats pour interroger la base de données locale et fournir des recommandations.

Comment Démarrer

Prérequis

•
Java Development Kit (JDK) 17 ou supérieur

•
Maven 3.x

•
IntelliJ IDEA (recommandé)

•
Un compte Groq et une clé API Groq (disponible sur Groq Cloud)

1. Cloner le Dépôt

Bash


git clone <URL_DE_VOTRE_DEPOT>
cd bibliotheque



2. Configuration de la Clé API Groq

Créez un fichier application.properties (ou application.yml) dans src/main/resources et ajoutez votre clé API Groq :

Plain Text


groq.api.key=VOTRE_CLE_API_GROQ
groq.api.url=https://api.groq.com/openai/v1/chat/completions
groq.model=llama3-8b-8192 # Ou un autre modèle Groq



3. Lancer l'Application

Vous pouvez lancer l'application via IntelliJ IDEA ou en utilisant Maven :

Bash


mvn spring-boot:run



L'application sera accessible sur http://localhost:8080.

Accès aux API et à la Documentation

•
Swagger UI : Une fois l'application démarrée, accédez à la documentation interactive de l'API via http://localhost:8080/swagger-ui.html.

•
Console H2 : Pour visualiser la base de données en mémoire, accédez à http://localhost:8080/h2-console. Utilisez jdbc:h2:mem:bibliotheque_db comme URL JDBC, sa comme utilisateur et un mot de passe vide.


Contribution

Les contributions ne sont pas attendues pour ce mini-projet individuel. Cependant, n'hésitez pas à explorer le code et à l'adapter à vos besoins.

Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

Auteur

[Votre Nom]

Remerciements

•
À mon professeur pour le sujet de projet stimulant.

•
À la communauté Spring Boot pour l'écosystème riche et la documentation complète.

•
À Groq pour leur API d'inférence rapide et accessible.

