# Especificacion de Requisitos del Sistema

## 1. Informacion general

- **Proyecto:** TaskManager Backend basado en microservicios Spring
- **Version del documento:** 0.1
- **Fecha de analisis:** 2026-05-20
- **Fuente base:** implementacion actual del repositorio
- **Objetivo:** establecer una primera especificacion de requisitos funcionales y no funcionales a partir del estado real del codigo

> Nota: este documento describe principalmente lo que ya esta implementado o claramente inferido desde el codigo. No sustituye una validacion posterior con negocio, frontend o producto.

## 2. Proposito y alcance

El sistema provee el backend de una aplicacion web para la gestion de proyectos y tareas con enfoque tipo kanban. La solucion esta organizada como un conjunto de microservicios Spring Cloud que cubren autenticacion, usuarios, proyectos, tareas, configuracion centralizada, descubrimiento de servicios y exposicion unificada mediante un API Gateway.

El alcance funcional actual incluye:

- registro e inicio de sesion de usuarios
- gestion de sesion mediante JWT en cookie `HttpOnly`
- administracion basica de usuarios
- alta, consulta, actualizacion y baja de proyectos
- alta, consulta, actualizacion y baja de tareas
- filtros de consulta para proyectos y tareas
- proteccion de endpoints a nivel de gateway

## 3. Actores del sistema

| Actor | Descripcion |
|---|---|
| Usuario no autenticado | Puede registrarse, iniciar sesion y consultar si un usuario existe |
| Usuario autenticado | Puede operar sobre proyectos, tareas y consultar informacion de sesion |
| Miembro de equipo | Usuario listado en `teamMembers` de un proyecto |
| Creador del proyecto | Usuario listado en `userCreated` del proyecto |
| Frontend web | Cliente principal que consume la API via `api-gateway` |
| Microservicios internos | Servicios que se comunican via Eureka y Gateway/LoadBalancer |

## 4. Vision arquitectonica

| Componente | Responsabilidad principal | Puerto/configuracion |
|---|---|---|
| `service-registry` | Registro y descubrimiento con Eureka | `8761`, contexto `/api/v1/registry` |
| `config-server` | Configuracion centralizada en perfil `native` | `8888` |
| `api-gateway` | Punto de entrada unico, CORS, validacion de JWT y ruteo | `8060`, publicado como `8080` en Docker |
| `auth-service` | Usuarios, autenticacion, JWT, sesion | `8085`, contexto `/api/v1` |
| `project-service` | Gestion de proyectos | `8086`, contexto `/api/v1/project` |
| `task-service` | Gestion de tareas | `8087`, contexto `/api/v1/task` |

Consideraciones tecnicas observadas:

- Java 21 y Spring Boot `3.5.4`
- Spring Cloud `2025.0.0`
- persistencia MongoDB por servicio
- configuracion remota via Spring Cloud Config
- descubrimiento de servicios con Eureka
- despliegue esperado con Docker Compose
- dependencias OpenAPI presentes en `auth-service`, `project-service` y `task-service`

## 5. Requisitos funcionales

### 5.1 Autenticacion y usuarios

**RF-01. Registro de usuario**

El sistema debe permitir registrar usuarios mediante `POST /api/v1/user/register` con los campos `name`, `lastName`, `email`, `password` y opcionalmente `roles`.

Reglas observadas:

- `email` es obligatorio, unico y con formato valido
- `password` es obligatoria y debe tener al menos 8 caracteres
- si no se envian roles, se asigna `["USER"]`
- la contrasena debe almacenarse cifrada con BCrypt

**RF-02. Inicio de sesion**

El sistema debe permitir autenticar usuarios mediante `POST /api/v1/auth/login` usando `email` y `password`.

Resultado esperado:

- si la autenticacion es correcta, se debe devolver una cookie `AUTH_TOKEN`
- la cookie debe ser `HttpOnly`, `path=/`, `SameSite` configurable y `secure` configurable
- la sesion JWT actual tiene vigencia de 10 minutos

**RF-03. Validacion y renovacion de sesion**

El sistema debe soportar:

- validacion de token mediante `POST /api/v1/auth/validate`
- renovacion de token mediante `POST /api/v1/auth/refresh`
- consulta del usuario autenticado mediante `POST /api/v1/auth/session`
- cierre de sesion mediante `POST /api/v1/auth/logout`

Comportamiento relevante:

- `refresh` y `session` dependen del token presente en la cookie `AUTH_TOKEN`
- el gateway inyecta el token en el body para esos endpoints
- `logout` elimina la cookie estableciendo `maxAge=0`

**RF-04. Consulta de usuarios**

El sistema debe permitir:

- consultar todos los usuarios: `GET /api/v1/user/all`
- consultar un usuario por id: `GET /api/v1/user/{id}`
- verificar existencia de usuario por correo enviado en body: `POST /api/v1/user/exist`
- buscar usuarios por coincidencia parcial de email: `GET /api/v1/user/search/email/{email}`
- recuperar un conjunto de usuarios por ids de equipo: `POST /api/v1/user/search/team`

Reglas observadas:

- si el usuario no tiene avatar, la respuesta usa `/user.png`
- la busqueda por equipo requiere una lista no vacia de ids

### 5.2 Gestion de proyectos

**RF-05. Creacion de proyectos**

El sistema debe permitir crear proyectos mediante `POST /api/v1/project`.

Campos requeridos observados en la solicitud:

- `name`
- `client`
- `summary`
- `priority`
- `health`
- `progress`
- `methodology`
- `createdDate`
- `startDate`
- `dueDate`
- `tags`
- `userCreated`
- `teamMembers`

Reglas observadas:

- `name` debe ser unico
- `name` debe tener entre 2 y 100 caracteres
- `client` debe tener entre 2 y 50 caracteres
- `summary` no debe exceder 500 caracteres
- `priority` solo admite `Alta`, `Media` o `Baja`
- `health` solo admite `En foco`, `En riesgo` o `Descubrimiento`
- `progress` debe estar entre 0 y 100
- `createdDate` debe ser pasada o presente
- `dueDate` debe ser futura
- `userCreated` y `teamMembers` son obligatorios

**RF-06. Consulta de proyectos**

El sistema debe permitir:

- listar todos los proyectos: `GET /api/v1/project`
- consultar un proyecto por id: `GET /api/v1/project/{projectId}`
- filtrar por cliente: `GET /api/v1/project/client/{clientName}`
- filtrar por prioridad: `GET /api/v1/project/priority/{priority}`
- buscar por etiqueta: `GET /api/v1/project/search/tag/{tag}`
- listar proyectos asociados a un usuario: `GET /api/v1/project/ofTheUser/{userId}`
- obtener resumen de proyecto: `GET /api/v1/project/{projectId}/summary`

La vista resumida por usuario debe incluir, al menos:

- `id`, `name`, `client`, `summary`
- `creator` y `role` del usuario dentro del proyecto
- `priority`, `health`, `progress`, `dueDate`, `methodology`
- `teamMembers` y `tags`

**RF-07. Actualizacion y eliminacion de proyectos**

El sistema debe permitir:

- actualizar un proyecto completo: `PUT /api/v1/project/{projectId}`
- eliminar un proyecto: `DELETE /api/v1/project/{projectId}`
- actualizar solo progreso: `PUT /api/v1/project/{projectId}/progress/{progress}`
- actualizar solo salud: `PUT /api/v1/project/{projectId}/health/{health}`
- actualizar solo prioridad: `PUT /api/v1/project/{projectId}/priority/{priority}`

### 5.3 Gestion de tareas

**RF-08. Creacion de tareas**

El sistema debe permitir crear tareas mediante `POST /api/v1/task`.

Campos relevantes observados:

- `title`
- `description`
- `type`
- `status`
- `projectId`
- `assigneeId` opcional
- `parentTaskId` opcional
- `dueDate`
- `createdDate` opcional
- `startDate` opcional
- `priority`
- `effortPoints` opcional
- `blocked` opcional

Reglas observadas:

- `title` es obligatorio y debe tener entre 2 y 100 caracteres
- `description` es obligatoria y debe tener entre 2 y 500 caracteres
- `type` solo admite `Error`, `Funcionalidad`, `Mejora` o `Documentacion`
- `status` solo admite `Creada`, `En curso`, `En revision` o `Completada`
- `projectId` es obligatorio
- `dueDate` debe ser futura
- `createdDate` y `startDate` no pueden estar en el futuro
- `priority` solo admite `Alta`, `Media` o `Baja`
- `effortPoints`, si se informa, debe estar entre 0 y 100
- si `createdDate` no se informa, se usa la fecha actual del servidor
- si `effortPoints` no se informa, se usa `0`

**RF-09. Consulta de tareas**

El sistema debe permitir:

- listar todas las tareas: `GET /api/v1/task`
- consultar una tarea por id: `GET /api/v1/task/{taskId}`
- filtrar por estado: `GET /api/v1/task/status/{status}`
- filtrar por prioridad: `GET /api/v1/task/priority/{priority}`
- filtrar por tipo: `GET /api/v1/task/type/{type}`
- filtrar por proyecto: `GET /api/v1/task/project/{projectId}`
- filtrar por asignado: `GET /api/v1/task/assignee/{assigneeId}`
- obtener tarjetas por proyecto y estado: `GET /api/v1/task/project/{projectId}/status/{status}`
- obtener resumen de tarea: `GET /api/v1/task/{taskId}/summary`
- obtener tareas por conjunto de proyectos: `POST /api/v1/task/byprojects`

**RF-10. Actualizacion y eliminacion de tareas**

El sistema debe permitir:

- actualizar una tarea completa: `PUT /api/v1/task/{taskId}`
- eliminar una tarea: `DELETE /api/v1/task/{taskId}`
- eliminar un conjunto de tareas: `DELETE /api/v1/task/set`
- actualizar solo estado: `PUT /api/v1/task/{taskId}/status/{status}`
- actualizar solo prioridad: `PUT /api/v1/task/{taskId}/priority/{priority}`
- actualizar solo puntos de esfuerzo: `PUT /api/v1/task/{taskId}/effortPoints/{effortPoints}`
- actualizar solo bloqueo: `PUT /api/v1/task/{taskId}/blocked/{blocked}`

Regla adicional:

- la eliminacion por conjunto debe fallar si alguno de los ids solicitados no existe

### 5.4 Gateway y acceso

**RF-11. Punto de entrada unico**

El sistema debe exponer la API publica a traves de `api-gateway`, publicando el puerto `8080` y resolviendo los microservicios mediante Eureka y LoadBalancer.

**RF-12. Proteccion de rutas**

El gateway debe exigir cookie `AUTH_TOKEN` para todas las rutas excepto:

- `POST /api/v1/auth/login`
- `POST /api/v1/user/register`
- `POST /api/v1/user/exist`

**RF-13. Validacion centralizada de token**

Antes de enrutar una solicitud protegida, el gateway debe:

- leer el token desde la cookie `AUTH_TOKEN`
- invocar internamente `auth-service` para validar el token
- responder `401 Unauthorized` si el token falta o no es valido

## 6. Reglas de negocio transversales

| Codigo | Regla |
|---|---|
| RN-01 | El email del usuario debe ser unico |
| RN-02 | El nombre del proyecto debe ser unico |
| RN-03 | Todas las operaciones de proyectos y tareas se conciben para usuarios autenticados a traves del gateway |
| RN-04 | El sistema usa JWT firmado con secreto configurable por entorno |
| RN-05 | El estado y prioridad de tareas y proyectos estan restringidos a catalogos cerrados |
| RN-06 | Los errores de validacion deben responder `400` con detalle por campo cuando aplique |
| RN-07 | La persistencia de usuarios, proyectos y tareas esta separada por microservicio |

## 7. Requisitos no funcionales

### 7.1 Seguridad

- autenticacion basada en JWT
- uso de cookie `HttpOnly`
- politica `stateless` en autenticacion
- configuracion de `secure` y `same-site` por variables de entorno

### 7.2 Persistencia y datos

- MongoDB como almacenamiento
- una URI de base de datos por servicio: autenticacion, proyectos y tareas
- colecciones observadas: `users`, `projects`, `tasks`

### 7.3 Despliegue

- orquestacion local con `docker-compose.yml`
- dependencias de arranque encadenadas entre registry, config server, servicios de dominio y gateway
- MongoDB no se levanta en `docker-compose`; se espera una instancia externa o Atlas

### 7.4 Integracion y compatibilidad

- CORS habilitado en gateway
- origenes configurados actualmente:
  - `http://192.168.100.249:4200`
  - `https://taskmanager-bf193.web.app`
- metodos permitidos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

### 7.5 Calidad y pruebas

- existen pruebas unitarias y de controladores en los tres microservicios de dominio
- el proyecto incorpora JaCoCo y configuracion Sonar en `auth-service`, `project-service` y `task-service`

## 8. Modelo de informacion principal

### 8.1 Usuario

- `id`
- `name`
- `lastName`
- `email`
- `password`
- `avatar`
- `roles`

### 8.2 Proyecto

- `id`
- `name`
- `client`
- `summary`
- `priority`
- `health`
- `progress`
- `methodology`
- `createdDate`
- `startDate`
- `dueDate`
- `tags`
- `userCreated`
- `teamMembers`

### 8.3 Tarea

- `id`
- `title`
- `description`
- `type`
- `status`
- `projectId`
- `assigneeId`
- `parentTaskId`
- `dueDate`
- `createdDate`
- `startDate`
- `priority`
- `effortPoints`
- `blocked`

## 9. Hallazgos y puntos a validar

Los siguientes puntos no invalidan la primera version del ERS, pero conviene revisarlos antes de cerrar una especificacion definitiva:

1. `auth-service` implementa `update` y `deleteById` en la interfaz de usuario, pero hoy devuelven `null`; por tanto no deben considerarse requisitos implementados.
2. La autorizacion actual es de autenticacion, no de permisos por rol, propiedad del proyecto o pertenencia al equipo.
3. No se observa validacion cruzada entre servicios para comprobar que un `projectId`, `assigneeId` o `teamMembers` realmente existan antes de guardar.
4. El README menciona RabbitMQ o Kafka, pero no hay integracion de mensajeria implementada en el codigo actual.
5. La seguridad efectiva de rutas depende del gateway; internamente `auth-service` tiene `permitAll`.
6. Los microservicios de proyecto y tarea no exponen aun reglas de dominio mas avanzadas de flujo kanban, dependencia entre tareas o metricas agregadas.

## 10. Siguiente iteracion sugerida

Para una version 0.2 del documento seria recomendable agregar:

1. historias de usuario priorizadas
2. diagramas de secuencia para login, creacion de proyecto y flujo de tablero
3. matriz de permisos por rol
4. contratos de API con ejemplos de request/response
5. criterios de aceptacion por requisito
