# LogiTrack

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

## Feuille de route

- [ ] Gestion de flotte et API Gateway
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