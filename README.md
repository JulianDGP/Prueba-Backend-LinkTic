# 📦 Inventory & Products Microservices

Este repositorio contiene dos microservicios:

1. **Products**: catálogo de productos (CRUD)  
2. **Inventory**: gestión de inventario y compras contra ese catálogo.

Ambos están implementados en Spring Boot 3.4, se comunican vía HTTP en el estandar JSON-API y persisten en PostgreSQL.

---

## 📑 Índice

- [1.Requisitos](#-requisitos)  
- [2.Configuración](#-configuración)  
- [3.Iniciar con Docker Compose](#-iniciar-con-docker-compose)  
- [4.Endpoints principales](#-endpoints-principales)  
- [5.Testing & Cobertura](#-testing--cobertura)  

---

## 🔧 Requisitos

Antes de comenzar, asegúrate de tener instalado:

- **Git** 
- **Docker** (versión ≥ 20.10)  
- **Docker Compose** (versión ≥ 1.29)  
- Opcional (local): cliente `psql` o cualquier GUI de PostgreSQL  

---

## ⚙️ Configuración

1. **Clona el repositorio**  
   ```bash
   git clone https://github.com/JulianDGP/Prueba-Backend-LinkTic.git
   ```
2. **Asegurate de estar en la carpeta correcta del repositorio**  
    ```bash
   cd Prueba-Backend-LinkTic
   ```
3. **Variables de entorno**
   
   Crea un archivo .env o renombra el archivo existente .env-example a '.env' modificando principalmente los valores de DB_USER y DB_PASS respecto a los valores de tu postgres

## 🚀 Iniciar con Docker Compose
En la raíz del repositorio, ejecuta:
```bash
docker-compose up --build
 ```
Levantara un contenedor PostgreSQL

Ejecutará el script de init.sql

Construirá y arrancará el microservicio Products

Construirá y arrancará el microservicio Inventory

Al terminar:

PostgreSQL 📦 en localhost:5435

Products ▶️ http://localhost:8080

Inventory ▶️ http://localhost:8081

## 🧪 Testing y Cobertura

Se consigue un 100% de cobertura en servicios/casos de uso y endpoints, y un total de 89% en todo el microsericio de inventario

<img width="1842" height="562" alt="Cobertura" src="https://github.com/user-attachments/assets/5ff86892-3ab8-48bd-84ed-517cced9f2fc" />

