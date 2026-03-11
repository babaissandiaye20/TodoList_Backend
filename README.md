# TodoList Backend — API REST Spring Boot

Projet de rattrapage — Formation Continue UFC / UNCHK
Mini-projet : Gestion de Tâches (To-Do List)

---

## Description

API REST permettant de gérer une liste de tâches. Chaque tâche possède :

| Champ         | Type                    | Description                      |
|---------------|-------------------------|----------------------------------|
| `id`          | UUID                    | Identifiant unique (généré auto) |
| `titre`       | String                  | Titre de la tâche (obligatoire)  |
| `description` | String                  | Description (optionnelle)        |
| `statut`      | `EN_COURS` / `TERMINEE` | Statut de la tâche               |

---

## Technologies utilisées

| Technologie       | Version        |
|-------------------|----------------|
| Java              | 17             |
| Spring Boot       | 4.0.3          |
| Spring Data JPA   | (inclus)       |
| MySQL             | 8+             |
| Lombok            | (inclus)       |
| Springdoc OpenAPI | 2.x            |
| Maven             | Wrapper inclus |

---

## Installation & Lancement

**1. Configurer la base de données**

Dans `src/main/resources/application.yaml`, renseignez vos identifiants MySQL :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todolist_db
    username: votre_utilisateur
    password: votre_mot_de_passe
```

**2. Créer la base de données**

```sql
CREATE DATABASE todolist_db;
```

**3. Lancer l'application**

```bash
./mvnw spring-boot:run
```

**4. Accéder à l'API**

- API : `http://localhost:8080/api/todos`
- Swagger UI : `http://localhost:8080/swagger-ui.html`

---

## Endpoints — Critères d'acceptation & Exceptions

### 1. Créer une tâche — `POST /api/todos`

**Corps de la requête :**
```json
{
  "titre": "Apprendre Spring Boot",
  "description": "Suivre le cours de formation"
}
```

**Critères d'acceptation :**
- `titre` est obligatoire (entre 2 et 100 caractères)
- `description` est optionnelle
- Le statut est initialisé automatiquement à `EN_COURS`
- Le titre doit être unique dans la base

**Exceptions levées par le service :**
| Situation | Exception | Code HTTP |
|-----------|-----------|-----------|
| `titre` vide ou absent | `MethodArgumentNotValidException` | `400` |
| `titre` déjà utilisé par une autre tâche | `ResourceAlreadyExistsException` | `409` |

---

### 2. Lister toutes les tâches — `GET /api/todos`

**Critères d'acceptation :**
- Retourne la liste complète de toutes les tâches
- Retourne une liste vide `[]` s'il n'y a aucune tâche (pas d'erreur)

**Exceptions levées par le service :** aucune

---

### 3. Récupérer une tâche par ID — `GET /api/todos/{id}`

**Critères d'acceptation :**
- `{id}` doit être un UUID valide
- Retourne la tâche correspondante si elle existe

**Exceptions levées par le service :**
| Situation | Exception | Code HTTP |
|-----------|-----------|-----------|
| Aucune tâche trouvée avec cet `id` | `ResourceNotFoundException` | `404` |

---

### 4. Modifier une tâche — `PUT /api/todos/{id}`

**Corps de la requête :**
```json
{
  "titre": "Nouveau titre",
  "description": "Nouvelle description"
}
```

**Critères d'acceptation :**
- `{id}` doit correspondre à une tâche existante
- `titre` est obligatoire (entre 2 et 100 caractères)
- Le nouveau titre ne doit pas appartenir à une autre tâche
- Le statut n'est pas modifié par cet endpoint

**Exceptions levées par le service :**
| Situation | Exception | Code HTTP |
|-----------|-----------|-----------|
| `titre` vide ou absent | `MethodArgumentNotValidException` | `400` |
| Aucune tâche trouvée avec cet `id` | `ResourceNotFoundException` | `404` |
| Nouveau titre déjà utilisé par une autre tâche | `ResourceAlreadyExistsException` | `409` |

---

### 5. Changer le statut — `PATCH /api/todos/{id}/status`

**Corps de la requête :**
```json
{
  "statut": "TERMINEE"
}
```

**Critères d'acceptation :**
- `{id}` doit correspondre à une tâche existante
- `statut` est obligatoire : uniquement `EN_COURS` ou `TERMINEE`
- Le nouveau statut doit être **différent** du statut actuel
- Seul le statut est modifié (titre et description restent inchangés)

**Exceptions levées par le service :**
| Situation | Exception | Code HTTP |
|-----------|-----------|-----------|
| `statut` absent dans le corps | `MethodArgumentNotValidException` | `400` |
| Valeur de statut invalide (ex: `"FINI"`) | `HttpMessageNotReadableException` | `400` |
| Aucune tâche trouvée avec cet `id` | `ResourceNotFoundException` | `404` |
| Le statut envoyé est identique au statut actuel | `BadRequestException` | `400` |

---

### 6. Supprimer une tâche — `DELETE /api/todos/{id}`

**Critères d'acceptation :**
- `{id}` doit correspondre à une tâche existante
- La tâche doit avoir le statut `TERMINEE` pour pouvoir être supprimée
- La suppression est définitive

**Exceptions levées par le service :**
| Situation | Exception | Code HTTP |
|-----------|-----------|-----------|
| Aucune tâche trouvée avec cet `id` | `ResourceNotFoundException` | `404` |
| La tâche n'est pas au statut `TERMINEE` | `BadRequestException` | `400` |

---

## Exceptions du service

| Exception | Code HTTP | errorCode | Déclencheur |
|-----------|-----------|-----------|-------------|
| `ResourceNotFoundException` | `404` | `RESOURCE_NOT_FOUND` | ID inconnu dans la base |
| `ResourceAlreadyExistsException` | `409` | `RESOURCE_ALREADY_EXISTS` | Titre déjà utilisé par une autre tâche |
| `BadRequestException` | `400` | `BAD_REQUEST` | Règle métier violée (statut identique, suppression interdite) |
| `MethodArgumentNotValidException` | `400` | `VALIDATION_ERROR` | Champ obligatoire manquant ou invalide |
| `HttpMessageNotReadableException` | `400` | `BAD_REQUEST` | Valeur d'enum invalide dans le corps JSON |

---

## Format des réponses

### Succès
```json
{
  "success": true,
  "status": 200,
  "message": "Opération réussie",
  "data": { ... },
  "timestamp": "2026-03-11T12:00:00"
}
```

### Erreur simple
```json
{
  "success": false,
  "status": 404,
  "message": "Todo non trouvé avec l'identifiant: ...",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2026-03-11T12:00:00"
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
  "timestamp": "2026-03-11T12:00:00"
}
```

---

## Architecture du projet

```
src/main/java/com/TodoList/TodoList_Backend/
├── controller/    → Endpoints HTTP (TodoController)
├── service/       → Logique métier (TodoService / TodoServiceImpl)
├── repository/    → Accès base de données (TodoRepository)
├── entity/        → Entité JPA (Todo) + enum TodoStatus
├── dto/           → Objets de transfert (TodoRequestDto, TodoResponseDto, TodoStatusDto)
├── mapper/        → Conversion entité ↔ DTO (TodoMapper)
├── exception/     → Gestion des erreurs (GlobalExceptionHandler)
└── response/      → Format standardisé des réponses (ApiResponse, ErrorResponse)
```

### Flux d'une requête

```
HTTP Request
     ↓
TodoController     ← reçoit la requête HTTP, délègue au service
     ↓
TodoServiceImpl    ← logique métier + transactions + exceptions
     ↓        ↓
Repository   Mapper
     ↓
Base de données
     ↑
GlobalExceptionHandler  ← intercepte les exceptions, retourne une ErrorResponse JSON
```

---

## Règles métier

- Le titre d'une tâche doit être **unique**
- Le statut par défaut à la création est `EN_COURS`
- Le statut ne peut pas être changé pour la **même valeur** (doit changer)
- Une tâche ne peut être **supprimée** que si son statut est `TERMINEE`

---

## Respect des principes SOLID

| Principe | Application |
|----------|-------------|
| **S** — Single Responsibility | Chaque classe a un seul rôle : Controller (HTTP), Service (métier), Repository (BDD), Mapper (conversion) |
| **O** — Open/Closed | On peut ajouter de nouveaux endpoints ou exceptions sans modifier le code existant |
| **L** — Liskov Substitution | `TodoServiceImpl` est substituable à `TodoService` sans impact sur le Controller |
| **I** — Interface Segregation | DTOs séparés par usage : `TodoRequestDto` (entrée), `TodoResponseDto` (sortie), `TodoStatusDto` (statut) |
| **D** — Dependency Inversion | Le Controller dépend de l'interface `TodoService`, jamais de l'implémentation concrète |

---

## Autrice

**Confectionné par** : Ndeye NDione Tine

**Email** : ndieyendionetine@gmail.com
