# API Gateway - Guide Complet

## 📋 Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Installation et Configuration](#installation-et-configuration)
4. [Utilisation avec Postman](#utilisation-avec-postman)
5. [Déploiement Kubernetes](#déploiement-kubernetes)
6. [Tests et Vérification](#tests-et-vérification)

---

## 🎯 Vue d'ensemble

L'API Gateway est le **point d'entrée unique** pour tous les microservices de l'application Hospital. Elle centralise l'accès et simplifie la communication avec les services backend.

### Pourquoi une API Gateway ?

✅ **Point d'entrée unique** : Un seul port (8080) au lieu de 4 ports différents  
✅ **Routage intelligent** : Redirection automatique vers le bon service  
✅ **Résilience** : Circuit breaker pour gérer les pannes  
✅ **CORS** : Gestion centralisée pour les applications web  
✅ **Monitoring** : Endpoints Actuator pour la santé des services  

---

## 🏗️ Architecture

### Flux des Requêtes HTTP

```
Client (Postman/Browser)
        ↓
[API Gateway :8080]
        ↓
    ┌───┴────────────────┬──────────────────┬─────────────────┐
    ↓                    ↓                  ↓                 ↓
Patient Service   Appointment Service  Billing Service  Notification Service
   :8082                :8081              :3003             :3004
```

### Table de Routage

| Path                      | Service Cible          | Port |
|---------------------------|------------------------|------|
| `/api/patients/**`        | Patient Service        | 8082 |
| `/api/appointments/**`    | Appointment Service    | 8081 |
| `/api/bills/**`           | Billing Service        | 3003 |
| `/api/notifications/**`   | Notification Service   | 3004 |

**Exemple concret :**
- Requête : `http://localhost:8080/api/patients/1`
- Gateway redirige vers : `http://localhost:8082/api/patients/1`

---

## 🚀 Installation et Configuration

### Option 1 : Exécution en Développement (Recommandé pour débuter)

#### Étape 1 : Démarrer l'infrastructure
```powershell
# À la racine du projet
docker-compose up -d mysql mongodb rabbitmq
```

#### Étape 2 : Compiler l'API Gateway
```powershell
cd api-gateway
./mvnw clean package -DskipTests
```

#### Étape 3 : Démarrer les microservices (5 terminaux)

**Terminal 1 - API Gateway:**
```powershell
cd api-gateway
./mvnw spring-boot:run
# Écoute sur http://localhost:8080
```

**Terminal 2 - Patient Service:**
```powershell
cd patient-service
./mvnw spring-boot:run
# Écoute sur http://localhost:8082
```

**Terminal 3 - Appointment Service:**
```powershell
cd appointment-service
./mvnw spring-boot:run
# Écoute sur http://localhost:8081
```

**Terminal 4 - Billing Service:**
```powershell
cd HOSPITAL/billing-service
../../mvnw spring-boot:run
# Écoute sur http://localhost:3003
```

**Terminal 5 - Notification Service:**
```powershell
cd HOSPITAL/notification-service
../../mvnw spring-boot:run
# Écoute sur http://localhost:3004
```

#### Vérification
```powershell
# Vérifier que l'API Gateway est démarrée
curl http://localhost:8080/actuator/health
```

---

### Option 2 : Exécution avec Docker Compose (Tout-en-un)

```powershell
# Démarrer toute l'infrastructure + API Gateway
docker-compose up -d

# Les microservices doivent être lancés manuellement (voir Option 1)
```

---

## 📮 Utilisation avec Postman

### Configuration Initiale

**Base URL:** `http://localhost:8080`

### Exemples de Requêtes

#### 1️⃣ **Créer un Patient**

```http
POST http://localhost:8080/api/patients
Content-Type: application/json

{
  "name": "Jean Dupont",
  "email": "jean.dupont@email.com",
  "phone": "0612345678",
  "address": "10 Rue de la Santé, Paris"
}
```

**Avant (sans Gateway):**  
`POST http://localhost:8082/api/patients`

**Maintenant (avec Gateway):**  
`POST http://localhost:8080/api/patients` ✅

---

#### 2️⃣ **Créer un Rendez-vous**

```http
POST http://localhost:8080/api/appointments
Content-Type: application/json

{
  "patientName": "Jean Dupont",
  "doctorName": "Dr. Martin",
  "appointmentDate": "2025-12-30T10:00:00",
  "reason": "Consultation générale"
}
```

**Résultat:**
- Gateway route vers `http://localhost:8081/api/appointments`
- Appointment Service publie un événement RabbitMQ
- Billing Service crée une facture automatiquement
- Notification Service crée une notification

---

#### 3️⃣ **Consulter les Factures**

```http
GET http://localhost:8080/api/bills
```

Gateway route vers `http://localhost:3003/api/bills`

---

#### 4️⃣ **Consulter les Notifications**

```http
GET http://localhost:8080/api/notifications
```

Gateway route vers `http://localhost:3004/api/notifications`

---

### Collection Postman

Vous pouvez créer une collection Postman avec ces variables :

**Variables d'environnement:**
```json
{
  "gateway_url": "http://localhost:8080",
  "patient_service": "http://localhost:8082",
  "appointment_service": "http://localhost:8081",
  "billing_service": "http://localhost:3003",
  "notification_service": "http://localhost:3004"
}
```

Utilisation : `{{gateway_url}}/api/patients`

---

## ☸️ Déploiement Kubernetes

### Prérequis
- Kubernetes cluster (Minikube, Docker Desktop, ou cloud)
- kubectl installé

### Déploiement de l'API Gateway

#### 1. Construire l'image Docker
```powershell
cd api-gateway
docker build -t api-gateway:latest .
```

#### 2. Déployer sur Kubernetes
```powershell
# Déployer l'API Gateway
kubectl apply -f k8s/api-gateway-deployment.yaml
kubectl apply -f k8s/api-gateway-service.yaml

# Vérifier le déploiement
kubectl get pods -l app=api-gateway
kubectl get svc api-gateway
```

#### 3. Accéder à l'API Gateway

```powershell
# Obtenir l'URL du service
kubectl get svc api-gateway

# Port-forward pour tester localement
kubectl port-forward svc/api-gateway 8080:8080
```

### Configuration Kubernetes

L'API Gateway utilise le DNS Kubernetes pour communiquer avec les services:
- `http://patient-service:8082`
- `http://appointment-service:8081`
- `http://billing-service:3003`
- `http://notification-service:3004`

---

## 🧪 Tests et Vérification

### 1. Vérifier la Santé de l'API Gateway

```powershell
# Health check général
curl http://localhost:8080/actuator/health

# Voir toutes les routes configurées
curl http://localhost:8080/actuator/gateway/routes
```

### 2. Tester le Routage

```powershell
# Test Patient Service via Gateway
curl http://localhost:8080/api/patients

# Test Appointment Service via Gateway
curl http://localhost:8080/api/appointments

# Test Billing Service via Gateway
curl http://localhost:8080/api/bills

# Test Notification Service via Gateway
curl http://localhost:8080/api/notifications
```

### 3. Tester le Circuit Breaker

1. Arrêter un service (ex: Patient Service)
2. Faire une requête via la Gateway
3. Recevoir une réponse de fallback au lieu d'une erreur 500

```powershell
# Arrêter Patient Service
# Tester
curl http://localhost:8080/api/patients/1

# Réponse attendue (fallback):
{
  "error": "Patient Service is currently unavailable",
  "message": "Please try again later",
  "timestamp": "2025-12-28T...",
  "status": 503
}
```

---

## 📊 Avantages de la Gateway

### Pour les Développeurs
- ✅ **Un seul port à retenir** : 8080
- ✅ **Configuration CORS centralisée**
- ✅ **Logs centralisés du trafic**

### Pour les Clients (Frontend/Postman)
- ✅ **URL simplifiée** : Toujours `localhost:8080`
- ✅ **Pas besoin de connaître les ports de chaque service**
- ✅ **Résilience** : Pas de crash si un service est down

### Pour la Production
- ✅ **Load Balancing** : Distribution automatique
- ✅ **Monitoring centralisé** : Via Actuator
- ✅ **Évolutivité** : Ajouter des services sans changer les clients

---

## 🔧 Dépannage

### L'API Gateway ne démarre pas
```powershell
# Vérifier les logs
./mvnw spring-boot:run

# Vérifier que le port 8080 est libre
netstat -ano | findstr :8080
```

### Une route ne fonctionne pas
```powershell
# Vérifier la configuration des routes
curl http://localhost:8080/actuator/gateway/routes

# Vérifier que le service cible est accessible
curl http://localhost:8082/actuator/health  # Exemple pour Patient Service
```

### Erreur 503 Service Unavailable
- Le service backend n'est pas démarré
- Vérifier que tous les microservices sont en cours d'exécution

---

## 📚 Ressources Supplémentaires

- [Spring Cloud Gateway Docs](https://spring.io/projects/spring-cloud-gateway)
- [Circuit Breaker Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)
- [API Gateway Pattern](https://microservices.io/patterns/apigateway.html)

---

## 🎓 Résumé pour Projet Académique

**Concept clé:** L'API Gateway est comme une **réceptionniste d'hôpital** :
- Tous les visiteurs (requêtes) passent par elle
- Elle sait où diriger chaque personne (routing)
- Elle gère les problèmes si un département est fermé (circuit breaker)
- Elle simplifie l'accès pour tout le monde (point d'entrée unique)

**Technologies utilisées:**
- **Spring Cloud Gateway** : Framework pour API Gateway
- **Spring WebFlux** : Programmation réactive (non-bloquante)
- **Resilience4j** : Circuit Breaker pour la résilience
- **Spring Actuator** : Monitoring et health checks
