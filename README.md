# 🚀 Microservicios-con-Spring 

Sistema backend de una aplicacion web para la gestion tareas de un proyecto y que ademnas implementa un servicio de autenticacion, y gestion de archivos.

## 🏛 Arquitectura del Sistema

El proyecto está construido bajo una arquitectura de microservicios utilizando el ecosistema de **Spring Cloud**.

### 📦 Componentes Principales:
* **Config Server:** Gestión centralizada de configuraciones (Spring Cloud Config).
* **Discovery Server:** Registro y descubrimiento de servicios con **Netflix Eureka**.
* **API Gateway:** Punto de entrada único utilizando **Spring Cloud Gateway**.
* **Microservicios de Dominio:** 
    * `service-auth`: Gestión de usuarios y autenticacion.
    * `service-task`: Gestion de tareas.

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