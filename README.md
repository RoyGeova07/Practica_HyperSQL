# DemoHSQLDB (Java + HSQLDB JDBC)

> **Este codigo** es una app de consola en Java que demuestra **CRUD basico** contra **HyperSQL (HSQLDB)** usando **JDBC**. Carga y reaplica un `schema.sql` desde recursos, valida la existencia del esquema al iniciar y expone un **menu interactivo** para listar, insertar, actualizar, borrar y buscar empleados.

---

## Tecnologias y librerias

* **Java** (recomendado 17+)
* **JDBC** (`DriverManager`, `Connection`, `Statement`, `PreparedStatement`, `ResultSet`)
* **HSQLDB** (driver `hsqldb.jar`)
* **I/O** (`InputStream`) y **charset** (`StandardCharsets.UTF_8`)
* **BigDecimal** para manejar importes exactos (evitar problemas de `double`)

---

## Estructura del proyecto

```
DemoHSQLDB/
├─ src/
│  └─ basico/
│     └─ Curso_HSQLDB.java
└─ resources/
   └─ sql/
      └─ schema.sql
```

> En NetBeans/IDE similar: `Curso_HSQLDB.java` en `basico`, y `schema.sql` empaquetado como recurso en `/sql/schema.sql` (classpath).

---

## ¿Que hace la app?

Al ejecutar `main`, la app:

1. **Abre conexión**: `DriverManager.getConnection(URL, USER, PASS)`.
2. **Asegura el esquema**: si `EMPLEADOS` no existe → ejecuta `schema.sql` desde recursos.
3. **Muestra un menú** (1–7) para operaciones CRUD y utilidades.
4. Opción **6** permite **reaplicar** el `schema.sql` con confirmación **"RESET"** (drop/create + datos de demo).

---

## Configuración de conexión

Dentro de `Curso_HSQLDB.java`:

```java
private static final String URL = "jdbc:hsqldb:file:C:/data/CursoBD/CursoBD"; // DB en disco
private static final String USER = "SA";
private static final String PASS = "";
```

* `jdbc:hsqldb:file:` → base en **archivo** (persistente).
* Cambia la ruta para tu entorno (la carpeta se crea si no existe).

> **Tip**: Para cerrar limpio la DB en apps largas usa `SHUTDOWN` (no imprescindible aquí al ser programa corto).

---

## Menu de opciones

1. **Listar empleados**: `SELECT ... ORDER BY id`
2. **Insertar empleado**: `INSERT (nombre, puesto, salario)` con `PreparedStatement`
3. **Actualizar salario por ID**: `UPDATE ... WHERE id=?`
4. **Borrar por ID**: `DELETE ... WHERE id=?`
5. **Buscar por puesto (LIKE)**: patrón ejemplo `Analista%`
6. **Reaplicar schema.sql**: requiere escribir **`RESET`** para confirmar
7. **Salir**

Cada operación usa **`PreparedStatement`** y muestra cuantas filas afecto.

---

## Esquema y datos (`schema.sql`)

```sql
DROP TABLE IF EXISTS empleados;

CREATE TABLE empleados (
  id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
  nombre  VARCHAR(30) NOT NULL,
  puesto  VARCHAR(35),
  salario DECIMAL(10,2)
);

INSERT INTO empleados(nombre, puesto, salario) VALUES
 ('Roy Umaña','Analista de datos',39000),
 ('Bill Gates','CEO',400000),
 ('Marcelo Garrido','Junior',2300),
 ('Mondongo Perez','Especialista en Fronted',3400);

SELECT * FROM empleados ORDER BY id;

INSERT INTO empleados(nombre, puesto, salario)
VALUES ('Spider de Cornella','Analista de datos',31000);

DELETE FROM empleados WHERE id=5;
```

---

## Puntos clave

* **`DriverManager`**: ubica el driver JDBC (estando `hsqldb.jar` en el classpath) y abre conexiones.
* **`Connection`**: sesión con la BD, admite transacciones, `createStatement` / `prepareStatement`.
* **`Statement`**: ejecución de SQL *literal* (ideal para scripts). Se usa para procesar `schema.sql`.
* **`PreparedStatement`**: parámetros `?` → seguro y eficiente (protege de inyección SQL).
* **`ResultSet`**: cursor fila a fila; `next()`, `getInt`, `getString`, `getBigDecimal`.
* **`InputStream` + `StandardCharsets`**: lectura de `schema.sql` desde recursos como texto UTF‑8.
* **`BigDecimal`**: manejo de dinero/decimales exactos.

---

## Flujo interno principal

1. `AsegurarEsquema(con)` → `TablaExiste("EMPLEADOS")` en `INFORMATION_SCHEMA.TABLES`.
2. Si no existe → `EjecutarSQLScriptDesdeElRecurso(con, "/sql/schema.sql")`.
3. En ejecución de script: limpia comentarios `--`, separa por `;`, ejecuta cada sentencia y consume posibles `ResultSet`.

---

## Como ejecutar

1. Añade **hsqldb.jar** al classpath del proyecto.
2. Ajusta `URL` a tu ruta local.
3. Ejecuta la clase `basico.Curso_HSQLDB`.

**Desde linea de comandos (ejemplo):**

```bash
javac -cp lib/hsqldb.jar -d out src/basico/Curso_HSQLDB.java
java  -cp lib/hsqldb.jar;out basico.Curso_HSQLDB
```

> En Linux/Mac usa `:` en lugar de `;` para el classpath.

---

## Ejemplo de salida

```
==MENU HYPERSQL NIVEL BASICO==
1. Listar Empleados
2. Insertar Empleados
3. Actualizar salario empleado por ID
4. Borrar empleados por ID
5. Buscar empleado por puesto (LIKE)
6. Reaplicar schema.sql (DROP/CREATE/DATOS)
7. Salir
Elige una opcion:
```

Y listados tipo:

```
1 | Roy Umaña | Analista de datos | 39000.00
2 | Bill Gates | CEO | 400000.00
...
```

---

## Troubleshooting

* **No encuentra `schema.sql`**: asegúrate de que está en `resources/sql/` y se empaqueta al JAR. Ruta usada: `"/sql/schema.sql"`.
* **Bloqueos de archivos**: en modo `file:`, HSQLDB crea archivos `.properties`, `.script`, `.lck`. Cierra la app antes de borrar/mover.
* **Encoding**: el script se lee como UTF‑8; verifica que el archivo esté guardado en ese formato.
* **Tabla no existe**: `AsegurarEsquema` la creará automáticamente si falta.

---


---

## Licencia

MIT — usa este ejemplo libremente para fines académicos y demos.
