# API Gateway - Résumé Exécutif

## ✅ API Gateway Créée avec Succès !

### 📁 Structure du Projet

```
api-gateway/
├── src/main/java/com/hospital/gateway/
│   ├── ApiGatewayApplication.java          # Point d'entrée
│   ├── config/
│   │   └── CorsConfig.java                 # Configuration CORS
│   └── controller/
│       └── FallbackController.java         # Gestion des erreurs
├── src/main/resources/
│   ├── application.yml                     # Config locale
│   └── application-k8s.yml                 # Config Kubernetes
├── k8s/
│   ├── api-gateway-deployment.yaml         # Déploiement K8s
│   └── api-gateway-service.yaml            # Service K8s
├── Dockerfile                              # Image Docker
├── pom.xml                                 # Dépendances Maven
└── API-GATEWAY-GUIDE.md                    # Documentation complète
```

---

## 🚀 Commandes d'Exécution

### 1️⃣ Démarrer l'Infrastructure
```powershell
docker-compose up -d mysql mongodb rabbitmq
```

### 2️⃣ Compiler l'API Gateway
```powershell
cd api-gateway
./mvnw clean package -DskipTests
```

### 3️⃣ Démarrer l'API Gateway
```powershell
./mvnw spring-boot:run
```

L'API Gateway démarre sur **http://localhost:8080**

### 4️⃣ Démarrer les Microservices (autres terminaux)
```powershell
# Patient Service (Terminal 2)
cd patient-service
./mvnw spring-boot:run

# Appointment Service (Terminal 3)
cd appointment-service
./mvnw spring-boot:run

# Billing Service (Terminal 4)
cd HOSPITAL/billing-service
../../mvnw spring-boot:run

# Notification Service (Terminal 5)
cd HOSPITAL/notification-service
../../mvnw spring-boot:run
```

---

## 🔀 Table de Routage

| Requête Client                              | Gateway Route Vers           |
|---------------------------------------------|------------------------------|
| `http://localhost:8080/api/patients/**`     | `http://localhost:8082`      |
| `http://localhost:8080/api/appointments/**` | `http://localhost:8081`      |
| `http://localhost:8080/api/bills/**`        | `http://localhost:3003`      |
| `http://localhost:8080/api/notifications/**`| `http://localhost:3004`      |

---

## 🧪 Tests Rapides

### Vérifier la Gateway
```powershell
curl http://localhost:8080/actuator/health
```

### Créer un Patient via Gateway
```powershell
curl -X POST http://localhost:8080/api/patients `
  -H "Content-Type: application/json" `
  -d '{
    "name": "Jean Dupont",
    "email": "jean@email.com",
    "phone": "0612345678"
  }'
```

### Créer un Rendez-vous via Gateway
```powershell
curl -X POST http://localhost:8080/api/appointments `
  -H "Content-Type: application/json" `
  -d '{
    "patientName": "Jean Dupont",
    "doctorName": "Dr. Martin",
    "appointmentDate": "2025-12-30T10:00:00",
    "reason": "Consultation"
  }'
```

---

## 📮 Utilisation avec Postman

**Importer la collection:**  
[api-gateway/Hospital-API-Gateway.postman_collection.json](api-gateway/Hospital-API-Gateway.postman_collection.json)

**Base URL:** `http://localhost:8080`

**Exemples de requêtes:**
- GET `{{gateway_url}}/api/patients`
- POST `{{gateway_url}}/api/appointments`
- GET `{{gateway_url}}/api/bills`
- GET `{{gateway_url}}/api/notifications`

---

## ☸️ Déploiement Kubernetes

### Build l'image
```powershell
cd api-gateway
docker build -t api-gateway:latest .
```

### Déployer
```powershell
kubectl apply -f k8s/api-gateway-deployment.yaml
kubectl apply -f k8s/api-gateway-service.yaml
```

### Accéder
```powershell
kubectl port-forward svc/api-gateway 8080:8080
```

---

## 📚 Comment ça Marche ?

### Flux d'une Requête HTTP

```
1. Client (Postman) envoie :
   POST http://localhost:8080/api/appointments

2. API Gateway reçoit la requête sur le port 8080

3. Gateway analyse le path : /api/appointments/**

4. Gateway route vers : http://localhost:8081/api/appointments
   (Appointment Service)

5. Appointment Service traite et répond

6. Gateway renvoie la réponse au client

7. En parallèle (via RabbitMQ) :
   - Billing Service crée une facture
   - Notification Service crée une notification
```

### Avantages

✅ **Un seul port** : Plus besoin de retenir 4 ports différents  
✅ **Routage automatique** : La Gateway sait où envoyer chaque requête  
✅ **Circuit Breaker** : Si un service est down, réponse de fallback au lieu d'erreur  
✅ **CORS** : Configuration centralisée pour les apps web  
✅ **Production-ready** : Compatible Kubernetes, Docker, monitoring inclus  

---

## 🎓 Technologies Utilisées dans l'API Gateway

| Technologie              | Rôle                                    |
|--------------------------|-----------------------------------------|
| **Spring Cloud Gateway** | Framework API Gateway                   |
| **Spring WebFlux**       | Programmation réactive (non-bloquant)   |
| **Resilience4j**         | Circuit Breaker pour la résilience      |
| **Spring Actuator**      | Health checks et monitoring             |
| **Docker**               | Containerisation                        |
| **Kubernetes**           | Orchestration de conteneurs             |

---

## 📖 Documentation Complète

Pour plus de détails, consultez :
- [API-GATEWAY-GUIDE.md](api-gateway/API-GATEWAY-GUIDE.md) - Documentation complète
- [Hospital-API-Gateway.postman_collection.json](api-gateway/Hospital-API-Gateway.postman_collection.json) - Collection Postman

---

## 🆚 Avant vs Après

### AVANT (Sans Gateway)
```
Client doit connaître 4 URLs différentes :
- http://localhost:8081/api/appointments
- http://localhost:8082/api/patients
- http://localhost:3003/api/bills
- http://localhost:3004/api/notifications
```

### APRÈS (Avec Gateway) ✅
```
Client utilise une seule URL :
- http://localhost:8080/api/appointments
- http://localhost:8080/api/patients
- http://localhost:8080/api/bills
- http://localhost:8080/api/notifications
```

**Plus simple, plus professionnel, production-ready !** 🎉
