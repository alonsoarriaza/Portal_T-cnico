-- ==============================================================================
-- SCRIPT DE CARGA DE DATOS DE PRUEBA: ABAXIAL PORTAL TÉCNICO
-- Genera 40 Clientes realistas con sus Contactos, Equipos, Webs, Servicios y Eventos.
-- Todos los registros utilizan identificadores controlados con prefijo 'CLI-TEST-', 'EQ-TEST-', '[TEST]'.
-- Operación Transaccional e Idempotente.
-- ==============================================================================

DO $$
DECLARE
    v_existente INTEGER;
    v_admin_id BIGINT;
    v_c_id BIGINT;
    v_i INTEGER;
BEGIN
    -- 1. Verificar si ya existen los datos de prueba
    SELECT COUNT(*) INTO v_existente FROM clientes WHERE codigo LIKE 'CLI-TEST-%';
    IF v_existente > 0 THEN
        RAISE NOTICE '>>> Los datos de prueba ya existen en la base de datos (% clientes detectados). Omite inserción duplicada.', v_existente;
        RETURN;
    END IF;

    -- Obtener el ID del usuario admin para asignaciones
    SELECT id INTO v_admin_id FROM usuarios WHERE username = 'admin' LIMIT 1;
    IF v_admin_id IS NULL THEN
        SELECT id INTO v_admin_id FROM usuarios LIMIT 1;
    END IF;

    RAISE NOTICE '>>> Iniciando inserción transaccional de 40 clientes de prueba...';

    -- ==============================================================================
    -- BLOQUE 1: INSERCIÓN DE 40 CLIENTES
    -- ==============================================================================
    
    -- Cliente 1: Gran empresa industrial (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-001', 'B99000001', '[TEST] Aceros y Estructuras del Sur S.L.', 'ALTA', 'INTEGRAL', 'Polígono Industrial La Isla, Calle 4, Nave 12', 'Dos Hermanas', 'Sevilla', 'Manuel Ortega Morales', '2024-01-15', true)
    RETURNING id INTO v_c_id;
    
    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono, telefono_fijo, observaciones) VALUES
    (v_c_id, 'Manuel', 'Ortega Morales', 'Director General', 'manuel.ortega@test-empresa-01.es', '610112233', '954001122', 'Contacto principal para decisiones estratégicas'),
    (v_c_id, 'Laura', 'Sánchez Gil', 'Jefa de Planta e Informática', 'laura.sanchez@test-empresa-01.es', '620334455', '954001123', 'Contacto técnico de fábrica');

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro) VALUES
    (v_c_id, 'Portal Corporativo Aceros del Sur', 'https://www.test-empresa-01.es', 'ONLINE', 'Web corporativa con catálogo de perfiles de acero', '2023-05-10'),
    (v_c_id, 'Portal de Clientes B2B', 'https://pedidos.test-empresa-01.es', 'ONLINE', 'Extranet de pedidos industriales', '2023-11-20');

    INSERT INTO servicios (cliente_id, nombre, descripcion, estado, fecha_inicio, fecha_fin) VALUES
    (v_c_id, 'Mantenimiento de Redes e Infraestructura 24/7', 'Soporte prioritario para planta de producción y oficinas', 'ACTIVO', '2024-01-15', '2027-01-15'),
    (v_c_id, 'Ciberseguridad y Backup Cloud Inmutable', 'Copias de seguridad diarias de servidores centrales', 'ACTIVO', '2024-02-01', '2026-02-01');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, observaciones) VALUES
    (v_c_id, 'EQ-TEST-0001', 'SERVIDOR', 'Dell', 'PowerEdge R750', 'SRV-DEL-9821A', 'OPERATIVO', '2024-01-15', 'Servidor principal de virtualización Proxmox'),
    (v_c_id, 'EQ-TEST-0002', 'FIREWALL', 'Fortinet', 'FortiGate 60F', 'FTG-60F-4412B', 'OPERATIVO', '2024-01-15', 'Firewall perimetral sede central'),
    (v_c_id, 'EQ-TEST-0003', 'SWITCH', 'Cisco', 'Catalyst 2960X 48P', 'CSCO-2960-778', 'OPERATIVO', '2024-01-15', 'Switch principal sala de racks'),
    (v_c_id, 'EQ-TEST-0004', 'SOBREMESA', 'HP', 'EliteDesk 800 G9', 'HPD-800-11234', 'OPERATIVO', '2024-01-20', 'Puesto de control en cabina de pesaje'),
    (v_c_id, 'EQ-TEST-0005', 'PORTATIL', 'Lenovo', 'ThinkPad T14 Gen 4', 'LNV-T14-99881', 'OPERATIVO', '2024-02-05', 'Portátil dirección técnica');

    INSERT INTO eventos (usuario_id, cliente_id, titulo, descripcion, fecha_inicio, fecha_fin, prioridad, estado, tipo, recurrencia, dias_semana, visibilidad) VALUES
    (v_admin_id, v_c_id, '[TEST] Mantenimiento Mensual Preventivo Servidores', 'Revisión de logs, estado de discos y parches de seguridad', '2026-09-01 09:00:00+02', '2026-09-01 11:30:00+02', 'ALTA', 'PENDIENTE', 'RECURRENTE', 'MENSUAL', NULL, 'COMPARTIDO');

    -- Cliente 2: Consultora tecnológica (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-002', 'B99000002', '[TEST] Innovación Cloud & Data S.L.', 'ALTA', 'PREMIUM', 'Paseo de la Castellana 180, Planta 7', 'Madrid', 'Madrid', 'Beatriz Gómez Pardo', '2024-02-01', true)
    RETURNING id INTO v_c_id;

    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono, observaciones) VALUES
    (v_c_id, 'Beatriz', 'Gómez Pardo', 'CEO & Socia Fundadora', 'beatriz.gomez@test-empresa-02.es', '600123456', 'Gerente general'),
    (v_c_id, 'Carlos', 'Navarro Ríos', 'Director de Operaciones', 'carlos.navarro@test-empresa-02.es', '611987654', 'Responsable compras IT');

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro) VALUES
    (v_c_id, 'Plataforma Web SaaS Analytics', 'https://www.test-empresa-02.es', 'ONLINE', 'Web de captación y landing de producto SaaS', '2024-01-10');

    INSERT INTO servicios (cliente_id, nombre, descripcion, estado, fecha_inicio, fecha_fin) VALUES
    (v_c_id, 'Soporte Helpdesk Avanzado Usuarios VIP', 'Atención inmediata telefónica y remota', 'ACTIVO', '2024-02-01', '2026-02-01');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, observaciones) VALUES
    (v_c_id, 'EQ-TEST-0006', 'PORTATIL', 'Apple', 'MacBook Pro 16 M3 Max', 'APL-MBP16-332', 'OPERATIVO', '2024-02-10', 'Puesto de diseño y desarrollo principal'),
    (v_c_id, 'EQ-TEST-0007', 'PORTATIL', 'Apple', 'MacBook Air 15 M2', 'APL-MBA15-881', 'OPERATIVO', '2024-02-12', 'Puesto comercial'),
    (v_c_id, 'EQ-TEST-0008', 'NAS', 'Synology', 'DiskStation DS923+', 'SYN-DS923-019', 'OPERATIVO', '2024-02-15', 'Copia local cifrada de repositorios');

    -- Cliente 3: Cadena de Clínicas de Salud (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-003', 'B99000003', '[TEST] Clínicas Médicas San Telmo S.A.', 'ALTA', 'INTEGRAL', 'Avenida Diagonal 450', 'Barcelona', 'Barcelona', 'Dr. Ferran Puigdemont', '2023-09-10', true)
    RETURNING id INTO v_c_id;

    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono) VALUES
    (v_c_id, 'Ferran', 'Puigdemont', 'Director Médico', 'ferran.puig@test-empresa-03.es', '622334455'),
    (v_c_id, 'Marta', 'Vila Bosch', 'Coordinadora de Centros', 'marta.vila@test-empresa-03.es', '633445566');

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Portal Cita Previa Online', 'https://www.test-empresa-03.es', 'ONLINE', 'Sistema de reserva de citas para pacientes');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, observaciones) VALUES
    (v_c_id, 'EQ-TEST-0009', 'SERVIDOR', 'HP', 'ProLiant ML350 Gen10', 'HPS-ML350-001', 'OPERATIVO', '2023-09-15', 'Servidor de base de datos de pacientes'),
    (v_c_id, 'EQ-TEST-0010', 'SOBREMESA', 'Dell', 'OptiPlex 7000 Micro', 'DLO-7000-01', 'OPERATIVO', '2023-09-18', 'Puesto de recepción clínica 1'),
    (v_c_id, 'EQ-TEST-0011', 'SOBREMESA', 'Dell', 'OptiPlex 7000 Micro', 'DLO-7000-02', 'OPERATIVO', '2023-09-18', 'Puesto de recepción clínica 2'),
    (v_c_id, 'EQ-TEST-0012', 'IMPRESORA', 'Brother', 'MFC-L8690CDW', 'BRO-8690-99', 'EN_REPARACION', '2023-10-01', 'Atasco recurrente en bandeja 2');

    -- Cliente 4: Distribución Logística (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-004', 'B99000004', '[TEST] Logística y Transportes Guadalquivir S.L.', 'ALTA', 'ESTANDAR', 'Autovía A-4 Km 535', 'Carmona', 'Sevilla', 'Antonio Romero Delgado', '2023-11-05', true)
    RETURNING id INTO v_c_id;

    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono) VALUES
    (v_c_id, 'Antonio', 'Romero', 'Director de Flota', 'antonio.romero@test-empresa-04.es', '644112233');

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Seguimiento de Envíos en Tiempo Real', 'https://tracking.test-empresa-04.es', 'ONLINE', 'Plataforma GPS de envíos');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta) VALUES
    (v_c_id, 'EQ-TEST-0013', 'SOBREMESA', 'Lenovo', 'ThinkCentre M70q', 'LNV-M70-112', 'OPERATIVO', '2023-11-10'),
    (v_c_id, 'EQ-TEST-0014', 'PORTATIL', 'Dell', 'Latitude 3540', 'DLL-3540-551', 'OPERATIVO', '2023-11-10');

    -- Cliente 5: Cliente en estado BAJA (para pruebas de reactivación)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo, fecha_eliminacion)
    VALUES ('CLI-TEST-005', 'B99000005', '[TEST] Consultores Fiscales Asociados S.L. (Inactivo)', 'BAJA', 'BASICO', 'Calle Sierpes 33, 2ºA', 'Sevilla', 'Sevilla', 'Jaime Benítez Cruz', '2022-03-01', false, '2025-12-31 10:00:00+01')
    RETURNING id INTO v_c_id;

    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono, activo) VALUES
    (v_c_id, 'Jaime', 'Benítez Cruz', 'Socio Director', 'jaime.benitez@test-empresa-05.es', '655001122', false);

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, fecha_baja, activo) VALUES
    (v_c_id, 'EQ-TEST-0015', 'PORTATIL', 'HP', 'ProBook 450 G8', 'HPP-450-7761', 'BAJA', '2022-03-05', '2025-12-31', false);

    -- Cliente 6: Estudio de Arquitectura y Urbanismo (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-006', 'B99000006', '[TEST] Estudio de Arquitectura Vértice S.L.P.', 'ALTA', 'PREMIUM', 'Calle Colón 15, Ático', 'Valencia', 'Valencia', 'Elena Marín Soriano', '2024-03-12', true)
    RETURNING id INTO v_c_id;

    INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono) VALUES
    (v_c_id, 'Elena', 'Marín', 'Arquitecta Principal', 'elena.marin@test-empresa-06.es', '666123789');

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Portfolio de Proyectos Arquitectura', 'https://www.test-empresa-06.es', 'ONLINE', 'Galería de proyectos de edificación');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, observaciones) VALUES
    (v_c_id, 'EQ-TEST-0016', 'WORKSTATION', 'Dell', 'Precision 5820 Tower', 'DLP-5820-998', 'OPERATIVO', '2024-03-15', 'Estación de renderizado 3D con GPU RTX 4080'),
    (v_c_id, 'EQ-TEST-0017', 'PLOTTER', 'HP', 'DesignJet T650 36-in', 'HPD-T650-331', 'OPERATIVO', '2024-03-18', 'Plotter de planos de obra en A0');

    -- Cliente 7: Cadena Hotelera Costa del Sol (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-007', 'B99000007', '[TEST] Hoteles Costa & Sol Resorts S.A.', 'ALTA', 'INTEGRAL', 'Paseo Marítimo 120', 'Marbella', 'Málaga', 'Álvaro Cárdenas Gil', '2023-06-01', true)
    RETURNING id INTO v_c_id;

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Motor de Reservas Hoteleras', 'https://reservas.test-empresa-07.es', 'ONLINE', 'Central de reservas en 5 idiomas');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta) VALUES
    (v_c_id, 'EQ-TEST-0018', 'FIREWALL', 'WatchGuard', 'Firebox M290', 'WG-M290-771', 'OPERATIVO', '2023-06-05'),
    (v_c_id, 'EQ-TEST-0019', 'ACCESS_POINT', 'Ubiquiti', 'UniFi U6 Pro (Pack x10)', 'UBNT-U6P-01', 'OPERATIVO', '2023-06-10');

    -- Cliente 8: Bodegas y Viñedos Tradicionales (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-008', 'B99000008', '[TEST] Bodegas Ribera Alta del Duero S.A.', 'ALTA', 'ESTANDAR', 'Carretera de Peñafiel Km 12', 'Aranda de Duero', 'Burgos', 'Gonzalo de la Riva', '2023-08-20', true)
    RETURNING id INTO v_c_id;

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Tienda Online Enoturismo y Vinos', 'https://tienda.test-empresa-08.es', 'ONLINE', 'E-commerce WooCommerce');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta) VALUES
    (v_c_id, 'EQ-TEST-0020', 'TPV', 'Posiflex', 'XT-3815', 'POS-3815-11', 'OPERATIVO', '2023-08-25');

    -- Cliente 9: Centro de Formación y Academia de Idiomas (ALTA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo)
    VALUES ('CLI-TEST-009', 'B99000009', '[TEST] Instituto Superior de Idiomas y Empresas S.L.', 'ALTA', 'BOLSA_HORAS', 'Gran Vía 44, 3º', 'Bilbao', 'Bizkaia', 'Miren Larrazabal Agirre', '2024-04-05', true)
    RETURNING id INTO v_c_id;

    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion) VALUES
    (v_c_id, 'Campus Virtual Moodle', 'https://campus.test-empresa-09.es', 'ONLINE', 'Plataforma elearning');

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta) VALUES
    (v_c_id, 'EQ-TEST-0021', 'SERVIDOR', 'Dell', 'PowerEdge T340', 'DLT-340-991', 'OPERATIVO', '2024-04-10');

    -- Cliente 10: Taller Mecánico y Chapa (BAJA)
    INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo, fecha_eliminacion)
    VALUES ('CLI-TEST-010', 'B99000010', '[TEST] Talleres Mecánicos del Ebro S.L. (Baja)', 'BAJA', 'BASICO', 'Polígono Malpica, Calle E 14', 'Zaragoza', 'Zaragoza', 'Ramón Valenzuela', '2021-05-10', false, '2026-01-15 12:00:00+01')
    RETURNING id INTO v_c_id;

    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, fecha_baja, activo) VALUES
    (v_c_id, 'EQ-TEST-0022', 'SOBREMESA', 'HP', 'ProDesk 400 G6', 'HPD-400-0981', 'BAJA', '2021-05-15', '2026-01-15', false);

    -- ==============================================================================
    -- BUCLE PARA GENERAR CLIENTES 11 AL 40 CON VARIEDAD REALISTA
    -- ==============================================================================
    FOR v_i IN 11..40 LOOP
        DECLARE
            v_codigo VARCHAR(50) := 'CLI-TEST-' || LPAD(v_i::text, 3, '0');
            v_nif VARCHAR(50) := 'B99000' || LPAD(v_i::text, 3, '0');
            v_estado VARCHAR(20) := CASE WHEN v_i IN (15, 20, 25, 30, 35, 40) THEN 'BAJA' ELSE 'ALTA' END;
            v_activo BOOLEAN := CASE WHEN v_i IN (15, 20, 25, 30, 35, 40) THEN false ELSE true END;
            v_f_baja TIMESTAMP WITH TIME ZONE := CASE WHEN v_activo THEN NULL ELSE '2026-02-01 10:00:00+01'::timestamptz END;
            v_mantenimiento VARCHAR(50) := CASE (v_i % 4)
                WHEN 0 THEN 'INTEGRAL'
                WHEN 1 THEN 'PREMIUM'
                WHEN 2 THEN 'ESTANDAR'
                ELSE 'BOLSA_HORAS'
            END;
            v_ciudades TEXT[] := ARRAY['Sevilla', 'Madrid', 'Barcelona', 'Valencia', 'Málaga', 'Zaragoza', 'Murcia', 'Alicante', 'Córdoba', 'Granada', 'Valladolid', 'A Coruña'];
            v_provincias TEXT[] := ARRAY['Sevilla', 'Madrid', 'Barcelona', 'Valencia', 'Málaga', 'Zaragoza', 'Murcia', 'Alicante', 'Córdoba', 'Granada', 'Valladolid', 'A Coruña'];
            v_actividades TEXT[] := ARRAY['Consultoría', 'Distribuciones', 'Construcciones', 'Servicios Energéticos', 'Soluciones IT', 'Alimentación Gourmet', 'Farmacéutica', 'Moda y Textil', 'Ingeniería Naval', 'Publicidad'];
            v_ciudad TEXT := v_ciudades[1 + (v_i % array_length(v_ciudades, 1))];
            v_provincia TEXT := v_provincias[1 + (v_i % array_length(v_provincias, 1))];
            v_actividad TEXT := v_actividades[1 + (v_i % array_length(v_actividades, 1))];
            v_nombre TEXT := '[TEST] ' || v_actividad || ' ' || v_ciudad || ' ' || CASE WHEN (v_i % 2 = 0) THEN 'S.L.' ELSE 'S.A.' END;
            v_gerentes TEXT[] := ARRAY['Ignacio Ruiz', 'Carmen Morales', 'Santiago Ramos', 'Patricia Herrera', 'Alejandro Castro', 'Nuria Domínguez', 'David Serrano', 'Raquel Moreno'];
            v_gerente TEXT := v_gerentes[1 + (v_i % array_length(v_gerentes, 1))];
        BEGIN
            INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, gerente, fecha_alta, activo, fecha_eliminacion)
            VALUES (v_codigo, v_nif, v_nombre, v_estado, v_mantenimiento, 'Avenida Principal nº ' || (v_i * 3), v_ciudad, v_provincia, v_gerente, ('2023-01-01'::date + (v_i * 15)), v_activo, v_f_baja)
            RETURNING id INTO v_c_id;

            -- Insertar Contacto principal
            INSERT INTO contactos (cliente_id, nombre, apellidos, cargo, email, telefono, activo) VALUES
            (v_c_id, split_part(v_gerente, ' ', 1), split_part(v_gerente, ' ', 2), 'Gerencia', 'contacto@test-empresa-' || LPAD(v_i::text, 2, '0') || '.es', '6' || LPAD((v_i * 12345)::text, 8, '0'), v_activo);

            -- Insertar Web
            IF (v_i % 3 != 0) THEN
                INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, activo) VALUES
                (v_c_id, 'Web Oficial ' || v_nombre, 'https://www.test-empresa-' || LPAD(v_i::text, 2, '0') || '.es', CASE WHEN v_activo THEN 'ONLINE' ELSE 'OFFLINE' END, 'Sitio web institucional', v_activo);
            END IF;

            -- Insertar Servicio
            INSERT INTO servicios (cliente_id, nombre, descripcion, estado, fecha_inicio, activo) VALUES
            (v_c_id, 'Soporte Informático ' || v_mantenimiento, 'Contrato de mantenimiento preventivo y correctivo', CASE WHEN v_activo THEN 'ACTIVO' ELSE 'INACTIVO' END, ('2023-01-01'::date + (v_i * 15)), v_activo);

            -- Insertar 1 a 3 Equipos por cliente con códigos de inventario únicos
            INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo) VALUES
            (v_c_id, 'EQ-TEST-' || LPAD((100 + v_i * 2)::text, 4, '0'), 'SOBREMESA', 'HP', 'ProDesk 400', 'SN-HP-' || v_i || '-A', CASE WHEN v_activo THEN 'OPERATIVO' ELSE 'BAJA' END, ('2023-01-01'::date + (v_i * 15)), v_activo);

            IF (v_i % 2 = 0) THEN
                INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo) VALUES
                (v_c_id, 'EQ-TEST-' || LPAD((100 + v_i * 2 + 1)::text, 4, '0'), 'PORTATIL', 'Lenovo', 'ThinkPad L15', 'SN-LNV-' || v_i || '-B', CASE WHEN (v_i % 4 = 0) THEN 'EN_REPARACION' WHEN v_activo THEN 'OPERATIVO' ELSE 'BAJA' END, ('2023-01-01'::date + (v_i * 15)), v_activo);
            END IF;

            -- Insertar Evento en cronograma para algunos clientes
            IF (v_i % 3 = 0 AND v_activo) THEN
                INSERT INTO eventos (usuario_id, cliente_id, titulo, descripcion, fecha_inicio, fecha_fin, prioridad, estado, tipo, recurrencia, dias_semana, visibilidad) VALUES
                (v_admin_id, v_c_id, '[TEST] Visita Técnica Quincenal ' || v_ciudad, 'Revisión técnica de equipos y copias de seguridad', ('2026-09-02 10:00:00+02'::timestamptz + (v_i * interval '1 day')), ('2026-09-02 12:00:00+02'::timestamptz + (v_i * interval '1 day')), 'MEDIA', 'PENDIENTE', 'RECURRENTE', 'QUINCENAL', NULL, 'COMPARTIDO');
            END IF;
        END;
    END LOOP;

    RAISE NOTICE '>>> Carga de 40 clientes de prueba y datos asociados completada con éxito.';
END $$;
