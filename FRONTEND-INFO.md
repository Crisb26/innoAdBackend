# 🚀 Backend InnoAd - Información para Integración Frontend

## ✅ ESTADO: DESPLEGADO Y LISTO

El backend está corriendo exitosamente en:
- **URL Base:** `http://localhost:8081`
- **Health Check:** `http://localhost:8081/actuator/health`
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **Puerto:** 8081 (cambió de 8080 para evitar conflicto)

---

## 🔌 Endpoints de Autenticación

### 1. Login (Iniciar Sesión)
```http
POST http://localhost:8081/api/v1/autenticacion/iniciar-sesion
Content-Type: application/json

{
  "nombreUsuarioOEmail": "admin",
  "contrasena": "Admin123!"
}
```

**Respuesta Exitosa:**
```json
{
  "exitoso": true,
  "mensaje": "Autenticación exitosa",
  "datos": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenActualizacion": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": {
      "id": 1,
      "nombreUsuario": "admin",
      "email": "admin@innoad.com",
      "nombreCompleto": "Admin InnoAd",
      "rol": {
        "nombre": "Administrador"
      },
      "permisos": []
    },
    "expiraEn": 86400
  },
  "timestamp": "2025-11-03T21:30:00.123Z",
  "errores": null
}
```

### 2. Registro
```http
POST http://localhost:8081/api/v1/autenticacion/registrar
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "nombreUsuario": "juanperez",
  "contrasena": "Password123!",
  "rol": "USUARIO"
}
```

### 3. Refrescar Token
```http
POST http://localhost:8081/api/v1/autenticacion/refrescar-token
Content-Type: application/json

{
  "tokenActualizacion": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 4. Cerrar Sesión
```http
POST http://localhost:8081/api/v1/autenticacion/cerrar-sesion
Authorization: Bearer {token}
```

---

## 👤 Usuarios de Prueba

El backend viene con usuarios precargados:

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `admin` | `Admin123!` | Administrador | Acceso completo |
| `tecnico` | `Tecnico123!` | Técnico | Soporte técnico |
| `dev` | `Dev123!` | Desarrollador | Desarrollo |
| `usuario` | `Usuario123!` | Usuario | Usuario estándar |

---

## 🔐 Autenticación en Requests

Para endpoints protegidos, incluir:
```http
Authorization: Bearer {tu-token-jwt}
```

---

## 📋 Formato de Respuesta Estándar

Todas las respuestas siguen este formato:

```typescript
interface RespuestaAPI<T> {
  exitoso: boolean;      // true si la operación fue exitosa
  mensaje: string;       // Mensaje descriptivo
  datos: T;              // Datos de respuesta (genérico)
  timestamp: string;     // ISO 8601 timestamp
  errores?: string[];    // Array de errores (opcional)
}
```

---

## 🛠️ Configuración Frontend

### Angular environment.ts
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081',  // 👈 IMPORTANTE: Puerto 8081
  apiVersion: 'v1',
  endpoints: {
    login: '/api/v1/autenticacion/iniciar-sesion',
    register: '/api/v1/autenticacion/registrar',
    refresh: '/api/v1/autenticacion/refrescar-token',
    logout: '/api/v1/autenticacion/cerrar-sesion'
  }
};
```

### Ejemplo de Servicio de Autenticación (Angular)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

interface RespuestaAPI<T> {
  exitoso: boolean;
  mensaje: string;
  datos: T;
  timestamp: string;
  errores?: string[];
}

interface RespuestaLogin {
  token: string;
  tokenActualizacion: string;
  usuario: {
    id: number;
    nombreUsuario: string;
    email: string;
    nombreCompleto: string;
    rol: { nombre: string };
    permisos: any[];
  };
  expiraEn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(nombreUsuarioOEmail: string, contrasena: string): Observable<RespuestaAPI<RespuestaLogin>> {
    return this.http.post<RespuestaAPI<RespuestaLogin>>(
      `${this.apiUrl}/api/v1/autenticacion/iniciar-sesion`,
      { nombreUsuarioOEmail, contrasena }
    );
  }

  register(userData: any): Observable<RespuestaAPI<any>> {
    return this.http.post<RespuestaAPI<any>>(
      `${this.apiUrl}/api/v1/autenticacion/registrar`,
      userData
    );
  }

  refreshToken(tokenActualizacion: string): Observable<RespuestaAPI<RespuestaLogin>> {
    return this.http.post<RespuestaAPI<RespuestaLogin>>(
      `${this.apiUrl}/api/v1/autenticacion/refrescar-token`,
      { tokenActualizacion }
    );
  }

  logout(): Observable<RespuestaAPI<void>> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<RespuestaAPI<void>>(
      `${this.apiUrl}/api/v1/autenticacion/cerrar-sesion`,
      {},
      { headers }
    );
  }

  // Guardar token
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Obtener token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Remover token
  removeToken(): void {
    localStorage.removeItem('token');
  }

  // Verificar si está autenticado
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

### Interceptor para JWT (Angular)
```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Agregar token si existe
    const token = this.authService.getToken();
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // Manejar errores
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token expirado o inválido
          this.authService.removeToken();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 🧪 Pruebas con curl

### Test 1: Health Check
```bash
curl http://localhost:8081/actuator/health
```
**Esperado:** `{"status":"UP"}`

### Test 2: Login
```bash
curl -X POST http://localhost:8081/api/v1/autenticacion/iniciar-sesion \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuarioOEmail":"admin","contrasena":"Admin123!"}'
```

### Test 3: Endpoint Protegido (ejemplo)
```bash
curl -X GET http://localhost:8081/api/usuarios/perfil \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🐛 Manejo de Errores

### Error 400 - Bad Request
```json
{
  "exitoso": false,
  "mensaje": "Datos de entrada inválidos",
  "datos": null,
  "timestamp": "2025-11-03T21:30:00.123Z",
  "errores": [
    "El campo 'email' es obligatorio",
    "La contraseña debe tener al menos 8 caracteres"
  ]
}
```

### Error 401 - Unauthorized
```json
{
  "exitoso": false,
  "mensaje": "Credenciales inválidas",
  "datos": null,
  "timestamp": "2025-11-03T21:30:00.123Z",
  "errores": ["Usuario o contraseña incorrectos"]
}
```

### Error 403 - Forbidden
```json
{
  "exitoso": false,
  "mensaje": "Acceso denegado",
  "datos": null,
  "timestamp": "2025-11-03T21:30:00.123Z",
  "errores": ["No tiene permisos para acceder a este recurso"]
}
```

### Error 500 - Internal Server Error
```json
{
  "exitoso": false,
  "mensaje": "Error interno del servidor",
  "datos": null,
  "timestamp": "2025-11-03T21:30:00.123Z",
  "errores": ["Ha ocurrido un error inesperado"]
}
```

---

## 🔄 CORS Configurado

El backend acepta requests de:
- ✅ `http://localhost:8080`
- ✅ `http://localhost:4200`
- ✅ `http://127.0.0.1:8080`
- ✅ `https://innoad.com`
- ✅ `https://www.innoad.com`

---

## 📊 Validaciones del Backend

### Contraseña
- Mínimo 8 caracteres
- Al menos 1 mayúscula
- Al menos 1 minúscula
- Al menos 1 número
- Al menos 1 carácter especial

### Email
- Formato válido de email
- Único en el sistema

### Nombre de Usuario
- Único en el sistema
- Sin espacios
- Alfanumérico

---

## ✅ Checklist de Integración

- [ ] Actualizar `environment.ts` con `apiUrl: 'http://localhost:8081'`
- [ ] Implementar `AuthService` con los endpoints correctos
- [ ] Crear `AuthInterceptor` para agregar JWT a headers
- [ ] Implementar guard para rutas protegidas
- [ ] Manejar refresh token antes de expiración
- [ ] Implementar logout que limpie localStorage
- [ ] Mostrar mensajes de error del backend
- [ ] Probar con usuarios de prueba

---

## 📞 Soporte

Si encuentras algún problema:
1. Verificar que el backend esté corriendo: `http://localhost:8081/actuator/health`
2. Revisar logs del backend en la consola
3. Verificar que el puerto 8081 esté libre
4. Confirmar que CORS esté permitiendo tu origen

---

## 🎯 Próximos Pasos

1. **Conectar Frontend:**
   - Actualizar URL base a `http://localhost:8081`
   - Implementar servicio de autenticación
   - Probar login con `admin/Admin123!`

2. **Probar Flujo Completo:**
   - Login → Guardar token → Request protegido → Refresh → Logout

3. **Manejo de Errores:**
   - Implementar interceptor
   - Mostrar mensajes al usuario
   - Redirigir en caso de 401

---

**Backend desplegado exitosamente** ✅  
**Fecha:** 3 de noviembre de 2025  
**Puerto:** 8081  
**Estado:** LISTO PARA INTEGRACIÓN
