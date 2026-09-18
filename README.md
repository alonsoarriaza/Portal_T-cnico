# COANDA — PORTAL TÉCNICO PARA GESTIÓN Y SOPORTE

Portal web empresarial integral para la gestión centralizada de clientes, soporte técnico, parque informático de equipos, servicios contratados, sitios web, gestión documental con control de versiones inmutable, cronograma de intervenciones, auditoría en tiempo real y administración granular de usuarios con RBAC.

---

## 1. Stack Tecnológico

### Backend
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.3.x (Spring Web, Spring Security 6, Spring Data JPA, Spring Validation, Spring AOP)
* **Seguridad:** JWT (JSON Web Token) HMAC-SHA256 con Access & Refresh Tokens y Password Hashing BCrypt (factor 12)
* **Base de Datos:** PostgreSQL 16/17/18
* **Migraciones de BBDD:** Flyway (`V1` a `V12`)
* **Generación de Informes:** OpenPDF (Exportación PDF de fichas técnicas de cliente con diseño corporativo)
* **Almacenamiento Documental:** Patrón Strategy con `FileStorageService` (soporte desacoplado para Local, MinIO, AWS S3 y Azure Blob)
* **Gestor de Construcción:** Maven con wrapper (`mvnw`)

### Frontend
* **Core:** React 18 + TypeScript
* **Herramienta de Build:** Vite 5
* **Enrutamiento:** React Router v6
* **Estilos & Diseño:** Vanilla CSS + TailwindCSS con paleta corporativa y diseño limpio
* **Iconografía:** Lucide React
* **Cliente HTTP:** Axios con interceptor automático para renovación transparente de tokens JWT

### Infraestructura & Despliegue
* **Contenedores:** Docker & Docker Compose
* **Servidor Web / Reverse Proxy:** Nginx Alpine con compresión gzip y fallback SPA
* **Configuración:** Separación estricta por perfiles (`dev`, `prod`, `test`) y variables de entorno (`.env`)

---

## 2. Arquitectura del Sistema

```text
               Navegador Web (SPA React + TypeScript)
                                 │
                                 ▼
                     Nginx (Reverse Proxy / HTTPS)
                                 │
                     ┌───────────┴───────────────┐
                     ▼                           ▼
            / (Archivos Estáticos)     /api/* (REST API)
                                             │
                                             ▼
                             Spring Boot 3 (Security + JWT Filter)
                                             │
                                             ▼
                               REST Controllers (@Valid DTOs)
                                             │
                                             ▼
                                       Service Layer
                     ┌───────────────────────┼───────────────────────┐
                     ▼                       ▼                       ▼
              AuditoriaService       FileStorageService       PDF Generator
                     │                       │                       │
                     ▼                       ▼                       ▼
             Spring Data JPA        Almacenamiento Físico         OpenPDF
                     │               (/storage/clientes/...)
                     ▼
             PostgreSQL 16/18
            (Flyway Migrations)
```

---

## 3. Modelo de Datos y Migraciones Flyway

Las migraciones se ejecutan de forma automática al iniciar la aplicación:

| Migración | Descripción |
| :--- | :--- |
| `V1__crear_esquema_seguridad.sql` | Tablas `usuarios`, `roles`, `permisos`, `usuario_roles`, `rol_permisos` |
| `V2__crear_esquema_clientes_y_activos.sql` | Tablas `clientes`, `contactos`, `equipos`, `servicios`, `webs` |
| `V3__crear_esquema_documental.sql` | Tablas `documentos`, `documento_versiones` con integridad y unicidad |
| `V4__crear_esquema_eventos_y_auditoria.sql` | Tablas `eventos` (cronograma) y `auditorias` (logs de trazabilidad) |
| `V5__crear_indices_rendimiento.sql` | Índices de búsqueda sobre código, NIF/CIF, fechas, usuarios y claves foráneas |
| `V6__datos_iniciales_seed.sql` | Roles `SUPER_ADMIN` y `TECNICO`, matriz de 20 permisos granulares y asignaciones |
| `V7__agregar_tipo_recurrencia_eventos.sql` | Soporte de tipos de eventos y recurrencias |
| `V8__ampliar_soporte_recurrencias.sql` | Motor avanzado de recurrencias |
| `V9__permitir_nif_cif_no_unico.sql` | Flexibilización de NIF/CIF para sedes y sucursales |
| `V10__agregar_campos_equipos.sql` | Ampliación de metadatos técnicos de equipos |
| `V11__normalizar_estado_documentos_general.sql` | Normalización y consistencia de estados documentales |
| `V12__ampliar_longitud_campos_contactos.sql` | Ampliación de campos de contacto |

---

## 4. Roles y Permisos Granulares (RBAC)

### Roles Iniciales
* **`SUPER_ADMIN`**: Control global de la aplicación, configuración de usuarios, asignación de permisos y consulta completa de auditoría.
* **`TECNICO`**: Acceso a la gestión técnica de clientes, equipos, contratos, webs, calendario y documentación.

### Permisos del Sistema
```text
CLIENTES:    CLIENTE_VER, CLIENTE_CREAR, CLIENTE_EDITAR, CLIENTE_ELIMINAR
CONTACTOS:   CONTACTO_VER, CONTACTO_GESTIONAR
EQUIPOS:     EQUIPO_VER, EQUIPO_CREAR, EQUIPO_EDITAR, EQUIPO_ELIMINAR
SERVICIOS:   SERVICIO_VER, SERVICIO_GESTIONAR
WEBS:        WEB_VER, WEB_GESTIONAR
DOCUMENTOS:  DOCUMENTO_VER, DOCUMENTO_SUBIR, DOCUMENTO_DESCARGAR, DOCUMENTO_ELIMINAR
CRONOGRAMA:  CRONOGRAMA_VER, CRONOGRAMA_GESTIONAR
AUDITORIA:   HISTORIAL_VER
SEGURIDAD:   USUARIO_VER, USUARIO_GESTIONAR, ROL_GESTIONAR, PERMISO_GESTIONAR
```

---

## 5. Puesta en Marcha en Entorno Local

### Requisitos Previos
* **Java 21** instalado (`java -version`)
* **Node.js 20+** y **NPM** (`node -v`, `npm -v`)
* **PostgreSQL** en ejecución en el puerto `5432` con la base de datos `portal_coanda` creada:
  ```sql
  CREATE DATABASE portal_coanda;
  ```

### 1. Configuración de Variables de Entorno
Copia el archivo de plantilla `.env.example` como `.env`:
```bash
cp .env.example .env
```

### 2. Arrancar el Backend (Spring Boot)
```bash
cd backend
./mvnw clean spring-boot:run
```
El servidor backend arrancará en: `http://localhost:8080`
*(Flyway creará automáticamente todas las tablas, roles y permisos)*.

### 3. Arrancar el Frontend (React + Vite)
En una nueva terminal:
```bash
cd frontend
npm install
npm run dev
```
La aplicación web estará disponible en: `http://localhost:5173`

---

## 6. Autenticación y Acceso Inicial

El acceso al portal requiere autenticación de usuario mediante credenciales. El sistema cuenta con control de acceso basado en roles (RBAC):

* **Super Administrador:** Acceso completo a todas las funcionalidades técnicas, administrativas y de auditoría.
* **Técnico:** Gestión de clientes, parque de equipos, servicios y agenda de intervenciones.

> **Seguridad:** Las credenciales del usuario administrador se definen mediante las variables de entorno `INITIAL_ADMIN_USERNAME` e `INITIAL_ADMIN_PASSWORD` en el archivo `.env`. Se recomienda definir contraseñas robustas y nunca compartir credenciales en repositorios públicos.

---

## 7. Despliegue con Docker Compose

Para levantar el ecosistema completo (PostgreSQL + Backend + Frontend Nginx) en un solo comando:

```bash
docker compose up --build -d
```

* **Frontend & Reverse Proxy:** `http://localhost` (puerto 80)
* **API REST Backend:** `http://localhost:8080/api`
* **Base de Datos PostgreSQL:** `localhost:5432`

Para detener los contenedores:
```bash
docker compose down
```

---

## 8. Gestión Documental y Versionado

1. **Almacenamiento Desacoplado:** Los binarios se guardan en `/storage/clientes/{clienteId}/documentos/{uuid}.ext` y los metadatos relacionales en PostgreSQL.
2. **Detección de Conflictos:** Si se intenta subir un archivo con un nombre ya existente para ese cliente, el backend emite un código `409 CONFLICT` con el detalle del documento existente.
3. **Flujo de Nueva Versión:** El usuario recibe un modal interactivo que le permite almacenar el archivo como versión incremental (`v2`, `v3`, etc.) preservando todo el historial de versiones anteriores.

---

## 9. Mantenimiento y Backups de Base de Datos

### Realizar Backup de PostgreSQL
```bash
pg_dump -U postgres -h localhost -F c -b -v -f backup_portal_coanda_$(date +%Y%m%d).dump portal_coanda
```

### Restaurar Backup
```bash
pg_restore -U postgres -h localhost -d portal_coanda -v backup_portal_coanda_YYYYMMDD.dump
```

---

## 10. Catálogo de Endpoints REST API

### Autenticación
* `POST /api/auth/login` — Autenticación y obtención de tokens
* `POST /api/auth/refresh` — Renovación de Access Token con Refresh Token
* `GET /api/auth/me` — Perfil y permisos del usuario autenticado
* `POST /api/auth/logout` — Cierre de sesión y auditoría

### Clientes y Ficha 360
* `GET /api/clientes` — Listado con búsqueda, filtros, ordenación y paginación
* `GET /api/clientes/{id}` — Ficha 360 del cliente con todas sus entidades
* `POST /api/clientes` — Alta de nuevo cliente
* `PUT /api/clientes/{id}` — Modificación de datos del cliente
* `DELETE /api/clientes/{id}` — Borrado lógico (Soft Delete)
* `GET /api/clientes/{id}/pdf` — Descarga de la ficha técnica completa en PDF
* `GET /api/clientes/dashboard/stats` — Métricas y actividad para el Dashboard

### Contactos, Equipos, Servicios y Webs
* `GET /api/clientes/{id}/contactos` / `POST /api/clientes/{id}/contactos`
* `GET /api/equipos` / `POST /api/clientes/{id}/equipos`
* `GET /api/servicios` / `POST /api/clientes/{id}/servicios`
* `GET /api/webs` / `POST /api/clientes/{id}/webs`

### Documentos
* `GET /api/documentos` — Repositorio global de documentos
* `POST /api/clientes/{id}/documentos` — Subida de documento multipart
* `POST /api/documentos/{id}/versiones` — Subida de nueva versión de documento
* `GET /api/documentos/{id}/download?version={v}` — Stream seguro de descarga
* `DELETE /api/documentos/{id}` — Desactivación de documento

### Cronograma y Auditoría
* `GET /api/eventos` / `POST /api/eventos` / `PUT /api/eventos/{id}`
* `GET /api/auditoria` — Trazabilidad y logs filtrables
* `GET /api/auditoria/recientes` — Últimas acciones del sistema

### Usuarios y Roles
* `GET /api/usuarios` / `POST /api/usuarios` / `PUT /api/usuarios/{id}` / `DELETE /api/usuarios/{id}`
* `GET /api/roles` / `GET /api/roles/permisos`
