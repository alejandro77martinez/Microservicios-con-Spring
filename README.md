# 🚀 Microservicios-con-Spring 

Sistema backend de la aplicacion -TaskManager- la cual tiene como proposito realizar la gestion de tareas de proyectos de software bajo un marco de trabajo kanban. El sistema cuenta con la implementacion de varios microservicios que cumplen una funcion o caso de uso, como por ejemplo un servicio de autenticacion usando cookies y JWT, un servicio para la gestion de proyectos y otro para la gestion de tareas, asi como otros servicios que satisfacen requisitos arquitectonicos dentro de todo el sistema backend.

## 🏛 Arquitectura del Sistema

El proyecto está construido bajo una arquitectura en microservicios utilizando el ecosistema de **Spring Cloud**.

### 📦 Componentes Principales:
* **Config Server:** Gestión centralizada de configuraciones (Spring Cloud Config).
* **Discovery Server:** Registro y descubrimiento de servicios con **Netflix Eureka**.
* **API Gateway:** Punto de entrada único utilizando **Spring Cloud Gateway**.
* **Microservicios de Dominio:** 
    * `service-auth`: Gestión de usuarios y autenticacion.
    * `service-task`: Gestion de tareas.
    * `service-project`: Gestion de proyectos.

## 🛠️ Tecnologías Utilizadas

* **Java 21 OpenJDK+**
* **Spring Boot 3.5.4**
* **Spring Cloud (Eureka, Gateway, Config)**
* **Bases de Datos: MongoDB**
* **Mensajería: RabbitMQ o Kafka (por definir)**
* **Docker & Docker Compose**

## 🔧 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:
* OpenJDK 21 o superior.
* Docker y Docker compose.
* Cluster en MongoDB Atlas o local

## 🏃 Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/alejandro77martinez/Microservicios-con-Spring.git](https://github.com/alejandro77martinez/Microservicios-con-Spring.git)

## 📄 Licencia

Este proyecto está bajo la licencia **CC BY-NC-SA 4.0**. Consulta el archivo [LICENSE](LICENSE) para más detalles.
