
-- =========================
-- 1. Tablas
-- =========================
DROP DATABASE IF EXISTS tp_empleados;
CREATE DATABASE tp_empleados CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tp_empleados;

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    legal_id VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(120),
    hire_date DATE,
    area VARCHAR(50),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_legal_id_format CHECK (legal_id REGEXP '^[0-9]{7,8}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employee_files (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    file_number VARCHAR(20) NOT NULL UNIQUE,
    category VARCHAR(30),
    status ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    date_created DATE NOT NULL,
    observation VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    employee_id BIGINT UNSIGNED NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_file_employee 
        FOREIGN KEY (employee_id) 
        REFERENCES employees(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 2. Índices
-- =========================

CREATE INDEX idx_employees_legal_id ON employees(legal_id);
CREATE INDEX idx_employees_deleted ON employees(deleted);
CREATE INDEX idx_employee_files_employee_id ON employee_files(employee_id);

-- =========================
-- 3. Limpieza de datos (opcional)
--    Deja las tablas vacías antes de insertar datos de ejemplo
-- =========================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE employee_files;
TRUNCATE TABLE employees;
SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- 4. Datos de ejemplo
-- =========================

INSERT INTO employees (first_name, last_name, legal_id, email, hire_date, area, deleted) VALUES
('Juan', 'Pérez', '12345678', 'juan.perez@empresa.com', '2020-01-15', 'Sistemas', FALSE),
('María', 'González', '23456789', 'maria.gonzalez@empresa.com', '2019-03-20', 'Finanzas', FALSE),
('Carlos', 'Rodríguez', '34567890', 'carlos.rodriguez@empresa.com', '2021-06-10', 'RRHH', FALSE),
('Ana', 'Martínez', '45678901', 'ana.martinez@empresa.com', '2018-11-05', 'Operaciones', FALSE),
('Luis', 'Fernández', '56789012', 'luis.fernandez@empresa.com', '2022-02-28', 'Comercial', FALSE);

INSERT INTO employee_files (file_number, category, status, date_created, observation, deleted, employee_id) VALUES
('LEG00001', 'Senior', 'ACTIVO',   '2020-01-15', 'Desarrollador Full Stack', FALSE, 1),
('LEG00002', 'Semi-Senior', 'ACTIVO', '2019-03-20', 'Analista Financiero',     FALSE, 2),
('LEG00003', 'Junior', 'ACTIVO',   '2021-06-10', 'Asistente RRHH',           FALSE, 3),
('LEG00004', 'Senior', 'INACTIVO', '2018-11-05', 'Supervisor de Operaciones',FALSE, 4);
