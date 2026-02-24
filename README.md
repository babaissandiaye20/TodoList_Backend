# TodoList Backend — API REST Spring Boot

Application web de gestion de tâches (To-Do List) développée dans le cadre du rattrapage UFC / UNCHK.

---

## Technologies & Versions

| Technologie | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.3 |
| Spring Data JPA | (inclus Spring Boot) |
| Spring Web MVC | (inclus Spring Boot) |
| Hibernate | 7.2.4 |
| MySQL Connector | 9.6.0 |
| Lombok | (inclus Spring Boot) |
| Springdoc OpenAPI (Swagger) | 3.0.1 |
| HikariCP (pool connexions) | 7.0.2 |
| Maven | Wrapper inclus |

---

## Prérequis

- Java 17+
- MySQL 8+
- Maven (ou utiliser `./mvnw`)

---

## Installation & Lancement

**1. Cloner le projet**
```bash
git clone <url-du-repo>
cd TodoList_Backend
```

**2. Configurer l'environnement**
```bash
cp .env.example .env
# Modifier .env avec vos identifiants MySQL
```

**3. Créer la base de données**
```sql
CREATE DATABASE todolist_db;
```

**4. Lancer l'application**
```bash
./mvnw spring-boot:run
```
> Les variables du fichier `.env` sont automatiquement chargées grâce à `spring.config.import` (Spring Boot 4.x natif).

**5. Accéder à l'API**
- API : `http://localhost:8080/api/todos`
- Swagger UI : `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON : `http://localhost:8080/api-docs`

---

## Architecture du Projet

### Arborescence des fichiers

```
TodoList_Backend/
├── .env                          → Variables d'environnement locales (ignoré par git)
├── .env.example                  → Template des variables d'environnement
├── .gitignore                    → Fichiers ignorés par git
├── pom.xml                       → Dépendances Maven
└── src/
    └── main/
        ├── java/com/TodoList/TodoList_Backend/
        │   │
        │   ├── TodoListBackendApplication.java       → Point d'entrée Spring Boot
        │   │
        │   ├── config/
        │   │   └── OpenApiConfig.java                → Configuration Swagger/OpenAPI
        │   │
        │   ├── entity/
        │   │   ├── Todo.java                         → Entité JPA (table todos)
        │   │   └── enums/
        │   │       └── TodoStatus.java               → Enum : EN_COURS | TERMINEE
        │   │
        │   ├── dto/
        │   │   ├── TodoRequestDto.java               → Données reçues (création/modification)
        │   │   ├── TodoResponseDto.java              → Données retournées au client
        │   │   └── TodoStatusDto.java                → Changement de statut uniquement
        │   │
        │   ├── repository/
        │   │   └── TodoRepository.java               → Accès base de données (JPA + @Query)
        │   │
        │   ├── service/
        │   │   ├── TodoService.java                  → Interface : contrat métier
        │   │   └── TodoServiceImpl.java              → Implémentation + @Transactional
        │   │
        │   ├── helper/
        │   │   ├── TodoHelper.java                   → Interface : contrat d'orchestration
        │   │   └── TodoHelperImpl.java               → Orchestration : service + mapper + exceptions
        │   │
        │   ├── mapper/
        │   │   └── TodoMapper.java                   → Conversion Entity ↔ DTO
        │   │
        │   ├── response/
        │   │   ├── ApiResponse.java                  → Format de réponse succès standardisé
        │   │   └── ErrorResponse.java                → Format de réponse erreur standardisé
        │   │
        │   ├── exception/
        │   │   ├── BadRequestException.java          → Erreur 400 : requête invalide
        │   │   ├── ResourceNotFoundException.java    → Erreur 404 : ressource introuvable
        │   │   ├── ResourceAlreadyExistsException.java → Erreur 409 : doublon
        │   │   ├── InternalErrorException.java       → Erreur 500 : erreur serveur
        │   │   └── GlobalExceptionHandler.java       → Gestionnaire global (@RestControllerAdvice)
        │   │
        │   └── controller/
        │       └── TodoController.java               → Endpoints REST HTTP
        │
        └── resources/
            └── application.yaml                      → Configuration de l'application
```

### Flux d'une requête

```
HTTP Request
     ↓
TodoController          ← reçoit la requête HTTP, délègue au helper
     ↓
TodoHelper (interface)  ← orchestre : validation métier, mapping, exceptions
     ↓           ↓
TodoService    TodoMapper
     ↓
TodoRepository          ← accès base de données via JPA
     ↓
MySQL Database
     ↑
GlobalExceptionHandler  ← intercepte toute exception et retourne une ErrorResponse JSON
```

---

## Modèle de Données

### Entité `Todo`

| Champ | Type | Contraintes |
|---|---|---|
| `id` | `UUID` | Clé primaire, auto-généré par Hibernate |
| `titre` | `String` | Non null, unique |
| `description` | `String` | Nullable |
| `statut` | `TodoStatus` (enum) | Non null, défaut : `EN_COURS` |
| `dateCreation` | `LocalDateTime` | Non null, auto-rempli à la création |
| `dateDebut` | `LocalDate` | Nullable, défaut : `dateCreation` si non fournie |
| `datePrevueFin` | `LocalDate` | Nullable |

### Enum `TodoStatus`

| Valeur | Description |
|---|---|
| `EN_COURS` | Tâche en cours (valeur par défaut) |
| `TERMINEE` | Tâche terminée |

---

## Endpoints API

### Base URL : `http://localhost:8080/api/todos`

---

### 1. Créer une tâche
**`POST /api/todos`**

**Body (JSON) :**
```json
{
  "titre": "Ma tâche",
  "description": "Description optionnelle",
  "dateDebut": "2026-03-01",
  "datePrevueFin": "2026-12-31"
}
```

**Critères d'acceptation :**
- `titre` obligatoire, entre 2 et 100 caractères
- `dateDebut` doit être aujourd'hui ou dans le futur (si fournie) — sinon défaut : `LocalDate.now()`
- `datePrevueFin` doit être aujourd'hui ou dans le futur (si fournie)
- **`dateDebut` ne doit pas être postérieure à `datePrevueFin`** — sinon `400 Bad Request` (`INVALID_DATE_RANGE`)
- Le titre doit être unique — sinon `409 Conflict`
- Statut initialisé à `EN_COURS` automatiquement
- Retourne `201 Created` avec la tâche créée

**Réponses :**

| Code | Description |
|---|---|
| `201` | Tâche créée avec succès |
| `400` | Données invalides (titre manquant, date passée) |
| `409` | Une tâche avec ce titre existe déjà |

---

### 2. Lister toutes les tâches
**`GET /api/todos`**

**Paramètres optionnels :**

| Paramètre | Type | Description |
|---|---|---|
| `statut` | `EN_COURS` / `TERMINEE` | Filtrer par statut |
| `dateDebut` | `yyyy-MM-dd` | Date de création minimale |
| `dateFin` | `yyyy-MM-dd` | Date de création maximale |

**Exemples :**
```
GET /api/todos
GET /api/todos?statut=EN_COURS
GET /api/todos?dateDebut=2026-01-01&dateFin=2026-12-31
GET /api/todos?statut=TERMINEE&dateDebut=2026-01-01
```

**Critères d'acceptation :**
- Sans paramètre → retourne toutes les tâches
- Les filtres sont cumulables
- Retourne une liste vide si aucun résultat (pas d'erreur)
- Retourne `200 OK`

---

### 3. Récupérer une tâche par ID
**`GET /api/todos/{id}`**

**Critères d'acceptation :**
- `{id}` doit être un UUID valide
- Retourne `404` si la tâche n'existe pas
- Retourne `200 OK` avec la tâche

| Code | Description |
|---|---|
| `200` | Tâche trouvée |
| `404` | Tâche non trouvée |

---

### 4. Modifier une tâche
**`PUT /api/todos/{id}`**

**Body (JSON) :**
```json
{
  "titre": "Nouveau titre",
  "description": "Nouvelle description",
  "datePrevueFin": "2026-06-30"
}
```

**Critères d'acceptation :**
- `{id}` doit exister — sinon `404`
- `titre` obligatoire, entre 2 et 100 caractères
- Le nouveau titre ne doit pas appartenir à une autre tâche — sinon `409`
- `dateDebut` et `datePrevueFin` doivent être aujourd'hui ou dans le futur (si fournies)
- **`dateDebut` ne doit pas être postérieure à `datePrevueFin`** — sinon `400 Bad Request` (`INVALID_DATE_RANGE`)
- Le statut n'est pas modifié par cet endpoint

| Code | Description |
|---|---|
| `200` | Tâche modifiée avec succès |
| `400` | Données invalides |
| `404` | Tâche non trouvée |
| `409` | Titre déjà utilisé par une autre tâche |

---

### 5. Changer le statut d'une tâche
**`PATCH /api/todos/{id}/status`**

**Body (JSON) :**
```json
{
  "statut": "TERMINEE"
}
```

**Critères d'acceptation :**
- `{id}` doit exister — sinon `404`
- `statut` obligatoire : `EN_COURS` ou `TERMINEE`
- Seul le statut est modifié (titre/description inchangés)
- Retourne `200 OK` avec la tâche mise à jour

| Code | Description |
|---|---|
| `200` | Statut mis à jour avec succès |
| `400` | Statut invalide ou manquant |
| `404` | Tâche non trouvée |

---

### 6. Supprimer une tâche
**`DELETE /api/todos/{id}`**

**Critères d'acceptation :**
- `{id}` doit exister — sinon `404`
- La tâche doit avoir le statut `TERMINEE` — sinon `400 Bad Request`
- Suppression définitive (pas de corbeille)
- Retourne `200 OK` avec message de confirmation

| Code | Description |
|---|---|
| `200` | Tâche supprimée avec succès |
| `400` | Tâche non terminée (statut ≠ TERMINEE) |
| `404` | Tâche non trouvée |

---

### 7. Tâches en retard
**`GET /api/todos/en-retard`**

**Critères d'acceptation :**
- Retourne uniquement les tâches dont le statut est `EN_COURS`
- ET dont la `datePrevueFin` est définie ET dépassée (< aujourd'hui)
- Retourne une liste vide si aucune tâche en retard
- Retourne `200 OK`

| Code | Description |
|---|---|
| `200` | Liste des tâches en retard |

---

## Format des Réponses

### Succès
```json
{
  "success": true,
  "status": 200,
  "message": "Opération réussie",
  "data": { ... },
  "timestamp": "2026-02-24T12:00:00"
}
```

### Erreur
```json
{
  "success": false,
  "status": 404,
  "message": "Todo non trouvé(e)",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2026-02-24T12:00:00"
}
```

### Erreur de validation (400)
```json
{
  "success": false,
  "status": 400,
  "message": "Erreurs de validation",
  "errorCode": "VALIDATION_ERROR",
  "errors": [
    {
      "field": "titre",
      "message": "Le titre est obligatoire",
      "rejectedValue": null
    }
  ],
  "timestamp": "2026-02-24T12:00:00"
}
```

---

## Respect des Principes SOLID

### S — Single Responsibility (Responsabilité unique)
Chaque classe a une seule raison de changer :
- `TodoController` : gère uniquement le protocole HTTP (status codes, réponses)
- `TodoHelperImpl` : orchestre uniquement la logique d'orchestration (validation métier, mapping, exceptions)
- `TodoServiceImpl` : gère uniquement les opérations en base de données + transactions
- `TodoMapper` : convertit uniquement entre Entity et DTO
- `GlobalExceptionHandler` : gère uniquement la transformation des exceptions en réponses JSON

### O — Open/Closed (Ouvert/Fermé)
Le code est ouvert à l'extension, fermé à la modification :
- `ApiResponse` et `ErrorResponse` exposent des méthodes factory statiques extensibles sans modifier les existantes
- Ajouter un nouveau type d'exception ne modifie pas `GlobalExceptionHandler` — on ajoute simplement un nouveau `@ExceptionHandler`
- Les filtres du `TodoRepository` utilisent des paramètres optionnels (`IS NULL`) — ajouter un filtre n'impacte pas les appels existants

### L — Liskov Substitution (Substitution de Liskov)
Les implémentations sont substituables à leurs interfaces :
- `TodoServiceImpl` implémente `TodoService` — le `TodoHelperImpl` dépend uniquement de l'interface, pas de l'implémentation
- `TodoHelperImpl` implémente `TodoHelper` — le `TodoController` dépend uniquement de l'interface
- On peut substituer n'importe quelle implémentation sans changer le code appelant

### I — Interface Segregation (Ségrégation des interfaces)
Les interfaces sont ciblées et cohésives :
- `TodoService` expose uniquement les opérations de persistence
- `TodoHelper` expose uniquement les opérations d'orchestration métier
- Les DTOs sont séparés par usage : `TodoRequestDto` (entrée), `TodoResponseDto` (sortie), `TodoStatusDto` (changement de statut) — le client n'envoie que ce dont il a besoin

### D — Dependency Inversion (Inversion des dépendances)
Les modules de haut niveau dépendent d'abstractions :
- `TodoController` → dépend de `TodoHelper` (interface), jamais de `TodoHelperImpl`
- `TodoHelperImpl` → dépend de `TodoService` (interface), jamais de `TodoServiceImpl`
- Spring IoC injecte les implémentations concrètes via `@RequiredArgsConstructor` — le code ne crée jamais `new TodoServiceImpl()`

---

## Variables d'Environnement

Les variables sont définies dans le fichier `.env` à la racine du projet et chargées automatiquement via `spring.config.import` (Spring Boot 4.x natif, aucun plugin nécessaire).

| Variable | Description | Défaut |
|---|---|---|
| `DB_URL` | URL JDBC de la base de données | `jdbc:mysql://localhost:3306/todolist_db` |
| `DB_USERNAME` | Nom d'utilisateur MySQL | `root` |
| `DB_PASSWORD` | Mot de passe MySQL | *(vide)* |
| `SERVER_PORT` | Port du serveur | `8080` |

> **⚠️ Important** : Le fichier `.env` contient des secrets et est ignoré par Git (`.gitignore`). Utilisez `.env.example` comme modèle.
