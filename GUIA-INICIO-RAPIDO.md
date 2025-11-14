# 🚀 GUÍA RÁPIDA PARA INICIAR EL BACKEND

## ⚡ OPCIÓN MÁS SIMPLE (RECOMENDADA)

### Usando VS Code:

1. **Abre el terminal integrado de VS Code** (Ctrl + `)

2. **Asegúrate de estar en el directorio correcto:**
   ```cmd
   cd c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\BACKEND\innoadBackend
   ```

3. **Ejecuta:**
   ```cmd
   mvn spring-boot:run
   ```

4. **Espera a ver este mensaje:**
   ```
   Started InnoAdApplication in X.XXX seconds
   Tomcat started on port 8080
   ```

---

## 📋 CONFIGURACIÓN PARA TU FRONTEND

Una vez que el backend esté corriendo (verás el mensaje arriba), usa esta configuración en tu frontend Angular:

### `src/environments/environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  authUrl: 'http://localhost:8080/api/v1/auth'  // ← IMPORTANTE: Usar /auth
};
```

### Endpoints disponibles:
```
✅ POST   http://localhost:8080/api/v1/auth/login
         Body: { "nombreUsuario": "admin", "contrasena": "admin123" }

✅ POST   http://localhost:8080/api/v1/auth/registrarse
         Body: { "nombreUsuario": "...", "email": "...", "contrasena": "..." }

✅ POST   http://localhost:8080/api/v1/auth/refresh
         Body: { "refreshToken": "..." }

✅ GET    http://localhost:8080/actuator/health
         (Para verificar que el servidor está vivo)
```

---

## 🧪 PROBAR QUE FUNCIONA

Desde el navegador o Postman:

```
GET http://localhost:8080/actuator/health
```

Debe responder:
```json
{
  "status": "UP"
}
```

---

## ⚙️ CAMBIOS IMPORTANTES REALIZADOS

1. ✅ **Ruta cambiada:** `/api/v1/autenticacion/*` → `/api/v1/auth/*`
2. ✅ **Compatibilidad:** Ambas rutas funcionan (por ahora)
3. ✅ **CORS configurado:** Acepta `http://localhost:4200`
4. ✅ **Seguridad:** Endpoints públicos sin token JWT

---

## 📝 RESUMEN PARA EL FRONTEND

**Tu auth.service.ts debe apuntar a:**
- Base URL: `http://localhost:8080/api/v1`
- Auth URL: `http://localhost:8080/api/v1/auth`

**Headers necesarios:**
- `Content-Type: application/json`
- `Authorization: Bearer {token}` (solo para endpoints protegidos)

---

¡Todo listo para que tu frontend se conecte! 🎉
