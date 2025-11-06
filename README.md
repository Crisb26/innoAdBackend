# InnoAd Backend

Sistema de Gestion de Publicidad Digital con Inteligencia Artificial

## Descripcion

Backend RESTful desarrollado en Spring Boot para la gestion de campañas publicitarias, pantallas digitales Raspberry Pi y contenidos multimedia.

## Tecnologias

- Java 21
- Spring Boot 3.5.0
- PostgreSQL 14+
- Spring Security con JWT
- Spring Data JPA Hibernate
- Maven 3.8+
- OpenAPI Swagger

## Requisitos

- JDK 21 o superior
- Maven 3.8+
- PostgreSQL 14 o superior

## Instalacion

1. Instalar PostgreSQL
2. Crear base de datos: psql -U postgres -f innoad_postgresql_schema.sql
3. Configurar variables de entorno
4. Compilar: mvn clean install
5. Ejecutar: mvn spring-boot:run

Aplicacion disponible en http://localhost:8081

## Documentacion API

Swagger UI: http://localhost:8081/swagger-ui.html

## Usuarios por Defecto

- admin / Admin123! (ADMINISTRADOR)
- cliente_demo / Admin123! (CLIENTE)
- tecnico_demo / Admin123! (TECNICO)

## Licencia

Copyright 2025 InnoAd
