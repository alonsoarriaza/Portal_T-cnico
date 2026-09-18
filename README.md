# PORTAL TÉCNICO PARA SOPORTE

Portal web empresarial integral para la gestiÃ³n centralizada de clientes, soporte tÃ©cnico, parque information de equipos, servicios contratados, sitios web, gestiÃ³n documental con control de versiones inmutable, cronograma de intervenciones, auditorÃ­a en tiempo real y administraciÃ³n granular de usuarios con RBAC.

---

## 1. Stack TecnolÃ³gico

### Backend
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.3.x (Spring Web, Spring Security 6, Spring Data JPA, Spring Validation, Spring AOP)
* **Seguridad:** JWT (JSON Web Token) HMAC-SHA256 con Access & Refresh Tokens y Password Hashing BCrypt (factor 12)
* **Base de Datos:** PostgreSQL 16/17/18
* **Migraciones de BBDD:** Flyway (`V1` a `V6`)
* **GeneraciÃ³n de Informes:** OpenPDF (ExportaciÃ³n PDF de fichas tÃ©cnicas de cliente con diseÃ±o corporativo)
* **Almacenamiento Documental:** PatrÃ³n Strategy con `FileStorageService` (soporte desacoplado para Local, MinIO, AWS S3 y Azure Blob)
* **Gestor de ConstrucciÃ³n:** Maven con wrapper (`mvnw`)

### Frontend
* **Core:** React 18 + TypeScript
* **Herramienta de Build:** Vite 5
* **Enrutamiento:** React Router v6
* **Estilos & DiseÃ±o:** TailwindCSS con paleta corporativa oscura (Slate 950 / Brand Cyan / Emerald)
* **IconografÃ­a:** Lucide React
* **Cliente HTTP:** Axios con interceptor automÃ¡tico para renovaciÃ³n transparente de tokens JWT

### Infraestructura & Despliegue
* **Contenedores:** Docker & Docker Compose
* **Servidor Web / Reverse Proxy:** Nginx Alpine con compresiÃ³n gzip y fallback SPA
* **ConfiguraciÃ³n:** SeparaciÃ³n estricta por perfiles (`dev`, `prod`, `test`) y variables de entorno (`.env`)

---

## 2. Arquitectura del Sistema

```text
               Navegador Web (SPA React + TypeScript)
                                 â”‚
                                 â–¼
                     Nginx (Reverse Proxy / HTTPS)
                                 â”‚
                     â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”´â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
                     â–¼                       â–¼
            / (Archivos EstÃ¡ticos)     /api/* (REST API)
                                             â”‚
                                             â–¼
                             Spring Boot 3 (Security + JWT Filter)
                                             â”‚
                                             â–¼
                               REST Controllers (@Valid DTOs)
                                             â”‚
                                             â–¼
                                       Service Layer
                     â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
                     â–¼                       â–¼                       â–¼
              AuditoriaService       FileStorageService       PDF Generator
                     â”‚                       â”‚                       â”‚
                     â–¼                       â–¼                       â–¼
             Spring Data JPA        Almacenamiento FÃ­sico         OpenPDF
                     â”‚               (/storage/clientes/...)
                     â–¼
             PostgreSQL 16/18
            (Flyway Migrations)
```

---

## 3. Modelo de Datos y Migraciones Flyway

Las migraciones se ejecutan de forma automÃ¡tica al iniciar la aplicaciÃ³n:

| MigraciÃ³n | DescripciÃ³n |
| :--- | :--- |
| `V1__crear_esquema_seguridad.sql` | Tablas `usuarios`, `roles`, `permisos`, `usuario_roles`, `rol_permisos` |
| `V2__crear_esquema_clientes_y_activos.sql` | Tablas `clientes`, `contactos`, `equipos`, `servicios`, `webs` |
| `V3__crear_esquema_documental.sql` | Tablas `documentos`, `documento_versiones` con integridad y unicidad |
| `V4__crear_esquema_eventos_y_auditoria.sql` | Tablas `eventos` (cronograma) y `auditorias` (logs de trazabilidad) |
| `V5__crear_indices_rendimiento.sql` | Ãndices de bÃºsqueda sobre cÃ³digo, NIF/CIF, fechas, usuarios y claves forÃ¡neas |
| `V6__datos_iniciales_seed.sql` | Roles `SUPER_ADMIN` y `TECNICO`, matriz de 20 permisos granulares y asignaciones |

---

## 4. Roles y Permisos Granulares (RBAC)

### Roles Iniciales
* **`SUPER_ADMIN`**: Control global de la aplicaciÃ³n, configuraciÃ³n de usuarios, asignaciÃ³n de permisos y consulta completa de auditorÃ­a.
* **`TECNICO`**: Acceso a la gestiÃ³n tÃ©cnica de clientes, equipos, contratos, webs, calendario y documentaciÃ³n.

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
* **PostgreSQL** en ejecuciÃ³n en el puerto `5432` con la base de datos `portal_abaxial` creada:
  ```sql
  CREATE DATABASE portal_abaxial;
  ```

### 1. ConfiguraciÃ³n de Variables de Entorno
Copia el archivo de plantilla `.env.example` como `.env`:
```bash
cp .env.example .env
```

### 2. Arrancar el Backend (Spring Boot)
```bash
cd backend
./mvnw clean spring-boot:run
```
El servidor backend arrancarÃ¡ en: `http://localhost:8080`
*(Flyway crearÃ¡ automÃ¡ticamente todas las tablas, roles, permisos y el usuario inicial `admin`)*.

### 3. Arrancar el Frontend (React + Vite)
En una nueva terminal:
```bash
cd frontend
npm install
npm run dev
```
La aplicaciÃ³n web estarÃ¡ disponible en: `http://localhost:5173`

---

## 6. Credenciales Iniciales de Acceso

| Rol | Usuario | ContraseÃ±a |
| :--- | :--- | :--- |
| **Super Administrador** | `admin` | `AdminAbaxial2026!` |

*(Se recomienda cambiar la contraseÃ±a o configurar variables `INITIAL_ADMIN_PASSWORD` en producciÃ³n)*.

---

## 7. Despliegue con Docker Compose (ProducciÃ³n o Local)

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

## 8. GestiÃ³n Documental y Versionado

1. **Almacenamiento Desacoplado:** Los binarios se guardan en `/storage/clientes/{clienteId}/documentos/{uuid}.ext` y los metadatos relacionales en PostgreSQL.
2. **DetecciÃ³n de Conflictos:** Si se intenta subir un archivo con un nombre ya existente para ese cliente, el backend emite un cÃ³digo `409 CONFLICT` con el detalle del documento existente.
3. **Flujo de Nueva VersiÃ³n:** El usuario recibe un modal interactivo que le permite almacenar el archivo como versiÃ³n incremental (`v2`, `v3`, etc.) preservando todo el historial de versiones anteriores.

---

## 9. Mantenimiento y Backups de Base de Datos

### Realizar Backup de PostgreSQL
```bash
pg_dump -U postgres -h localhost -F c -b -v -f backup_portal_abaxial_$(date +%Y%m%d).dump portal_abaxial
```

### Restaurar Backup
```bash
pg_restore -U postgres -h localhost -d portal_abaxial -v backup_portal_abaxial_YYYYMMDD.dump
```

---

## 10. CatÃ¡logo de Endpoints REST API

### AutenticaciÃ³n
* `POST /api/auth/login` â€” AutenticaciÃ³n y obtenciÃ³n de tokens
* `POST /api/auth/refresh` â€” RenovaciÃ³n de Access Token con Refresh Token
* `GET /api/auth/me` â€” Perfil y permisos del usuario autenticado
* `POST /api/auth/logout` â€” Cierre de sesiÃ³n y auditorÃ­a

### Clientes y Ficha 360
* `GET /api/clientes` â€” Listado con bÃºsqueda, filtros, ordenaciÃ³n y paginaciÃ³n
* `GET /api/clientes/{id}` â€” Ficha 360 del cliente con todas sus entidades
* `POST /api/clientes` â€” Alta de nuevo cliente
* `PUT /api/clientes/{id}` â€” ModificaciÃ³n de datos del cliente
* `DELETE /api/clientes/{id}` â€” Borrado lÃ³gico (Soft Delete)
* `GET /api/clientes/{id}/pdf` â€” Descarga de la ficha tÃ©cnica completa en PDF
* `GET /api/clientes/dashboard/stats` â€” MÃ©tricas y actividad para el Dashboard

### Contactos, Equipos, Servicios y Webs
* `GET /api/clientes/{id}/contactos` / `POST /api/clientes/{id}/contactos`
* `GET /api/equipos` / `POST /api/clientes/{id}/equipos`
* `GET /api/servicios` / `POST /api/clientes/{id}/servicios`
* `GET /api/webs` / `POST /api/clientes/{id}/webs`

### Documentos
* `GET /api/documentos` â€” Repositorio global de documentos
* `POST /api/clientes/{id}/documentos` â€” Subida de documento multipart
* `POST /api/documentos/{id}/versiones` â€” Subida de nueva versiÃ³n de documento
* `GET /api/documentos/{id}/download?version={v}` â€” Stream seguro de descarga
* `DELETE /api/documentos/{id}` â€” DesactivaciÃ³n de documento

### Cronograma y AuditorÃ­a
* `GET /api/eventos` / `POST /api/eventos` / `PUT /api/eventos/{id}`
* `GET /api/auditoria` â€” Trazabilidad y logs filtrables
* `GET /api/auditoria/recientes` â€” Ãšltimas acciones del sistema

### Usuarios y Roles
* `GET /api/usuarios` / `POST /api/usuarios` / `PUT /api/usuarios/{id}` / `DELETE /api/usuarios/{id}`
* `GET /api/roles` / `GET /api/roles/permisos`
"# Portal_T-cnico" 
"# Portal_T-cnico" 
