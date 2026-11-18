-- ============================================================
-- Archivo: schema.sql
-- Propósito: construir desde cero la base de datos "carmotors".
-- Conceptos clave: tablas de consulta (lookup), tablas operacionales,
-- claves foráneas, tablas de unión muchos-a-muchos, restricciones UNIQUE.
-- Justificación: se respeta el modelo entregado en el enunciado y se
-- EXTIENDE solo lo necesario para cumplir los módulos funcionales:
--   * repuestos.marca              -> "Registro detallado" pide la marca.
--   * repuestos.cantidad_reservada -> regla "Reservado" al asignar a una orden.
--   * servicios_repuestos.estado   -> distingue RESERVADO / DEDUCIDO.
--   * proveedores.telefono/email   -> "información de contacto" del proveedor.
--   * lotes                        -> trazabilidad de lotes y caducidad.
--   * tecnicos, servicios_tecnicos -> asignación de técnicos y productividad.
--   * proveedor_catalogo           -> qué repuesto suministra cada proveedor y a qué costo.
--   * evaluaciones_proveedor       -> calificación por calidad y puntualidad.
-- Orden de ejecución: 1) schema.sql  2) data.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS carmotors
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE carmotors;

-- Se eliminan en orden inverso de dependencia para poder re-ejecutar el script.
DROP TABLE IF EXISTS facturas;
DROP TABLE IF EXISTS servicios_tecnicos;
DROP TABLE IF EXISTS servicios_repuestos;
DROP TABLE IF EXISTS servicios;
DROP TABLE IF EXISTS tecnicos;
DROP TABLE IF EXISTS evaluaciones_proveedor;
DROP TABLE IF EXISTS proveedor_catalogo;
DROP TABLE IF EXISTS lotes;
DROP TABLE IF EXISTS repuestos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS vehiculos;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS mantenimiento_tipos;
DROP TABLE IF EXISTS servicio_estados;
DROP TABLE IF EXISTS repuesto_tipos;

-- ------------------------------------------------------------
-- Tablas de consulta (Lookup Tables)
-- ------------------------------------------------------------
CREATE TABLE repuesto_tipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE servicio_estados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE mantenimiento_tipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

-- ------------------------------------------------------------
-- Tablas operacionales
-- ------------------------------------------------------------
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    documento_identidad VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE vehiculos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT,
    placa VARCHAR(10) UNIQUE NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    contacto VARCHAR(255),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE repuestos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    repuesto_tipo_id INT,
    marca VARCHAR(100),
    proveedor_id INT,
    precio_compra DECIMAL(10, 2),
    precio_venta DECIMAL(10, 2),
    cantidad_stock INT NOT NULL,
    stock_minimo INT NOT NULL,
    cantidad_reservada INT NOT NULL DEFAULT 0,
    FOREIGN KEY (repuesto_tipo_id) REFERENCES repuesto_tipos(id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CHECK (cantidad_stock >= 0),
    CHECK (cantidad_reservada >= 0),
    CHECK (cantidad_reservada <= cantidad_stock)
);

-- Trazabilidad: cada ingreso de mercancía es un lote con código único.
CREATE TABLE lotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    repuesto_id INT NOT NULL,
    proveedor_id INT NOT NULL,
    fecha_ingreso DATE NOT NULL,
    fecha_caducidad DATE NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (repuesto_id) REFERENCES repuestos(id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CHECK (cantidad > 0)
);

-- Catálogo: qué repuestos ofrece cada proveedor y a qué costo.
CREATE TABLE proveedor_catalogo (
    proveedor_id INT NOT NULL,
    repuesto_id INT NOT NULL,
    costo DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (proveedor_id, repuesto_id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    FOREIGN KEY (repuesto_id) REFERENCES repuestos(id)
);

-- Evaluación de desempeño (escala 1 a 5).
CREATE TABLE evaluaciones_proveedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INT NOT NULL,
    calidad TINYINT NOT NULL,
    puntualidad TINYINT NOT NULL,
    fecha DATE NOT NULL,
    comentario VARCHAR(255),
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CHECK (calidad BETWEEN 1 AND 5),
    CHECK (puntualidad BETWEEN 1 AND 5)
);

CREATE TABLE tecnicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE servicios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id INT,
    mantenimiento_tipo_id INT,
    fecha_recepcion DATETIME NOT NULL,
    fecha_entrega DATETIME,
    descripcion_problema TEXT,
    costo_mano_obra DECIMAL(10, 2),
    estado_id INT,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id),
    FOREIGN KEY (mantenimiento_tipo_id) REFERENCES mantenimiento_tipos(id),
    FOREIGN KEY (estado_id) REFERENCES servicio_estados(id)
);

-- Unión muchos-a-muchos servicio <-> técnico.
CREATE TABLE servicios_tecnicos (
    servicio_id INT NOT NULL,
    tecnico_id INT NOT NULL,
    PRIMARY KEY (servicio_id, tecnico_id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id),
    FOREIGN KEY (tecnico_id) REFERENCES tecnicos(id)
);

-- Unión muchos-a-muchos servicio <-> repuesto.
-- estado: RESERVADO (asignado a la orden) o DEDUCIDO (orden completada).
CREATE TABLE servicios_repuestos (
    servicio_id INT,
    repuesto_id INT,
    cantidad_usada INT NOT NULL,
    precio_unitario_venta DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'RESERVADO',
    PRIMARY KEY (servicio_id, repuesto_id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id),
    FOREIGN KEY (repuesto_id) REFERENCES repuestos(id),
    CHECK (cantidad_usada > 0)
);

CREATE TABLE facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    servicio_id INT UNIQUE,
    fecha_emision DATETIME NOT NULL,
    total_repuestos DECIMAL(10, 2),
    total_mano_obra DECIMAL(10, 2),
    impuestos DECIMAL(10, 2),
    total_final DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);
