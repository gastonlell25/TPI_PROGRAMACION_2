
# TPI_PROGRAMACION_2 – Sistema de Gestión de Empleados y Legajos

Aplicación de consola en Java que implementa un sistema de gestión de empleados y sus legajos utilizando arquitectura en capas, JDBC y MySQL, desarrollada como Trabajo Práctico Integrador de Programación 2 (UTN).

---

## 1. Descripción general del proyecto

Este proyecto implementa un **sistema de consola en Java** para la gestión de:

- **Empleado (`Employee`)**: persona que trabaja en la organización.  
- **Legajo (`EmployeeFile`)**: ficha administrativa asociada a un empleado.  

La relación entre ambas entidades es **1 a 1 unidireccional**:

- Un **Empleado** puede tener como máximo **un Legajo**.
- Cada **Legajo** pertenece a un único Empleado.
- En código, `Employee` tiene una referencia a `EmployeeFile`.
- En la BD, la tabla de legajos (`employee_files`) tiene una **FK única** a `employees(id)` con `ON DELETE CASCADE`.  

Además, se aplica **baja lógica (soft delete)** mediante un campo booleano `deleted/eliminated` en ambas entidades, evitando eliminar físicamente los registros y permitiendo trazabilidad histórica.

---

## 2. Colaboradores

- Gaston Lell  
- Adriel Lopez  
- Juan Cruz Leal  
- Gabriel Lovera  

---

## 3. Objetivos académicos

El proyecto permite practicar y demostrar:

- **Arquitectura en capas (Layered Architecture)** con separación en:
  - Capa de Presentación (consola, `Main/AppMenu`).
  - Capa de Servicio (lógica de negocio, validaciones).
  - Capa DAO (acceso a datos con JDBC).
  - Capa de Entidades (modelo de dominio).  
- **Programación Orientada a Objetos (POO)**:
  - Uso de una clase base abstracta (`BaseEntity`).
  - Interfaces genéricas (`GenericDao`, `GenericService`).
  - Encapsulamiento y sobrescritura de métodos (`toString`, `equals`, `hashCode`).  
- **Persistencia con JDBC**:
  - Conexión a MySQL.
  - Patrón **DAO**.
  - Uso de `PreparedStatement` para prevenir SQL Injection.
  - Manejo de transacciones (`commit` / `rollback`).  
- **Manejo de excepciones y recursos**:
  - `try-with-resources`.
  - `TransactionManager` como `AutoCloseable`.  

---

## 4. Tecnologías y arquitectura

- **Lenguaje:** Java (recomendado **JDK 21** o superior).  
- **Base de datos:** MySQL 8.x.  
- **Persistencia:** JDBC puro (sin ORM).  
- **Patrones de diseño:**
  - DAO Pattern.
  - Service Layer.
  - Soft Delete.
  - Factory para la conexión (e.g. `DatabaseConnection`).  
- **Interfaz de usuario:** Menú por consola.

### 4.1. Estructura de paquetes (sugerida)

- `config/`  
  Manejo de la conexión a la BD (`DatabaseConnection`, lectura de propiedades).  
- `entities/`  
  Modelo de dominio: `Employee`, `EmployeeFile`, `BaseEntity`.  
- `dao/`  
  Interfaces genéricas e implementaciones JDBC concretas.  
- `service/`  
  Lógica de negocio, validaciones, orquestación de transacciones.  
- `main/`  
  Punto de entrada (`Main`) y menú de consola (`AppMenu`).  

---

## 5. Modelo de dominio

### 5.1. Entidad `Employee` (Empleado)

Atributos principales:

- `id` (heredado de `BaseEntity`).
- `firstName`.
- `lastName`.
- `legalId` (DNI / identificador legal, **único**).
- `email`.
- `hireDate`.
- `area`.
- `deleted / eliminated` (baja lógica).
- Referencia opcional a `EmployeeFile`.

### 5.2. Entidad `EmployeeFile` (Legajo)

Atributos principales:

- `id` (heredado de `BaseEntity`).
- `fileNumber` (número de legajo, **único**).
- `category`.
- `status`.
- `dateCreated`.
- `observation`.
- `deleted / eliminated` (baja lógica).
- `employeeId` (FK hacia `employees.id`).

### 5.3. Soft Delete

En lugar de eliminar registros físicamente, se utiliza un campo booleano (`deleted`/`eliminated`) para marcar la baja:

- Las consultas “normales” filtran por `deleted = FALSE`.
- Se mantiene el historial y la trazabilidad de los datos.

---

## 6. Requisitos del sistema

| Componente        | Requerido                        |
|-------------------|----------------------------------|
| Java JDK          | 21 o superior                    |
| MySQL             | 8.0 o superior                   |
| SO                | Windows / Linux / macOS          |
| IDE recomendado   | IntelliJ IDEA, NetBeans o Eclipse |
| Cliente SQL       | Workbench / DBeaver / consola    |

---

## 7. Paso a paso para ejecutar el proyecto **desde cero**

### 7.1. Clonar el repositorio

```bash
git clone <URL_DEL_REPO>
cd <carpeta_del_repo>
```

> Reemplazá `<URL_DEL_REPO>` y `<carpeta_del_repo>` según tu proyecto real.

---

### 7.2. Instalar y configurar MySQL

1. Asegurate de tener un servidor MySQL 8.x en ejecución.  
2. Crear la base de datos y usuario (si corresponde).  
   Por ejemplo:

```sql
CREATE DATABASE dbtpi3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'tpi_user'@'localhost' IDENTIFIED BY 'tpi_password';
GRANT ALL PRIVILEGES ON dbtpi3.* TO 'tpi_user'@'localhost';
FLUSH PRIVILEGES;
```

> Podés usar el usuario `root` si lo preferís, ajustando luego el archivo de configuración.

---

### 7.3. Crear esquema y tablas

En la carpeta `sql/` del proyecto se incluye un script de inicialización (por ejemplo `init.sql`) con la creación de tablas y restricciones necesarias.

En tu cliente SQL (Workbench, DBeaver o consola):

```sql
SOURCE /ruta/al/proyecto/sql/init.sql;
```

> Ajustá la ruta al archivo según la ubicación real en tu equipo.

---

### 7.4. Configurar la conexión a la base de datos

El proyecto utiliza una clase `DatabaseConnection` (en `config/`) que lee un archivo de propiedades externo, por ejemplo `config/database.properties`.

Ejemplo de `database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/dbtpi3
db.user=tpi_user
db.password=tpi_password
db.useSSL=false
db.serverTimezone=UTC
```

> Podés cambiar `dbtpi3`, usuario y contraseña según tu entorno.

---

### 7.5. Compilar el proyecto

#### Opción A: Desde el IDE (recomendado)

1. Abrir el proyecto en tu IDE (IntelliJ, NetBeans, Eclipse).  
2. Esperar a que se resuelvan las dependencias.  
3. Compilar el proyecto desde el menú **Build**.

#### Opción B: Desde línea de comandos

Si el proyecto usa Gradle/Maven, ejecutá el comando correspondiente (`./gradlew build` o `mvn clean package`).  
Si está configurado como proyecto simple, compilá las clases desde el IDE o usando `javac`.

---

### 7.6. Ejecutar la aplicación

#### Opción A: Desde el IDE

1. Configurá la clase de arranque (por ejemplo `Main.Main`).  
2. Ejecutá el proyecto desde el botón de **Run**.

#### Opción B: Desde la terminal

Suponiendo:

- Clases compiladas en `build/classes/java/main`.
- Ruta al conector MySQL: `<ruta-mysql-jar>`.

**Windows:**

```bash
java -cp "build\classes\java\main;<ruta-mysql-jar>" Main.Main
```

**Linux/macOS:**

```bash
java -cp "build/classes/java/main:<ruta-mysql-jar>" Main.Main
```

---

### 7.7. Verificar conexión a la base de datos (opcional)

Si existe una clase de prueba de conexión (por ejemplo `Main.TestConexion`), podés ejecutarla para comprobar:

```bash
java -cp "build/classes/java/main:<ruta-mysql-jar>" Main.TestConexion
```

---

## 8. Uso del sistema (menú de consola)

Al iniciar la aplicación se muestra un **menú interactivo** en consola, con opciones del estilo:

```text
========= MENU =========
1. Crear empleado
2. Listar empleados
3. Actualizar empleado
4. Eliminar empleado (baja lógica)
5. Crear legajo
6. Listar legajos
7. Actualizar legajo
8. Eliminar legajo (baja lógica)
0. Salir
```

Características principales:

- Validaciones de entrada (tipos de datos y campos obligatorios).
- Mensajes claros de éxito y error.
- Manejo de baja lógica para mantener historial.
- La aplicación no se detiene ante errores de entrada: informa el problema y permite reintentar.

---

## 9. Reglas de negocio y transacciones

La capa de servicio se encarga de:

- Validar **unicidad** de:
  - `legalId` para `Employee`.
  - `fileNumber` para `EmployeeFile`.
- Mantener la relación 1 → 1 entre Empleado y Legajo.
- Manejar transacciones:
  - `setAutoCommit(false)`.
  - `commit()` si todo se ejecuta correctamente.
  - `rollback()` ante cualquier error.

Operaciones típicamente transaccionales:

- Crear Empleado + Legajo en una sola operación.
- Eliminar Empleado y su Legajo asociado.
- Actualizar datos asegurando integridad referencial.
