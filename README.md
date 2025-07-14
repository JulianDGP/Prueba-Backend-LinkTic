# 📦 Inventory & Products Microservices

Este **mono-repo** alberga dos microservicios en Spring Boot 3.4 que se exponen por completo en el estandar JSON-API y persisten en PostgreSQL:

1. **Products**: catálogo de productos (crear, listar, obtener por ID).  
2. **Inventory**: gestión de stock y registro de compras contra el catálogo.

Ambos están implementados en Spring Boot 3.4, se comunican vía HTTP en el estandar JSON-API, persisten en PostgreSQL y puedes iniciarlos atraves de Docker Compose.

---

## 📑 Índice
1. [🛠️ Requisitos](#1-requisitos)  
2. [📂 Estructura general del proyecto](#2-estructura-general-del-proyecto)  
3. [⚙️ Configuración del entorno](#3-configuración-de-entorno)
4. [🚀 Primer arranque con Docker Compose](#4-primer-arranque-con-docker-compose)
5. [🏛️ Diagrama de arquitectura](#5-diagrama-de-arquitectura)
6. [📋 Endpoints principales](#6-endpoints-principales)
7. [💡 Justificación y decisiones técnicas](#7-justificacion-y-decisiones-tecnicas)
8. [🧪 Testing y Cobertura](#8-testing-y-cobertura)
9. [💎 Buenas practicas](#9-buenas-practicas)
10. [🤖 Uso de IA](#10-uso-de-ia)
11. [🚧 Propuestas de mejora](#11-propuestas-de-mejora)


---

<a id="1-requisitos"></a>
## 1. 🛠️ Requisitos
Antes de comenzar, asegúrate de tener instalado:

- **Git** 
- **Docker** (versión ≥ 20.10)  
- **Docker Compose** (versión ≥ 1.29)  
- Opcional (local): cliente `psql` o cualquier GUI de PostgreSQL  

---
<a id="2-estructura-general-del-proyecto"></a>
## 2. 📂 Estructura general del proyecto
```
/
├─ .env ← credenciales y URLs
├─ init.sql ← script de creación y semilla de datos de prueba
├─ docker-compose.yml ← orquesta contenedores
│
├─ products/ ← microservicio Products
│ ├─ src/ ← código fuente
│ └─ Dockerfile ← build & runtime
│
└─ inventory/ ← microservicio Inventory
├─ src/  ← código fuente
└─ Dockerfile ← build & runtime
```
<a id="3-configuración-de-entorno"></a>
## 3. ⚙️ Configuración del entorno

1. **Clona el repositorio**  
   ```bash
   git clone https://github.com/JulianDGP/Prueba-Backend-LinkTic.git
   ```
2. **Asegurate de estar en la carpeta raiz correcta del repositorio**  
    ```bash
   cd Prueba-Backend-LinkTic
   ```
3. **Variables de entorno**
   
   Crea un archivo .env basandote en el formato de .env-example o renombra este mismo a '.env' modificando principalmente los valores de DB_USER y DB_PASS respecto a tus credenciales de postgres

<a id="4-primer-arranque-con-docker-compose"></a>
## 4. 🚀 Primer arranque con Docker Compose
En la raíz del repositorio, ejecuta:
```bash
docker-compose up --build
 ```
Esto hace:

1. Levantar PostgreSQL y aplicar init.sql (schema sales + datos).

2. Construir y arrancar Products (puerto 8080).

3. Construir y arrancar Inventory (puerto 8081).

Cuando termine, dispondrás de:

PostgreSQL 📦 en localhost:5435

Products ▶️ http://localhost:8080

Inventory ▶️ http://localhost:8081


<a id="5-diagrama-de-arquitectura"></a>
## 5. 🏛️ Diagrama de arquitectura
### 5.1 Diagrama arquitectura de microservicios 
<img width="670" height="427" alt="image" src="https://github.com/user-attachments/assets/bfb3dd52-83fb-4545-b317-af378e585c1d" />

### 5.2 Diagrama de esquema relacional

<img width="407" height="378" alt="image" src="https://github.com/user-attachments/assets/8ba86d1c-2dc1-404b-9a0d-da9f57a1b2ff" />

<a id="6-endpoints-principales"></a>
## 6. Endpoints principales y flujo de interaccion entre servicios
### 6.1 Flujo de compra

<img width="910" height="480" alt="image" src="https://github.com/user-attachments/assets/d59510fa-e5c6-440e-b74b-9206d6594b65" />

### 6.1.1 Peticion en Swagger

<img width="1252" height="614" alt="image" src="https://github.com/user-attachments/assets/5d746db2-3f6a-47cf-9bb8-5b0262db6769" />

### 6.2 Consultar Inventario

<img width="842" height="507" alt="image" src="https://github.com/user-attachments/assets/3292ca77-fa4a-4102-8037-ca4bdf75c811" />

### 6.2.1 Peticion en Swagger

<img width="1254" height="594" alt="image" src="https://github.com/user-attachments/assets/914de2c6-2172-465e-bb38-5cc5f1ed5258" />

## 6.3 Otras peticiones documentadas en Swagger

<img width="1292" height="403" alt="image" src="https://github.com/user-attachments/assets/c3cc5c5c-0cd2-4ced-9b29-34e07e0eed4c" />

<img width="1296" height="549" alt="image" src="https://github.com/user-attachments/assets/ec9fba30-88a9-476a-a525-eb7340ff2fcf" />


<a id="7-justificacion-y-decisiones-tecnicas"></a>
## 7. Justificacion y decisiones tecnicas

### 7.1 ¿Porque PostgreSQL y no otra?

ACID y consistencia: PostgreSQL garantiza transacciones atómicas, consistentes, aisladas y durables, imprescindible para operaciones de inventario y compras, una de las mejores opciones para soluciones de datos estrcuturados.

### 7.2 ¿Por qué implementar el endpoint de compra en el microservicio de Inventory?
* **Responsabilidad única**: El inventario es quien posee el estado de stock; es responsable de descontar y registrar transacciones.

* **Consistencia transaccional**: Al agrupar la presistencia del registro de compra + ajuste de inventario en un mismo bounded context, se reduce riesgo de datos incongruentes al hacerse en una misma transacion.

* **Acoplamiento mínimo**: El servicio Products sólo aporta datos de catálogo; Inventory orquesta la operación de compra sin exponer su lógica interna.


### 7.3 ¿Por qué arquitectura hexagonal (Ports & Adapters)?
* **Testabilidad**: Los puertos se pueden mockear sin levantar Spring o una BD real, simplemente simulando los “puertos” (interfaces) lo cual facilita llegar al ≥ 80 % de cobertura en especial para logica de negocio donde los tests son muy rápidos.

* **Flexibilidad**: Se puede cambiar el cliente HTTP, la BD o incluso exponer nueva interfaz (gRPC, Kafka) sin tocar la lógica de negocio.

* **Asumir el riesgos**: Al usar la arquitectura hexagonal se entra en el riesgo de gastar mas tiempo al haber mas clases y mas abstracciones, pero se gana mantenibilidad y escalabilidad a largo plazo. 


### 7.4 ¿Por qué Spring WebClient en lugar de RestTemplate o FeignClient?
* **Tiempo de espera y reintentos**: configura timeout y retryWhen de forma fluida en el mismo flujo reactivo.

* **Feign**: Es declarativo, pero menos flexible a nivel de control fino sobre reconexiones y timeouts reactivos.

* **RestTemplate**: Mantenimient, Es muy simple pero obsoleto en favor de WebClient (en Spring 5+).


<a id="8-testing-y-cobertura"></a>
## 8. 🧪 Testing y Cobertura

Se consigue un 100% de cobertura en servicios/casos de uso y endpoints, y un total de 89% en todo el microsericio de inventario

<img width="1842" height="562" alt="Cobertura" src="https://github.com/user-attachments/assets/5ff86892-3ab8-48bd-84ed-517cced9f2fc" />

<a id="9-buenas-practicas"></a>
## 9. 💎 Buenas practicas

* **Responsabilidad Única**
  Cada clase y servicio cumple un único propósito.
* **Arquitectura Hexagonal**
  Puertos y adaptadores para aislar la lógica de negocio.
* **Inyección por Constructor**
  Facilita pruebas y evita null-checks.
* **Manejo Global de Excepciones**
  Siempre responde en formato JSON\:API, incluso en errores inesperados, gracias al ApiExceptionHandler.
* **DTOs y Mappers Dedicados**
  Separación clara entre dominio y transporte.
* **Lombok**
  Reduce código boilerplate en modelos, constructores y builders.
* **Spring WebClient**
  Flujo reactivo con **timeouts** y **reintentos** configurables.
* **Logs Estructurados**
  SLF4J + niveles claros para trazabilidad.
* **Cobertura de Pruebas ≥ 80 %**
  Tests unitarios y de integración en servicios y controladores.
* **Análisis de Código con SonarQube**
Análisis estático con SonarQube sin issues, warnings ni vulnerabilidades, y sin detección de code smells en codigo en la inspección del IDE, con el fin de verificar la calidad del código (Sin incluir paquete de Tests).

* **GitFlow**
Buenas practicas de git con creacion de ramas independientes, pull request y conventional commits.

**Base de datos**
* Esquema dedicado `sales` para separar dominio operativo.
* Documentación inline con `COMMENT ON TABLE` y `COMMENT ON COLUMN`.

**Contenerización**

* Multi-stage Dockerfiles para optimizar la imagen de producción.
* Orquestación con Docker Compose para levantar DB y microservicios juntos.


<a id="10-uso-de-ia"></a>
## 10. 🤖 Uso de IA

* **Diagramación con Mermaid**
  Generación automática de diagramas de flujo para README.
* **Pruebas Unitarias Asistidas**
  Creación de esqueletos de tests y casos borde usando sugerencias de IA (ChatGPT).
* **Copilot para Redacción de Código**
  Autocompletado y snippets para DTOs, mappers y configuraciones Spring Boot.
* **Copilot para Comentarios y Documentación**
  Ayuda en la redacción de descripciones, comentarios de métodos y README.
* **Revisión de Pull Requests**
  Uso de IA para detectar patrones anti–patrón, estandares semanticos de nombres en variables, clases y metodos, y sugerir mejoras en PRs antes de merge.
* **Refactorización Sugerida**
  Recomendaciones automáticas de renombrado, extracción de métodos y simplificación de expresiones.


<a id="11-propuestas-de-mejora"></a>
## 11. 🚧 Propuestas de mejora
* **Paginacion**
   Implementar las respuestas paginadas en las peticiones de obtencion de datos, de cara al envio de datos al usuario para reducir tiempos de respuesta.
* **Documentacion**
   Mejorar documentacion para explicar la estructura y flujo de la arquitectura hexagonal, y division de responsabilidades por capa, puerto y adaptador.
* **Logs**
   Aumentar cobertura y uso de logs que permitan validar trazabilidad de flujo de datos en caso de incosistencias o bugs.
