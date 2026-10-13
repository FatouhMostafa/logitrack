# LogiTrack
[![CI](https://github.com/FatouhMostafa/logitrack/actions/workflows/ci.yml/badge.svg)](https://github.com/FatouhMostafa/logitrack/actions/workflows/ci.yml)
> Plateforme logistique temps réel : gestion de flotte, suivi GPS des livraisons, alertes d'arrivée et assistant IA.
> Projet portfolio en microservices **Spring Boot · Angular · Kafka · Kubernetes**.

**Statut : en construction.** Ce dépôt est développé publiquement, étape par étape.
Ce README ne décrit que ce qui fonctionne réellement ; il évolue avec le code.

## Objectif

LogiTrack permet à un dispatcher de :

- gérer une flotte de véhicules et de chauffeurs ;
- créer et assigner des livraisons, puis suivre leur cycle de vie ;
- voir les camions bouger sur une carte en temps réel ;
- être alerté quand un livreur arrive à destination ;
- interroger un assistant IA qui agit avec ses droits, après confirmation.

## Architecture visée

[Architecture](docs/images/architecture.png) 

## Stack prévue

Java 21 · Spring Boot 4 · Spring Cloud Gateway · PostgreSQL · Redis · Apache Kafka · Keycloak ·
Angular · Python / FastAPI · Docker · GitHub Actions · Kubernetes

## Ce qui fonctionne aujourd'hui

- API REST de gestion des véhicules et des chauffeurs (création, statut, appairage chauffeur ↔ véhicule)
- Règles métier testées : transitions de statut contrôlées, un chauffeur par véhicule
- Erreurs au format standard `ProblemDetail` (RFC 9457)
- API Gateway : point d'entrée unique sur `http://localhost:8080`
- Base PostgreSQL versionnée par Flyway, données de démonstration

## Démarrer en local

**Prérequis :** Java 21, Maven 3.9, Docker. Sous Windows : WSL2.

    git clone https://github.com/FatouhMostafa/logitrack.git
    cd logitrack
    cp .env.example .env
    make up            # PostgreSQL + fleet-service + gateway
    curl http://localhost:8080/api/v1/vehicles

Documentation de l'API : http://localhost:8081/swagger-ui.html
Swagger UI is available when running the Fleet Service in development mode.

## Tests

    make test          # tests unitaires + tests d'intégration avec PostgreSQL réel (Testcontainers)

## Structure du dépôt

    gateway/                    API Gateway (Spring Cloud Gateway)
    services/fleet-service/     véhicules et chauffeurs
    docker/                     Docker Compose, Dockerfile, PostgreSQL
    docs/                       architecture et plan de réalisation

## Feuille de route

- [x] Gestion de flotte et API Gateway
- [ ] Livraisons, sécurité Keycloak, résilience
- [ ] Suivi GPS, simulateur, interface Angular
- [ ] Déploiement en ligne et CI/CD
- [ ] Kafka, événements fiables, temps réel
- [ ] Observabilité et tests de charge
- [ ] Assistant IA
- [ ] Kubernetes et autoscaling

## Auteur

**Fatouh** — [LinkedIn](https://www.linkedin.com/in/mostafa-fatouh-01a0a2353/)

## Licence

[MIT](LICENSE)
