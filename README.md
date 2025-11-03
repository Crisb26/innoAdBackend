# InnoAd Backend

## Descripción del Proyecto
InnoAd Backend es un sistema de gestión de publicidad potenciado por Inteligencia Artificial, desarrollado con Spring Boot 3.5.0. Este sistema proporciona una API REST robusta para la gestión de publicidad, usuarios y conversaciones con IA, implementando características avanzadas de seguridad y rendimiento.

## Tecnologías Principales
- Java 21 (LTS)
- Spring Boot 3.5.0
- Spring Security con JWT
- Spring Data JPA
- Redis para caché
- H2 Database
- Maven
- Docker
- Swagger/OpenAPI para documentación

## Requisitos de Sistema
### Requisitos Mínimos
- JDK 21
- Maven 3.9.x
- Docker Desktop
- 4GB RAM
- 2 núcleos de CPU
- 10GB espacio en disco

### Software Requerido
1. **Java Development Kit (JDK) 21**
   - Descargar de: https://adoptium.net/
   - Configurar JAVA_HOME

2. **Docker Desktop**
   - Descargar de: https://www.docker.com/products/docker-desktop
   - Necesario para la contenerización

3. **IDE Recomendado: Visual Studio Code**
   - Extensiones necesarias:
     - Extension Pack for Java
   # InnoAd Backend

   ## Descripción del Proyecto
   InnoAd Backend es un sistema de gestión de publicidad potenciado por Inteligencia Artificial, desarrollado con Spring Boot 3.5.0. Este sistema proporciona una API REST robusta para la gestión de publicidad, usuarios y conversaciones con IA, implementando características avanzadas de seguridad y rendimiento.

   ## Tecnologías Principales
   - Java 21 (LTS)
   - Spring Boot 3.5.0
   - Spring Security con JWT
   - Spring Data JPA
   - Redis para caché
   - H2 Database
   - Maven
   - Docker
   - Swagger/OpenAPI para documentación

   ## Requisitos de Sistema
   ### Requisitos Mínimos
   - JDK 21
   - Maven 3.9.x
   - Docker Desktop
   - 4GB RAM
   - 2 núcleos de CPU
   - 10GB espacio en disco

   ### Software Requerido
   1. **Java Development Kit (JDK) 21**
      - Descargar de: https://adoptium.net/
      - Configurar JAVA_HOME

   2. **Docker Desktop**
      - Descargar de: https://www.docker.com/products/docker-desktop
      - Necesario para la contenerización

   3. **IDE Recomendado: Visual Studio Code**
      - Extensiones necesarias:
        - Extension Pack for Java
        - Spring Boot Extension Pack
        - Lombok Annotations Support
        - Docker
        - GitLens

   ## Estructura del Proyecto
   ```plaintext
   innoadBackend/
   ├── src/
   │   ├── main/
   │   │   ├── java/com/innoad/
   │   │   │   ├── configuracion/    # Configuraciones de Spring
   │   │   │   ├── controlador/      # Controladores REST
   │   │   │   ├── dto/             # Objetos de transferencia de datos
   │   │   │   ├── modelo/          # Entidades y modelos
   │   │   │   ├── repositorio/     # Repositorios JPA
   │   │   │   ├── servicio/        # Lógica de negocio
   │   │   │   └── utilidad/        # Clases utilitarias
   │   │   └── resources/
   │   │       ├── application.yml   # Configuración principal
   │   │       ├── application-dev.yml
   │   │       └── application-prod.yml
   │   └── test/                    # Pruebas unitarias e integración
   ├── Dockerfile                   # Configuración de Docker
   ├── pom.xml                     # Dependencias Maven
   └── README.md                   # Documentación
   ```

   ## Documentación de la API

   ### Acceso a la Documentación
   La documentación completa de la API está disponible a través de Swagger UI:
   ```
   http://localhost:8000/swagger-ui.html
   ```

   ### Endpoints Principales

   #### Autenticación y Usuarios
   - `POST /api/auth/registro` - Registro de nuevos usuarios
   - `POST /api/auth/login` - Inicio de sesión
   - `POST /api/auth/refresh-token` - Renovar token JWT

   #### Publicidad
   - `GET /api/publicidad` - Listar publicidades
   - `POST /api/publicidad` - Crear nueva publicidad
   - `PUT /api/publicidad/{id}` - Actualizar publicidad
   - `DELETE /api/publicidad/{id}` - Eliminar publicidad

   #### Conversaciones IA
   - `POST /api/ia/conversacion` - Iniciar conversación con IA
   - `GET /api/ia/conversacion/{id}` - Obtener historial
   - `POST /api/ia/mensaje` - Enviar mensaje a IA

   ### Seguridad
   - Autenticación mediante JWT
   - Roles de usuario: ADMIN, ANUNCIANTE, CLIENTE
   - Tokens de acceso y refresh
   - Validación de sesiones

   ## Variables de Entorno Requeridas

   ```properties
   # Configuración JWT
   JWT_SECRET=tu_clave_secreta_segura
   JWT_EXPIRATION=3600000
   JWT_REFRESH_EXPIRATION=86400000

   # Perfil de Spring
   SPRING_PROFILES_ACTIVE=dev

   # Puerto de la aplicación
   SERVER_PORT=8000

   # Configuración Redis (opcional)
   REDIS_HOST=localhost
   REDIS_PORT=6379
   ```

   ## Construcción y Despliegue

   ### Construcción Local
   ```bash
   # Compilar el proyecto
   ./mvnw clean install

   # Ejecutar pruebas
   ./mvnw test

   # Ejecutar la aplicación
   ./mvnw spring-boot:run
   ```

   ### Despliegue con Docker
   ```bash
   # Construir imagen
   docker build -t innoad-backend .

   # Ejecutar contenedor
   docker run -d \
     --name innoad-backend \
     -p 8000:8000 \
     -e SPRING_PROFILES_ACTIVE=prod \
     innoad-backend
   ```

   ## Pruebas y Calidad de Código

   ### Ejecutar Pruebas
   ```bash
   # Pruebas unitarias
   ./mvnw test

   # Pruebas de integración
   ./mvnw verify
   ```

   ### Análisis de Código
   El proyecto utiliza:
   - Pruebas unitarias con JUnit 5
   - Integración continua
   - Cobertura de código
   - Validaciones de seguridad

   ## Contribución al Proyecto

   1. Crear una rama para cada característica:
   ```bash
   git checkout -b feature/nombre-caracteristica
   ```

   2. Seguir las convenciones de código:
      - Usar CamelCase para nombres de clases y métodos
      - Documentar métodos públicos
      - Mantener clases enfocadas y cohesivas
      - Escribir pruebas unitarias

   3. Proceso de envío de cambios:
   ```bash
   git add .
   git commit -m "descripción detallada del cambio"
   git push origin feature/nombre-caracteristica
   ```

   4. Crear Pull Request con:
      - Descripción clara de los cambios
      - Referencias a issues relacionados
      - Pruebas realizadas

   ## Solución de Problemas Comunes

   1. **Error de conexión a Redis**
      - Verificar que Redis está ejecutándose
      - Comprobar configuración en application.yml

   2. **Problemas con JWT**
      - Validar SECRET_KEY configurada
      - Verificar tiempo de expiración del token

   3. **Errores de compilación**
      - Actualizar dependencias Maven
      - Limpiar caché de Maven:
        ```bash
        ./mvnw clean
        ```

   ## Mantenimiento

   - Actualizar dependencias regularmente
   - Monitorear logs de la aplicación
   - Realizar copias de seguridad de la base de datos
   - Revisar y actualizar documentación

   ## Contacto y Soporte

   Para reportar problemas o sugerir mejoras:
   1. Crear un issue en GitHub
   2. Proporcionar detalles completos del problema
   3. Incluir logs relevantes
