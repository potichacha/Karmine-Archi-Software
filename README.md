# Documentation du Projet message-topic-app
## 1. Introduction
### 1.1 Objectif du projet
Le projet message-topic-app est une API REST développée avec Spring Boot.
Son objectif est de permettre la gestion de Topics (sujets de discussion) et de Messages associés à ces Topics.

### 1.2 Fonctionnalités principales
Création, récupération et suppression de Topics (catégories de discussion).
Ajout de Messages à un Topic.
Affichage de la liste des Messages d’un Topic.
Lecture d’un Message et suivi du nombre de lectures.
Blocage de la suppression d’un Message tant qu’il n’a pas été lu.
Suppression d’un Message uniquement s'il n’est plus lié à aucun Topic.
Recherche de Messages par contenu.
Tests de l’API via cURL et accès aux données via H2.
Cette API fonctionne sans interface graphique et s’utilise à l’aide de requêtes HTTP (GET, POST, DELETE).
Elle repose sur Spring Boot, Spring Data JPA, et une base de données H2 en mémoire.

### 1.3 Gestion de la Queue (Message FIFO)
Le projet intègre un système de **queue (file d'attente)** pour chaque Topic. Cela permet :
- De stocker temporairement les messages dans une queue associée à un topic.
- De récupérer facilement le **dernier message ajouté** sans parcourir toute la liste.
- D'assurer une gestion des messages en **mode FIFO (First In, First Out)**.

🚀 **Endpoints liés à la queue :**
| Méthode | Endpoint | Description |
|---------|---------|-------------|
| `GET` | `/topics/{topicId}/last-message` | Récupère le dernier message ajouté dans la queue d’un Topic |

## 2. Architecture du Projet
### 2.1 Structure des fichiers
Le projet suit une architecture classique en Spring Boot, avec plusieurs dossiers ayant chacun un rôle précis.

messageQueues-main/
├── pom.xml
├── Dockerfile                  # ✅ Fichier pour conteneuriser l’application Spring Boot
├── README.md
├── monitoring/                  # 📌 (Dossier contenant uniquement Dockerfile)
│   ├── Dockerfile                # ✅ Image pour le monitoring
├── nginx/                        # 📌 (Dossier contenant Dockerfile + nginx.conf)
│   ├── Dockerfile                # ✅ Image NGINX pour reverse proxy
│   ├── nginx.conf                 # ✅ Configuration du reverse proxy
├── src/
│   ├── main/
│   │   ├── java/demo/
│   │   │   ├── Application.java  # ✅ Point d’entrée Spring Boot
│   │   │   ├── config/           # 📌 (Configuration Spring Boot)
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   ├── controller/       # 📌 (Endpoints REST)
│   │   │   │   ├── MessageController.java
│   │   │   │   ├── TopicController.java
│   │   │   ├── exception/        # 📌 (Gestion des erreurs)
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── model/            # 📌 (Entités JPA)
│   │   │   │   ├── Message.java
│   │   │   │   ├── Topic.java
│   │   │   ├── queue/            # 📌 (Gestion des queues FIFO)
│   │   │   │   ├── InMemoryQueue.java
│   │   │   │   ├── QueueManager.java
│   │   │   ├── repository/       # 📌 (Accès DB avec Spring Data JPA)
│   │   │   │   ├── MessageRepository.java
│   │   │   │   ├── TopicRepository.java
│   │   │   ├── service/          # 📌 (Logique métier)
│   │   │   │   ├── MessageService.java
│   │   │   │   ├── TopicService.java
│   │   │   │   ├── QueueService.java    # ✅ Service pour gérer la queue
│   │   ├── resources/
│   │   │   ├── application.properties  # ✅ Configuration de l’application
│   │   │   ├── log4j2.xml              # ✅ Configuration des logs



### 2.2 Explication des dossiers
controller/ : Gère les requêtes HTTP envoyées par l’utilisateur.
service/ : Contient la logique métier et les traitements des données.
model/ : Définit la structure des données et la gestion des entités en base de données.
repository/ : Contient les interfaces permettant d’accéder à la base de données.
exception/ : Gère les erreurs afin de renvoyer des messages explicites aux utilisateurs.
resources/ : Contient les fichiers de configuration (application.properties, log4j2.xml).
queue/ : Contient la gestion des messages en mode file d’attente (QueueService).

## 3. Explication détaillée des fichiers
Chaque fichier a un rôle spécifique au sein du projet. Cette section détaille les fonctions contenues dans chaque fichier, leur objectif et leur fonctionnement.

### 3.1 Application.java
Emplacement
com.example.Application

Rôle
Ce fichier est le point d’entrée de l’application.
Il démarre Spring Boot et initialise le serveur.

Contenu du fichier
java
Copier
Modifier
@SpringBootApplication
public class Application {
public static void main(String[] args) {
SpringApplication.run(Application.class, args);
}
}
Explication des fonctions
@SpringBootApplication : Indique que ce fichier contient l'application principale de Spring Boot.
SpringApplication.run(Application.class, args); : Démarre l’application et effectue un scan automatique des autres fichiers (@RestController, @Service, @Repository).
Lien avec les autres fichiers
Charge tous les composants du projet et les met en route.
Détecte automatiquement les classes annotées avec @RestController, @Service, et @Repository.
3.2 controller/ : Gestion des requêtes HTTP
Ce dossier contient les contrôleurs qui reçoivent les requêtes HTTP et envoient des réponses JSON.

3.2.1 TopicController.java
Rôle
Gère la création, la récupération et l'ajout de Messages dans un Topic.

Contenu du fichier
@RestController
@RequestMapping("/topics")
public class TopicController {
private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(topicService.createTopic(topic));
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @PostMapping("/{topicId}/messages")
    public ResponseEntity<Topic> addMessageToTopic(@PathVariable Long topicId,
                                                   @RequestBody Message message) {
        Topic updatedTopic = topicService.addMessageToTopic(topicId, message);
        return ResponseEntity.ok(updatedTopic);
    }
}
Explication des fonctions
POST /topics : Crée un Topic et l’enregistre en base de données.
GET /topics : Liste tous les Topics enregistrés.
POST /topics/{topicId}/messages : Ajoute un Message dans un Topic.
Interaction avec les autres fichiers
Appelle TopicService pour exécuter la logique métier.
TopicService appelle TopicRepository pour enregistrer ou récupérer les données.
3.3 service/ : La Logique Métier
Ce dossier contient la logique du projet, c’est-à-dire les traitements effectués avant d’enregistrer ou de récupérer des données.

3.3.1 TopicService.java
Rôle
Gérer la logique métier des Topics.

Contenu du fichier
@Service
public class TopicService {
private final TopicRepository topicRepository;
private final MessageRepository messageRepository;

    public TopicService(TopicRepository topicRepository, MessageRepository messageRepository) {
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
    }

    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic addMessageToTopic(Long topicId, Message message) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic non trouvé"));
        message = messageRepository.save(message);
        topic.getMessages().add(message);
        return topicRepository.save(topic);
    }
}
Explication des fonctions
createTopic(Topic topic) : Enregistre un nouveau Topic en base.
getAllTopics() : Récupère tous les Topics enregistrés en base.
addMessageToTopic(Long topicId, Message message) :
Vérifie si le Topic existe.
Sauvegarde le Message en base.
Ajoute le Message au Topic.
Sauvegarde le Topic mis à jour.

### 3.3.2 QueueService.java
📌 **Rôle**
Ce service gère une file d’attente pour stocker les derniers messages de chaque Topic.

📌 **Fonctionnalités principales**
- Stocker le dernier message d’un Topic.
- Récupérer rapidement le dernier message stocké.
- Fonctionne comme une queue FIFO.

📌 **Exemple d’utilisation**
Message lastMessage = queueService.getLastMessage(topic);

Documentation du Projet message-topic-app – Partie 4 : Explication détaillée des fichiers restants
Cette partie couvre les modèles de données (model/), les repositories (repository/), la gestion des exceptions (exception/), et les fichiers de configuration (resources/).

## 4. Modèles de Données (model/)
Les fichiers du dossier model/ représentent les entités du projet, c'est-à-dire les objets stockés dans la base de données.
Spring Boot utilise Spring Data JPA pour gérer ces entités de manière automatique.

### 4.1 Topic.java
📌 Rôle
Le fichier Topic.java définit la structure des Topics qui contiennent des Messages.

📌 Contenu du fichier
@Entity
public class Topic {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String name;

    @ManyToMany
    private List<Message> messages;

    public Topic() {}

    public Topic(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
📌 Explication des éléments
@Entity → Indique que cette classe est une table en base de données.
@Id @GeneratedValue(strategy = GenerationType.IDENTITY) → Génère un identifiant unique automatiquement.
private String name; → Stocke le nom du Topic.
@ManyToMany → Indique une relation multiple entre les Topics et les Messages.
📌 Lien avec les autres fichiers
Utilisé dans TopicService.java pour créer et récupérer des Topics.
Lié à Message.java pour gérer les Messages associés à un Topic.

### 4.2 Message.java
📌 Rôle
Le fichier Message.java définit la structure des Messages qui sont liés à un ou plusieurs Topics.

📌 Contenu du fichier
java
Copier
Modifier
@Entity
public class Message {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String content;
    private int numberOfReads;
    
    @ManyToMany(mappedBy = "messages")
    private List<Topic> topics;

    public Message() {}

    public Message(String content) {
        this.content = content;
        this.numberOfReads = 0;
        this.topics = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getNumberOfReads() { return numberOfReads; }
    public void setNumberOfReads(int numberOfReads) { this.numberOfReads = numberOfReads; }
    public List<Topic> getTopics() { return topics; }
    public void setTopics(List<Topic> topics) { this.topics = topics; }
}
📌 Explication des éléments
@Entity → Déclare que Message est une table en base de données.
@Id @GeneratedValue(strategy = GenerationType.IDENTITY) → Génère un identifiant unique.
private String content; → Stocke le contenu du Message.
private int numberOfReads; → Compteur de nombre de lectures.
@ManyToMany(mappedBy = "messages") → Indique que plusieurs Messages peuvent appartenir à plusieurs Topics.
📌 Lien avec les autres fichiers
Utilisé dans MessageService.java pour gérer la lecture et la suppression des Messages.
Lié à Topic.java pour référencer les Topics contenant ce Message.

## 5. Gestion de la Base de Données (repository/)
Les fichiers du dossier repository/ permettent d'accéder aux données stockées en base via Spring Data JPA.

### 5.1 TopicRepository.java
📌 Rôle
Interface permettant de gérer les Topics en base de données.

📌 Contenu du fichier
java
Copier
Modifier
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
}
📌 Explication
@Repository → Indique que ce fichier est un repository.
extends JpaRepository<Topic, Long> → Indique que cette interface permet :
De récupérer, enregistrer et supprimer des Topics.
De retrouver un Topic à partir de son id (type Long).
📌 Lien avec les autres fichiers
Utilisé dans TopicService.java pour ajouter et récupérer des Topics.
### 5.2 MessageRepository.java
📌 Rôle
Interface permettant de gérer les Messages en base de données.

📌 Contenu du fichier
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
📌 Explication
@Repository → Indique que ce fichier est un repository.
extends JpaRepository<Message, Long> → Indique que cette interface permet :
De récupérer, enregistrer et supprimer des Messages.
De retrouver un Message à partir de son id (type Long).
📌 Lien avec les autres fichiers
Utilisé dans MessageService.java pour ajouter et récupérer des Messages.

### 5.3 Tester la File d’Attente des Messages

**1️⃣ Ajouter un Message à un Topic**
curl.exe -X POST http://localhost:8080/topics/1/messages -H "Content-Type: application/json" -d "{\"content\":\"Message dans la queue\"}"
Résultat attendu :
{"id":1,"name":"Mon Topic Test","messages":[{"id":2,"content":"Message dans la queue","numberOfReads":0}]}
**🚀 2️⃣ Récupérer le Dernier Message Stocké**
curl.exe -X GET http://localhost:8080/topics/1/last-message
{"id":2,"content":"Message dans la queue","numberOfReads":0}

## 6. Gestion des Erreurs (exception/)
Le fichier GlobalExceptionHandler.java permet de gérer les erreurs et d’envoyer des messages clairs à l’utilisateur.

📌 Contenu du fichier
java
Copier
Modifier
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
📌 Explication
@RestControllerAdvice → Intercepte toutes les exceptions dans l’API.
@ExceptionHandler(RuntimeException.class) → Capture les erreurs de type RuntimeException.
ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
→ Renvoie un message clair en HTTP 400 (Bad Request).

## 7. Fichiers de Configuration (resources/)
Les fichiers de configuration permettent de gérer la base de données et les logs.

### 7.1 application.properties
Ce fichier configure la base de données H2.

properties
Copier
Modifier
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
📌 Explication
spring.datasource.url=jdbc:h2:mem:testdb → Utilise une base de données H2 en mémoire.
spring.h2.console.enabled=true → Active la console H2 pour voir la base via un navigateur.
📌 Lien avec les autres fichiers
Permet à repository/ d’accéder à la base H2.

Documentation du Projet message-topic-app – Partie 5 : Commandes et Tests de l’API
Cette dernière partie couvre toutes les commandes utilisées pour tester l’API, interagir avec la base de données et vérifier son bon fonctionnement.

1. Vérification du Bon Fonctionnement de l’API
   Avant de commencer les tests, il est important de vérifier que l’API est bien en cours d’exécution.

1.1 Vérifier que l’API est démarrée
💻 Commande :
curl.exe -X GET http://localhost:8080/actuator/health
📌 Pourquoi ?

Cette commande permet de vérifier que l’application Spring Boot fonctionne correctement.
Elle interroge l’endpoint /actuator/health, fourni par Spring Boot Actuator.
📌 Résultat attendu :

json
Copier
Modifier
{"status":"UP"}
Si la réponse indique "status":"UP", cela signifie que l’API fonctionne normalement.
Si l’API est hors service, il faudra vérifier si elle a bien été démarrée avec :
mvn spring-boot:run
2. Commandes pour Gérer les Topics
   Un Topic est une catégorie qui contient des Messages.

2.1 Créer un Topic
💻 Commande :
curl.exe -X POST http://localhost:8080/topics -H "Content-Type: application/json" -d "{\"name\":\"Mon Topic Test\"}"
📌 Pourquoi ?

Cette commande crée un nouveau Topic nommé "Mon Topic Test".
📌 Explication des options utilisées :

-X POST → Envoie une requête HTTP POST pour créer une nouvelle ressource.
-H "Content-Type: application/json" → Indique que les données envoyées sont au format JSON.
-d "{\"name\":\"Mon Topic Test\"}" → Envoie un objet JSON avec la clé "name".
📌 Résultat attendu :

{"id":1,"name":"Mon Topic Test","messages":[]}
Cela signifie que le Topic a bien été créé en base de données.

2.2 Récupérer la Liste des Topics
💻 Commande :
curl.exe -X GET http://localhost:8080/topics
📌 Pourquoi ?

Permet de récupérer tous les Topics enregistrés.
📌 Explication :

-X GET → Effectue une requête GET pour récupérer les données existantes.
📌 Résultat attendu :

json
Copier
Modifier
[
{
"id": 1,
"name": "Mon Topic Test",
"messages": []
}
]
3. Commandes pour Gérer les Messages
   Un Message appartient à un ou plusieurs Topics.

3.1 Ajouter un Message à un Topic
💻 Commande :

curl.exe -X POST http://localhost:8080/topics/1/messages -H "Content-Type: application/json" -d "{\"content\":\"Premier message\"}"
📌 Pourquoi ?

Permet d’ajouter un Message au Topic 1.
📌 Explication :

-X POST → Envoie une requête POST pour créer une ressource.
"{\"content\":\"Premier message\"}" → Envoie un objet JSON avec le contenu du Message.
📌 Résultat attendu :

json
Copier
Modifier
{"id":1,"name":"Mon Topic Test","messages":[{"id":1,"content":"Premier message","numberOfReads":0}]}
Le Message 1 a bien été ajouté au Topic 1.

3.2 Récupérer les Messages d’un Topic
💻 Commande :
curl.exe -X GET http://localhost:8080/topics/1/messages
📌 Pourquoi ?

Permet de voir tous les Messages du Topic 1.
📌 Résultat attendu :

json
Copier
Modifier
[
{
"id": 1,
"content": "Premier message",
"numberOfReads": 0
}
]
3.3 Lire un Message
💻 Commande :

curl.exe -X GET http://localhost:8080/messages/1
📌 Pourquoi ?

Cette commande récupère un Message et incrémente son compteur de lecture.
📌 Résultat attendu après plusieurs lectures :

json
Copier
Modifier
{
"id": 1,
"content": "Premier message",
"numberOfReads": 3
}
Le nombre de lectures (numberOfReads) augmente à chaque requête.

4. Commandes pour Supprimer des Messages
   4.1 Essayer de Supprimer un Message Non Lu (Échec)
   💻 Commande :
   curl.exe -X DELETE http://localhost:8080/messages/1
   📌 Pourquoi ?

Un Message ne peut pas être supprimé tant qu’il n’a pas été lu.
📌 Résultat attendu :

json
Copier
Modifier
{
"error": "Impossible de supprimer un message non lu."
}
4.2 Lire puis Supprimer un Message
💻 1️⃣ Lire le Message
curl.exe -X GET http://localhost:8080/messages/1
💻 2️⃣ Supprimer le Message
curl.exe -X DELETE http://localhost:8080/messages/1
📌 Résultat attendu :

json
Copier
Modifier
"Message supprimé !"
Le Message a bien été supprimé car il avait été lu.

4.3 Supprimer un Message d’un Topic
💻 Commande :-b   
curl.exe -X DELETE http://localhost:8080/topics/1/messages/1
📌 Pourquoi ?

Cette commande retire un Message d’un Topic.
📌 Résultat attendu :

Si le Message n’est plus dans aucun Topic, il est supprimé.
5. Commandes pour Tester avec la Base de Données
   Le projet utilise H2, une base de données en mémoire, ce qui signifie que les données sont perdues après l'arrêt du serveur.

5.1 Ouvrir la Console H2
Ouvrir un navigateur et aller à :
bash
Copier
Modifier
http://localhost:8080/h2-console
Remplir les champs :
JDBC URL : jdbc:h2:mem:testdb
User : sa
Password : (laisser vide)
Cliquer sur "Connect".
5.2 Voir les Topics en Base
💻 Commande SQL :
SELECT * FROM topics;
📌 Pourquoi ?

Affiche tous les Topics enregistrés dans la base de données.
5.3 Voir les Messages en Base
💻 Commande SQL :
SELECT * FROM messages;
6. Conclusion
   Cette partie couvre l’ensemble des commandes et tests permettant de vérifier le bon fonctionnement de l’API.
   Elle comprend :

La vérification du serveur
La gestion des Topics et des Messages
Les commandes de suppression
L’accès aux données via la console H2

## 8. Docker
Nous avons créer un Dockfile pour créer un container. \
On utilise la commande suivante commande :
+ docker build -t my-app . => créer un conteneur

On va ensuite sur Docker Desktop > image > my-app > run pour lancer le container.
Nous avons également utilisé NGINX pour utiliser nos services via Docker, en créant un nginx.conf ainsi qu'un Dockerfile pour utiliser NGINX. 