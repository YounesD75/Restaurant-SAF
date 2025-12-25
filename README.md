# Restaurant-SAF

Framework d'acteurs cooperants via microservices Spring Boot (inspire d'Akka) sur le theme restaurant.
Le projet fournit un framework d'acteurs (saf-core1), des microservices (restaurant, inventory, client),
un annuaire Eureka, une demo frontend, des logs lisibles, de la scalabilite, et des tests d'integration.

## Sommaire
- Architecture
- Prerequis
- Installation
- Lancer les services
- Demo rapide (curl)
- Demo Postman
- Scalabilite (scale up/down)
- Logs
- Tests
- Depannage rapide
- Reutiliser le framework (saf-core1)
- Contributeurs

## Architecture (modules)
- `saf-core1`: framework d'acteurs (mailbox, supervision, scalabilite locale).
- `saf-discovery`: annuaire Eureka.
- `saf-restaurant`: gestion commandes, tickets, tresorerie.
- `InventoryService`: gestion stock (BDD + callbacks).
- `saf-client`: proxy REST vers restaurant (client).
- `frontend`: UI admin (Vite).

## Prerequis
- Java 17+ (recommande: 21)
- Maven 3.8+
- PostgreSQL 14+ (local)
- Node.js 18+ et npm (pour le frontend)
- Docker (optionnel, pour les tests d'integration automatiques)

## Installation (local)
```bash
sudo service postgresql start
sudo -u postgres psql -c "CREATE DATABASE saf_restaurant;"
sudo -u postgres psql -c "CREATE DATABASE inventory_db;"
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"
```

Build des modules (sans tests):
```bash
cd ~/Restaurant-SAF
mvn -N install
mvn -pl saf-core1,saf-restaurant,InventoryService,saf-discovery,saf-client -am clean package -DskipTests
```

## Lancer les services (1 terminal par service)
Eureka:
```bash
mvn -pl saf-discovery spring-boot:run
```

Inventory:
```bash
mvn -pl InventoryService spring-boot:run
```

Restaurant:
```bash
mvn -pl saf-restaurant spring-boot:run
```

Client:
```bash
mvn -pl saf-client spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
npm run dev
```

### Ports
- Eureka: http://localhost:8761
- Restaurant: http://localhost:8081
- Inventory: http://localhost:8083
- Client: http://localhost:8080
- Frontend: http://localhost:5173

## Demo rapide (curl)
Menu:
```bash
curl http://localhost:8080/client/menu
```

Passer une commande:
```bash
curl -X POST http://localhost:8080/client/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientName":"Alice",
    "tableNumber":"12",
    "items":[
      {"dishName":"burger","quantity":2},
      {"dishName":"drink","quantity":2}
    ],
    "instructions":"sans oignons"
  }'
```

Recevoir un ticket (remplacer ORDER_ID):
```bash
curl http://localhost:8080/client/receipts/ORDER_ID
```

Tresorerie:
```bash
curl http://localhost:8081/treasury
```

Liste des tickets:
```bash
curl http://localhost:8081/receipts
```

Commande avec plat inconnu:
```bash
curl -X POST http://localhost:8080/client/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientName":"Bob",
    "tableNumber":"7",
    "items":[{"dishName":"unknown-item","quantity":1}]
  }'
```

## Demo Postman
Fichiers:
- `postman/Restaurant-SAF.postman_collection.json`
- `postman/Restaurant-SAF.postman_environment.json`

Etapes:
1. Importer la collection + l'environnement.
2. Choisir l'environnement "Restaurant-SAF Local".
3. Executer les requetes dans l'ordre:
   - Menu (Client)
   - Passer une commande (OK)
   - Recevoir le ticket
   - Tresorerie
   - Tickets
   - Commande avec plat inconnu (erreur)

## Scalabilite (scale up/down)
Inventory:
```bash
curl -X POST "http://localhost:8083/api/admin/scaling/stock/up?count=2"
curl -X POST "http://localhost:8083/api/admin/scaling/stock/down?count=1"
```

Client:
```bash
curl -X POST "http://localhost:8080/api/admin/scaling/client/up?count=2"
curl -X POST "http://localhost:8080/api/admin/scaling/client/down?count=1"
```

## Logs (lisibles)
```bash
tail -f saf-restaurant/logs/restaurant.log
tail -f InventoryService/logs/inventory.log
tail -f saf-client/logs/client.log
```

## Tests
Framework d'acteurs:
```bash
mvn -pl saf-core1 test
```

Tests microservices (unitaires):
```bash
mvn -pl saf-restaurant test
mvn -pl InventoryService test
mvn -pl saf-client test
```

Test d'integration cross-services (auto, via Docker/Testcontainers):
```bash
mvn -pl saf-client -am verify
```

Rapports:
```bash
ls -la saf-client/target/failsafe-reports
cat saf-client/target/failsafe-reports/com.saf.client.CrossServiceIntegrationIT.txt
```

## Depannage rapide
- Erreurs "POM missing" => lancer les builds depuis la racine `~/Restaurant-SAF`.
- Erreurs CORS front => redemarrer `saf-restaurant`.
- "Unknown dish" => Inventory seed via Flyway (V2).
- Eureka non dispo => lancer `saf-discovery` en premier.

## Reutiliser le framework (saf-core1)
Si vous voulez utiliser le framework d'acteurs dans un autre projet, voici le minimum a integrer.

### Dependances Maven
Dans le `pom.xml` du service:
```xml
<dependency>
  <groupId>com.saf</groupId>
  <artifactId>saf-core1</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Classes a reimplementer
1. Creer des messages (DTO) qui implementent `com.saf.core1.Message`.
2. Creer des acteurs qui implementent `com.saf.core1.Actor`.
3. Creer un `LocalActorSystem` et un `ActorRef` racine.

### Exemple minimal (pseudo)
```java
LocalActorSystem system = new LocalActorSystem(threads, mailboxSize, supervisionConfig);
ActorRef root = system.actorOf("root", () -> new MyRootActor());
root.tell(new MyMessage(...));
```

### Integration Spring (recommande)
- Declarer un bean `LocalActorSystem` dans une classe `@Configuration`.
- Gerer l'arret propre du systeme a la fin (ex: `@PreDestroy`).

### Ajuster la configuration
- Taille du pool (threads).
- Strategie de supervision (`SupervisionConfig`).
- Router pool si besoin (`RoundRobinPool`).

### A remplacer dans votre projet
- Messages metier (DTO).
- Acteurs metier (logique de traitement).
- Endpoints REST qui envoient des messages aux acteurs.

## Contributeurs
- Equipe CY Tech (Restaurant-SAF)

---
Ce README permet a un correcteur de cloner, installer, lancer et valider tous les flux du projet.
