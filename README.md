# 📦 Inventory & Products Microservices

Este **mono-repo** alberga dos microservicios en Spring Boot 3.4 que se exponen por completo en el estandar JSON-API y persisten en PostgreSQL:

1. **Products**: catálogo de productos (crear, listar, obtener por ID).  
2. **Inventory**: gestión de stock y registro de compras contra el catálogo.

Ambos están implementados en Spring Boot 3.4, se comunican vía HTTP en el estandar JSON-API y persisten en PostgreSQL.

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
9. [🚧 Propuestas de mejora](#9-propuestas-de-mejora)
10. [Licencia](#8-licencia)

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
├─ init.sql ← script de creación y datos de prueba
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

aqui imagen de diagrama general de arquitectura

<a id="6-endpoints-principales"></a>
## 6. Endpoints principales

Aca vendra una captura de cada endpoint en swagger con su respectivo diagrama de interaccion entre servicios 


<a id="7-justificacion-y-decisiones-tecnicas"></a>
## 7. Justificacion y decisiones tecnicas

### 7.1 ¿Porque PostgreSQL y no otra?

ACID y consistencia: PostgreSQL garantiza transacciones atómicas, consistentes, aisladas y durables, imprescindible para operaciones de inventario y compras, una de las mejores opciones para soluciones de datos estrcuturados.

### 7.2 ¿Por qué implementar el endpoint de compra en el microservicio de Inventory?
**Responsabilidad única**: El inventario es quien posee el estado de stock; es responsable de descontar y registrar transacciones.

**Consistencia transaccional**: Al agrupar la presistencia del registro de compra + ajuste de inventario en un mismo bounded context, se reduce riesgo de datos incongruentes al hacerse en una misma transacion.

**Acoplamiento mínimo**: El servicio Products sólo aporta datos de catálogo; Inventory orquesta la operación de compra sin exponer su lógica interna.


### 7.3 ¿Por qué arquitectura hexagonal (Ports & Adapters)?
**Testabilidad**: Los puertos se pueden mockear sin levantar Spring o una BD real, simplemente simulando los “puertos” (interfaces) lo cual facilita llegar al ≥ 80 % de cobertura en especial para logica de negocio donde los tests son muy rápidos.

**Flexibilidad**: Se puede cambiar el cliente HTTP, la BD o incluso exponer nueva interfaz (gRPC, Kafka) sin tocar la lógica de negocio.

**Asumir el riesgos**: Al usar la arquitectura hexagonal se entra en el riesgo de gastar mas tiempo al haber mas clases y mas abstracciones, pero se gana mantenibilidad y escalabilidad a largo plazo. 


### 7.4 ¿Por qué Spring WebClient en lugar de RestTemplate o FeignClient?
**Tiempo de espera y reintentos**: configura timeout y retryWhen de forma fluida en el mismo flujo reactivo.

**Feign**: Es declarativo, pero menos flexible a nivel de control fino sobre reconexiones y timeouts reactivos.

**RestTemplate**: Mantenimient, Es muy simple pero obsoleto en favor de WebClient (en Spring 5+).


<a id="8-testing-y-cobertura"></a>
## 8. 🧪 Testing y Cobertura

Se consigue un 100% de cobertura en servicios/casos de uso y endpoints, y un total de 89% en todo el microsericio de inventario

<img width="1842" height="562" alt="Cobertura" src="https://github.com/user-attachments/assets/5ff86892-3ab8-48bd-84ed-517cced9f2fc" />



<a id="9-propuestas-de-mejora"></a>
## 9. 🚧 Propuestas de mejora


