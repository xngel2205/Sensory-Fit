# Módulo de Autenticación Biométrica — U.are.U® 4500

Guía de arquitectura, instalación y funcionamiento del módulo biométrico
agregado al sistema **ProyectoGym**.

---

## 1. Diseño de la arquitectura (MVC por capas)

El módulo respeta una separación estricta de responsabilidades. La interfaz
gráfica nunca habla directamente con la persistencia ni con el SDK del lector:
siempre pasa por un controlador y un servicio.

```
┌──────────────────────────────────────────────────────────────────┐
│  VISTA (Swing / JFrame)                                            │
│  RegistroUsuarioFrame · CapturaHuellaFrame · ValidacionIdentidad…  │
└───────────────┬────────────────────────────────────────────────────┘
                │ usa
┌───────────────▼────────────────────────────────────────────────────┐
│  CONTROLADOR                                                         │
│  RegistrationController · AuthenticationController                   │
└───────────────┬────────────────────────────────────────────────────┘
                │ orquesta
┌───────────────▼────────────────────────────────────────────────────┐
│  SERVICIOS (lógica de negocio)                                       │
│  MemberService (validaciones) · MembershipService (estado/mensajes)  │
│  biometric.FingerprintService  ◄── interfaz (desacople del SDK)      │
│        └── DigitalPersonaFingerprintService (impl. U.are.U)          │
└──────┬──────────────────────────────────────────┬───────────────────┘
       │ usa                                       │ usa
┌──────▼───────────────────┐          ┌────────────▼─────────────────────┐
│  PERSISTENCIA (DAO)       │          │  SDK U.are.U (com.digitalpersona) │
│  MemberDao (interfaz)     │          │  Reader · Engine · Fmd · Fid      │
│   └─ FileMemberDao        │          └───────────────────────────────────┘
│      (serialización)      │
└──────┬────────────────────┘
       │ lee/escribe
┌──────▼────────────────────┐
│  data/members.dat          │
└────────────────────────────┘

           MODELO (POJOs): Member · MembershipType · MembershipStatus
           (más los existentes: User · Role · historicResult)

           config.AppContext: contenedor de dependencias (singleton)
```

---

## 2. Estructura completa de carpetas

```
ProyectoGym/
├── pom.xml                         (+ dependencia SDK + exec-maven-plugin)
├── INSTALACION_BIOMETRICO.md       (este documento)
├── lib/
│   ├── LEEME.txt
│   └── dpuareu.jar                 ← DEBES copiarlo aquí (SDK U.are.U)
├── data/                           (se crea solo en el primer guardado)
│   └── members.dat                 (datos + plantillas biométricas)
└── src/main/java/com/unicesar/proyectogym/
    ├── ProyectoGym.java            (main — actualizado)
    ├── config/
    │   └── AppContext.java         (composición de dependencias)
    ├── model/
    │   ├── Member.java             (NUEVO — entidad principal)
    │   ├── MembershipType.java     (NUEVO — enum)
    │   ├── MembershipStatus.java   (NUEVO — enum)
    │   ├── User.java               (existente — intacto)
    │   ├── Role.java               (existente — intacto)
    │   └── historicResult.java     (existente — intacto)
    ├── persistence/
    │   ├── MemberDao.java          (interfaz DAO)
    │   ├── FileMemberDao.java      (impl. con serialización)
    │   └── DataAccessException.java
    ├── service/
    │   ├── MemberService.java      (validaciones + reglas)
    │   ├── MembershipService.java  (estado y mensajes)
    │   ├── MembershipReport.java   (resultado de consulta)
    │   ├── ValidationException.java
    │   └── biometric/
    │       ├── FingerprintService.java                (interfaz)
    │       ├── DigitalPersonaFingerprintService.java  (impl. U.are.U)
    │       ├── FingerprintServiceFactory.java
    │       ├── FingerprintCapture.java
    │       ├── IdentificationResult.java
    │       └── BiometricException.java
    ├── controller/
    │   ├── RegistrationController.java
    │   └── AuthenticationController.java
    └── views/
        ├── dashboard.java                 (existente — menús cableados)
        ├── newUsuario.java                (existente — intacto)
        ├── RegistroUsuarioFrame.java      (NUEVO)
        ├── CapturaHuellaFrame.java        (NUEVO)
        ├── ValidacionIdentidadFrame.java  (NUEVO)
        └── util/
            └── DateField.java
```

---

## 3. Instalación del SDK U.are.U

### Paso 1 — Hardware y drivers
1. Instala el **DigitalPersona U.are.U SDK** (incluye drivers + librerías nativas).
2. Conecta el lector **U.are.U 4500** y verifica que Windows lo reconozca.

### Paso 2 — Copiar el jar del SDK
Copia el jar del SDK a la carpeta `lib/` del proyecto con el nombre exacto
`dpuareu.jar`:

```
C:\Program Files\DigitalPersona\U.are.U SDK\Java\Lib\dpuareu.jar
        ──►  ProyectoGym\lib\dpuareu.jar
```

> El `pom.xml` ya referencia `lib/dpuareu.jar` mediante una dependencia con
> `scope=system`. Con esto el proyecto **compila** apuntando a ese jar.

### Paso 3 (recomendado para producción) — instalar en el repositorio local
Para evitar el `scope=system`, instala el jar en tu repositorio Maven local:

```powershell
mvn install:install-file `
  -Dfile=lib/dpuareu.jar `
  -DgroupId=com.digitalpersona `
  -DartifactId=uareu `
  -Dversion=2.3.0 `
  -Dpackaging=jar
```

Luego, en `pom.xml`, reemplaza el bloque `system` por una dependencia normal:

```xml
<dependency>
    <groupId>com.digitalpersona</groupId>
    <artifactId>uareu</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Paso 4 — Librerías nativas en tiempo de ejecución
El SDK Java carga DLLs nativas. Si al ejecutar aparece
`UnsatisfiedLinkError`, agrega la ruta de las librerías nativas del SDK a la
variable `java.library.path`, por ejemplo:

```powershell
mvn exec:java "-Dexec.args=" `
  "-Djava.library.path=C:\Program Files\DigitalPersona\U.are.U SDK\Bin"
```

---

## 4. Compilar y ejecutar

> Rutas REALES confirmadas en este equipo:
> - jar del SDK: `C:\Program Files\DigitalPersona\U.are.U SDK\Windows\Lib\Java\dpuareu.jar`
>   (ya copiado a `lib\dpuareu.jar`)
> - DLLs nativas (JVM 64-bit): `C:\Program Files\DigitalPersona\U.are.U SDK\Windows\Lib\x64`

Usar el Maven incluido en NetBeans (no está en el PATH global):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$mvn = "C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd"

# Compilar
& $mvn clean compile

# Ejecutar: agrega las DLLs JNI al PATH de la sesión y lanza la app.
# (En Windows, Java localiza las librerías JNI a través del PATH.)
$env:PATH = "C:\Program Files\DigitalPersona\U.are.U SDK\Windows\Lib\x64;" + $env:PATH
& $mvn exec:java
```

> Si tu JVM fuera de 32 bits, usa la carpeta `...\Lib\win32` en lugar de `x64`.

Desde el **dashboard**:
- Menú **Usuarios → Gestión de usuarios** → formulario de registro.
- Menú **Usuarios → Membresías** → validación biométrica y consulta de membresía.

---

## 5. Flujo completo de funcionamiento

### a) Registro + captura de huella
1. `RegistroUsuarioFrame` recoge los datos y llama a `RegistrationController.register()`.
2. `MemberService.validate()` valida TODO (obligatorios, correo, teléfono,
   fechas, longitudes, ID duplicada) y lanza `ValidationException` con la lista
   de errores si algo falla.
3. `FileMemberDao.save()` persiste el usuario en `data/members.dat`.
4. Se abre `CapturaHuellaFrame`:
   - `FingerprintService.initialize()` abre el lector.
   - `capture()` obtiene la imagen (FID), valida la **calidad** y genera la
     plantilla (FMD). Se muestra la **vista previa**; se permite **reintentar**.
   - Al guardar, `RegistrationController.saveFingerprint()` asocia la plantilla
     al usuario y la persiste.

### b) Validación de identidad + consulta de membresía
1. `ValidacionIdentidadFrame` inicializa el lector.
2. Al pulsar *Identificar*, `capture()` toma la huella y
   `AuthenticationController.identify()` ejecuta una búsqueda **1:N**
   (`Engine.Identify`) contra todos los usuarios con huella.
3. Si hay coincidencia, `MembershipService.evaluate()` calcula el estado
   efectivo y el mensaje:
   - **Activa:** "Membresía Activa. Puede ingresar."
   - **Vencida:** "Membresía Vencida. Debe renovar el pago."
   - **< 5 días:** "Membresía próxima a vencer."
   - **Suspendida:** "Membresía Suspendida. Contacte a administración."
4. Se muestran nombre completo, documento, tipo de membresía, fechas y estado.

---

## 6. Persistencia

- Formato: **serialización Java** (`data/members.dat`), elegido por manejar de
  forma nativa el `byte[]` de la plantilla biométrica sin dependencias extra.
- Escritura **atómica** (archivo temporal + `move`) para evitar corrupción.
- Carga **automática** al iniciar (`AppContext` → `FileMemberDao`).
- Errores de E/S encapsulados en `DataAccessException`.

---

## 7. Notas de integración con el SDK

La clase `DigitalPersonaFingerprintService` concentra todo el uso del SDK:

| Operación              | API U.are.U                                            |
|------------------------|--------------------------------------------------------|
| Listar/abrir lector    | `UareUGlobal.GetReaderCollection()`, `Reader.Open()`   |
| Capturar               | `Reader.Capture(Fid.Format, ImageProcessing, dpi, t)`  |
| Calidad                | `Reader.CaptureResult.quality == CaptureQuality.GOOD`  |
| Crear plantilla        | `Engine.CreateFmd(Fid, Fmd.Format.ANSI_378_2004)`      |
| Reimportar plantilla   | `Importer.ImportFmd(bytes, fmt, fmt)`                  |
| Comparar 1:1           | `Engine.Compare(fmdA, 0, fmdB, 0)`                     |
| Identificar 1:N        | `Engine.Identify(probe, 0, fmds[], umbral, n)`         |
| Liberar                | `Reader.Close()`                                       |

> Umbral de coincidencia: `Engine.PROBABILITY_ONE / 100000` (FAR ≈ 1/100000).
> Ajústalo en `DigitalPersonaFingerprintService.MATCH_THRESHOLD` según el nivel
> de seguridad deseado.
```
