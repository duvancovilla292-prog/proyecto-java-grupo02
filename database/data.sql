-- ============================================================
-- Archivo: data.sql
-- Propósito: poblar la base "carmotors" con datos coherentes para probar
-- todos los módulos (inventario, servicios, clientes, facturación,
-- reportes y proveedores).
-- Conceptos clave: INSERT con ids explícitos para que las relaciones
-- sean legibles; los ids de las tablas de consulta coinciden con los
-- enum de Java (EstadoServicio, TipoMantenimiento).
-- Justificación de coherencia (verificable a mano):
--   * cantidad_stock = suma de lotes - unidades DEDUCIDAS en servicios.
--   * cantidad_reservada = unidades RESERVADAS en órdenes Pendiente/En Proceso.
--   * Facturas: total = (repuestos + mano de obra) * 1.19 (IVA 19 %).
--   * Hay repuestos bajo mínimo y lotes por vencer para ver las alertas.
--   * Hay vehículos con mantenimiento preventivo vencido o próximo
--     para ver los recordatorios (fecha de referencia: noviembre 2026).
-- Orden de ejecución: 1) schema.sql  2) data.sql
-- ============================================================
USE carmotors;

-- ------------------------------------------------------------
-- Tablas de consulta (ids alineados con los enum de Java)
-- ------------------------------------------------------------
INSERT INTO repuesto_tipos (id, nombre) VALUES
(1, 'Mecánico'),
(2, 'Eléctrico'),
(3, 'Lubricantes y fluidos'),
(4, 'Frenos'),
(5, 'Suspensión'),
(6, 'Carrocería');

INSERT INTO servicio_estados (id, nombre) VALUES
(1, 'Pendiente'),
(2, 'En Proceso'),
(3, 'Completado'),
(4, 'Facturado'),
(5, 'Entregado');

INSERT INTO mantenimiento_tipos (id, nombre) VALUES
(1, 'Preventivo'),
(2, 'Correctivo');

-- ------------------------------------------------------------
-- Clientes y vehículos
-- ------------------------------------------------------------
INSERT INTO clientes (id, nombre, documento_identidad, telefono, email) VALUES
(1, 'Carlos Pérez',     '1090123456', '3001112233', 'carlos.perez@example.com'),
(2, 'María Gómez',      '1090234567', '3012223344', 'maria.gomez@example.com'),
(3, 'Andrés Rojas',     '1090345678', '3023334455', 'andres.rojas@example.com'),
(4, 'Laura Martínez',   '1090456789', '3034445566', 'laura.martinez@example.com'),
(5, 'Jorge Díaz',       '1090567890', '3045556677', 'jorge.diaz@example.com'),
(6, 'Sofía Herrera',    '1090678901', '3056667788', 'sofia.herrera@example.com'),
(7, 'Pedro Castillo',   '1090789012', '3067778899', 'pedro.castillo@example.com'),
(8, 'Valentina Suárez', '1090890123', '3078889900', 'valentina.suarez@example.com');

INSERT INTO vehiculos (id, cliente_id, placa, marca, modelo) VALUES
(1,  1, 'ABC123', 'Chevrolet',  'Spark'),
(2,  1, 'XYZ789', 'Renault',    'Duster'),
(3,  2, 'DEF456', 'Mazda',      '3'),
(4,  3, 'GHI321', 'Toyota',     'Corolla'),
(5,  4, 'JKL654', 'Kia',        'Picanto'),
(6,  5, 'MNO987', 'Nissan',     'Frontier'),
(7,  6, 'PQR246', 'Volkswagen', 'Gol'),
(8,  7, 'STU135', 'Ford',       'Ranger'),
(9,  8, 'VWX864', 'Hyundai',    'Tucson'),
(10, 2, 'YZA579', 'Suzuki',     'Swift');

-- ------------------------------------------------------------
-- Proveedores, catálogo y evaluaciones
-- ------------------------------------------------------------
INSERT INTO proveedores (id, nombre, contacto, telefono, email) VALUES
(1, 'AutoPartes del Norte',    'Ricardo León',   '6075551001', 'ventas@autopartesnorte.example.com'),
(2, 'Eléctricos Santander',    'Paola Méndez',   '6075551002', 'pedidos@electricossantander.example.com'),
(3, 'Lubricantes Andinos',     'Mauricio Niño',  '6075551003', 'comercial@lubricantesandinos.example.com'),
(4, 'Frenos y Más',            'Gloria Cárdenas','6075551004', 'contacto@frenosymas.example.com'),
(5, 'Importadora Rueda Libre', 'Esteban Torres', '6075551005', 'info@ruedalibre.example.com');

-- ------------------------------------------------------------
-- Repuestos (stock y reservas coherentes con lotes y servicios)
-- ------------------------------------------------------------
INSERT INTO repuestos (id, nombre, repuesto_tipo_id, marca, proveedor_id, precio_compra, precio_venta,
                       cantidad_stock, stock_minimo, cantidad_reservada) VALUES
(1,  'Filtro de aceite',               3, 'Bosch',    3,  18000.00,  28000.00, 26, 10, 1),
(2,  'Aceite 10W-40 (litro)',          3, 'Mobil',    3,  32000.00,  45000.00, 23, 20, 4),
(3,  'Pastillas de freno delanteras',  4, 'Brembo',   4,  85000.00, 130000.00,  6,  4, 0),
(4,  'Disco de freno',                 4, 'Brembo',   4, 140000.00, 210000.00,  4,  2, 0),
(5,  'Batería 12V 60Ah',               2, 'MAC',      2, 320000.00, 420000.00,  1,  2, 0),
(6,  'Bujía de iridio',                2, 'NGK',      2,  25000.00,  38000.00, 20,  8, 0),
(7,  'Correa de distribución',         1, 'Gates',    1,  95000.00, 150000.00,  5,  2, 0),
(8,  'Bomba de agua',                  1, 'Aisin',    1, 180000.00, 260000.00,  2,  1, 0),
(9,  'Amortiguador delantero',         5, 'Monroe',   5, 210000.00, 300000.00,  4,  2, 2),
(10, 'Filtro de aire',                 1, 'Mann',     1,  22000.00,  35000.00, 14,  5, 0),
(11, 'Líquido de frenos DOT4',         3, 'Bosch',    4,  20000.00,  30000.00, 10,  4, 0),
(12, 'Refrigerante (galón)',           3, 'Prestone', 3,  35000.00,  52000.00,  8,  4, 0),
(13, 'Alternador',                     2, 'Valeo',    2, 450000.00, 620000.00,  2,  1, 1),
(14, 'Espejo retrovisor',              6, 'Genérico', 5,  60000.00,  95000.00,  1,  2, 0),
(15, 'Kit de embrague',                1, 'Valeo',    1, 380000.00, 540000.00,  2,  1, 1);

-- Lotes: suma por repuesto = stock actual + unidades ya deducidas.
INSERT INTO lotes (id, codigo, repuesto_id, proveedor_id, fecha_ingreso, fecha_caducidad, cantidad) VALUES
(1,  'LOT-2026-001', 1,  3, '2026-01-15', NULL,         30),
(2,  'LOT-2026-002', 2,  3, '2026-01-15', '2027-06-01', 40),
(3,  'LOT-2026-003', 3,  4, '2026-01-20', NULL,          8),
(4,  'LOT-2026-004', 4,  4, '2026-01-20', NULL,          4),
(5,  'LOT-2026-005', 5,  2, '2026-02-01', NULL,          2),
(6,  'LOT-2026-006', 6,  2, '2026-02-01', NULL,         24),
(7,  'LOT-2026-007', 7,  1, '2026-02-10', NULL,          5),
(8,  'LOT-2026-008', 8,  1, '2026-02-10', NULL,          3),
(9,  'LOT-2026-009', 9,  5, '2026-03-01', NULL,          4),
(10, 'LOT-2026-010', 10, 1, '2026-03-01', NULL,         15),
(11, 'LOT-2026-011', 11, 4, '2026-03-05', '2026-12-01', 12),
(12, 'LOT-2026-012', 12, 3, '2026-03-05', '2026-11-30', 10),
(13, 'LOT-2026-013', 13, 2, '2026-04-01', NULL,          2),
(14, 'LOT-2026-014', 14, 5, '2026-04-01', NULL,          1),
(15, 'LOT-2026-015', 15, 1, '2026-04-10', NULL,          2);

INSERT INTO proveedor_catalogo (proveedor_id, repuesto_id, costo) VALUES
(1, 1,  19500.00), (1, 7,  95000.00), (1, 8, 180000.00), (1, 10, 22000.00), (1, 15, 380000.00),
(2, 5, 320000.00), (2, 6,  25000.00), (2, 13, 450000.00),
(3, 1,  18000.00), (3, 2,  32000.00), (3, 11, 21000.00), (3, 12, 35000.00),
(4, 3,  85000.00), (4, 4, 140000.00), (4, 11, 20000.00),
(5, 3,  82000.00), (5, 5, 335000.00), (5, 9, 210000.00), (5, 14, 60000.00);

INSERT INTO evaluaciones_proveedor (proveedor_id, calidad, puntualidad, fecha, comentario) VALUES
(1, 4, 5, '2026-02-10', 'Entrega completa y a tiempo'),
(1, 4, 4, '2026-04-10', 'Buen empaque'),
(2, 5, 3, '2026-02-01', 'Excelente calidad, llegó dos días tarde'),
(2, 5, 4, '2026-04-01', 'Sin novedades'),
(3, 4, 5, '2026-01-15', 'Siempre puntual'),
(3, 3, 5, '2026-03-05', 'Un galón con el sello roto'),
(4, 5, 5, '2026-01-20', 'Repuestos originales'),
(4, 5, 4, '2026-03-05', 'Muy recomendado'),
(5, 3, 2, '2026-03-01', 'Retraso de una semana'),
(5, 2, 3, '2026-04-01', 'Espejo con rayones');

-- ------------------------------------------------------------
-- Técnicos
-- ------------------------------------------------------------
INSERT INTO tecnicos (id, nombre, especialidad, activo) VALUES
(1, 'Javier Ortiz',   'Motor',                  TRUE),
(2, 'Camila Ríos',    'Sistema eléctrico',      TRUE),
(3, 'Luis Fernández', 'Frenos y suspensión',    TRUE),
(4, 'Diana Pardo',    'Diagnóstico electrónico',TRUE),
(5, 'Héctor Vargas',  'Latonería',              FALSE);

-- ------------------------------------------------------------
-- Órdenes de servicio en todos los estados del ciclo de vida
-- estado_id: 1 Pendiente, 2 En Proceso, 3 Completado, 4 Facturado, 5 Entregado
-- mantenimiento_tipo_id: 1 Preventivo, 2 Correctivo
-- ------------------------------------------------------------
INSERT INTO servicios (id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, fecha_entrega,
                       descripcion_problema, costo_mano_obra, estado_id) VALUES
(1,  1,  1, '2026-03-10 08:30:00', '2026-03-10 17:00:00', 'Cambio de aceite y filtro',                  60000.00, 5),
(2,  3,  2, '2026-04-02 09:00:00', '2026-04-05 16:00:00', 'Los frenos chillan al detenerse',           120000.00, 5),
(3,  4,  1, '2026-05-15 08:00:00', '2026-05-16 12:00:00', 'Mantenimiento de los 30.000 km',             90000.00, 5),
(4,  6,  2, '2026-06-20 10:00:00', '2026-06-24 11:00:00', 'No enciende, batería descargada',            50000.00, 5),
(5,  2,  1, '2026-09-01 08:00:00', '2026-09-01 15:00:00', 'Cambio de aceite',                           60000.00, 5),
(6,  7,  2, '2026-10-05 07:30:00', NULL,                  'Recalentamiento del motor',                 180000.00, 4),
(7,  9,  1, '2026-10-20 08:00:00', NULL,                  'Revisión general y cambio de pastillas',     80000.00, 3),
(8,  5,  2, '2026-11-03 09:30:00', NULL,                  'Ruido en la suspensión delantera',          150000.00, 2),
(9,  8,  2, '2026-11-08 14:00:00', NULL,                  'La batería no carga, posible alternador',   110000.00, 2),
(10, 10, 1, '2026-11-10 08:00:00', NULL,                  'Cambio de aceite de los 10.000 km',          60000.00, 1),
(11, 1,  2, '2026-11-11 10:00:00', NULL,                  'El embrague patina en subidas',             250000.00, 1),
(12, 4,  1, '2025-10-01 08:00:00', '2025-10-01 16:00:00', 'Cambio de aceite',                           60000.00, 5);

INSERT INTO servicios_tecnicos (servicio_id, tecnico_id) VALUES
(1, 1), (2, 3), (3, 1), (3, 4), (4, 2), (5, 1), (6, 1), (6, 4),
(7, 3), (8, 3), (9, 2), (11, 1), (12, 1);

INSERT INTO servicios_repuestos (servicio_id, repuesto_id, cantidad_usada, precio_unitario_venta, estado) VALUES
(1,  1,  1,  28000.00, 'DEDUCIDO'), (1,  2, 4,  45000.00, 'DEDUCIDO'),
(2,  3,  1, 130000.00, 'DEDUCIDO'), (2, 11, 1,  30000.00, 'DEDUCIDO'),
(3,  1,  1,  28000.00, 'DEDUCIDO'), (3,  2, 4,  45000.00, 'DEDUCIDO'),
(3, 10,  1,  35000.00, 'DEDUCIDO'), (3,  6, 4,  38000.00, 'DEDUCIDO'),
(4,  5,  1, 420000.00, 'DEDUCIDO'),
(5,  1,  1,  28000.00, 'DEDUCIDO'), (5,  2, 5,  45000.00, 'DEDUCIDO'),
(6,  8,  1, 260000.00, 'DEDUCIDO'), (6, 12, 2,  52000.00, 'DEDUCIDO'),
(7,  3,  1, 130000.00, 'DEDUCIDO'), (7, 11, 1,  30000.00, 'DEDUCIDO'),
(8,  9,  2, 300000.00, 'RESERVADO'),
(9, 13,  1, 620000.00, 'RESERVADO'),
(10, 1,  1,  28000.00, 'RESERVADO'), (10, 2, 4,  45000.00, 'RESERVADO'),
(11, 15, 1, 540000.00, 'RESERVADO'),
(12, 1,  1,  28000.00, 'DEDUCIDO'), (12, 2, 4,  45000.00, 'DEDUCIDO');

-- ------------------------------------------------------------
-- Facturas de las órdenes Facturadas/Entregadas (IVA 19 %)
-- ------------------------------------------------------------
INSERT INTO facturas (id, servicio_id, fecha_emision, total_repuestos, total_mano_obra, impuestos, total_final) VALUES
(1, 1,  '2026-03-10 16:30:00', 208000.00,  60000.00,  50920.00, 318920.00),
(2, 2,  '2026-04-05 15:30:00', 160000.00, 120000.00,  53200.00, 333200.00),
(3, 3,  '2026-05-16 11:30:00', 395000.00,  90000.00,  92150.00, 577150.00),
(4, 4,  '2026-06-24 10:30:00', 420000.00,  50000.00,  89300.00, 559300.00),
(5, 5,  '2026-09-01 14:30:00', 253000.00,  60000.00,  59470.00, 372470.00),
(6, 6,  '2026-10-09 17:00:00', 364000.00, 180000.00, 103360.00, 647360.00),
(7, 12, '2025-10-01 15:30:00', 208000.00,  60000.00,  50920.00, 318920.00);
