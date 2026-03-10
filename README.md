# TodoList Backend — API REST Spring Boot

Projet de rattrapage — Formation Continue UFC / UNCHK
Mini-projet : Gestion de Tâches (To-Do List)

---

## Description

API REST permettant de gérer une liste de tâches. Chaque tâche possède :

| Champ         | Type              | Description                      |
|---------------|-------------------|----------------------------------|
| `id`          | UUID              | Identifiant unique (généré auto) |
| `titre`       | String            | Titre de la tâche (obligatoire)  |
| `description` | String            | Description (optionnelle)        |
| `statut`      | `EN_COURS` / `TERMINEE` | Statut de la tâche         |

---

## Technologies utilisées

| Technologie             | Version         |
|-------------------------|-----------------|
| Java                    | 17              |
| Spring Boot             | 3.x             |
| Spring Data JPA         | (inclus)        |
| PostgreSQL / MySQL      | 8+              |
| Lombok                  | (inclus)        |
| Springdoc OpenAPI       | 2.x             |
| Maven                   | Wrapper inclus  |

---

## Installation & Lancement

**1. Configurer la base de données**

Dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/todolist_db
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
```

**2. Lancer l'application**

```bash
./mvnw spring-boot:run
```

**3. Accéder à l'API**

- API : `http://localhost:8080/api/todos`
- Swagger UI : `http://localhost:8080/swagger-ui.html`

---

## Endpoints disponibles

| Méthode | URL                      | Description                   |
|---------|--------------------------|-------------------------------|
| POST    | `/api/todos`             | Ajouter une tâche             |
| GET     | `/api/todos`             | Lister toutes les tâches      |
| GET     | `/api/todos/{id}`        | Récupérer une tâche par ID    |
| PUT     | `/api/todos/{id}`        | Modifier une tâche            |
| PATCH   | `/api/todos/{id}/status` | Marquer une tâche comme terminée |
| DELETE  | `/api/todos/{id}`        | Supprimer une tâche           |

---

## Exemples de requêtes

### Créer une tâche
```json
POST /api/todos
{
  "titre": "Apprendre Spring Boot",
  "description": "Suivre le cours de formation"
}
```

### Modifier une tâche
```json
PUT /api/todos/{id}
{
  "titre": "Nouveau titre",
  "description": "Nouvelle description"
}
```

### Marquer une tâche comme terminée
```json
PATCH /api/todos/{id}/status
{
  "statut": "TERMINEE"
}
```

---

## Format des réponses

### Succès
```json
{
  "success": true,
  "status": 200,
  "message": "Opération réussie",
  "data": { ... },
  "timestamp": "2026-03-10T12:00:00"
}
```

### Erreur
```json
{
  "success": false,
  "status": 404,
  "message": "Todo non trouvé avec l'identifiant: ...",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2026-03-10T12:00:00"
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
TodoServiceImpl    ← logique métier + transactions
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
