
BEGIN;

-- 1. Eliminar restricción de unicidad en nif_cif si existe
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_nif_cif_key;

-- 2. Limpiar todos los datos actuales
DELETE FROM documento_versiones;
DELETE FROM documentos;
DELETE FROM eventos;
DELETE FROM webs;
DELETE FROM servicios;
DELETE FROM equipos;
DELETE FROM contactos;
DELETE FROM auditorias;
DELETE FROM clientes;

-- 3. Insertar nuevos clientes y sus equipos correspondientes

  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000007', 'S4111001F', 'CEIP San Pedro Crisólogo', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-08', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000007'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000007',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-08'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000037', '28479487J', 'José Carlos Morales Martín', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-08', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000037'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000037',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-08'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000037'),
      'EQ-43000037-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000037-0001',
      'OPERATIVO',
      '2008-04-08',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000037'),
      'EQ-43000037-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000037-0002',
      'OPERATIVO',
      '2008-04-08',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000037'),
      'EQ-43000037-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000037-0003',
      'OPERATIVO',
      '2008-04-08',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000037'),
      'EQ-43000037-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000037-0004',
      'OPERATIVO',
      '2008-04-08',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000078', 'F41015967', 'S. C. A. de Viviendas de los Agentes Comerciales', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-09', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000078'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000078',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-09'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000078'),
      'EQ-43000078-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000078-0001',
      'OPERATIVO',
      '2008-04-09',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000078'),
      'EQ-43000078-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000078-0002',
      'OPERATIVO',
      '2008-04-09',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000147', 'A41395005', 'Real Betis Baloncesto S.A.D.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-10', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000147',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-10'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000147-0001',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000147-0002',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000147-0003',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000147-0004',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000147-0005',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000147-0006',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000147-0007',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000147-0008',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000147-0009',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000147-0010',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000147-0011',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000147-0012',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000147-0013',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000147-0014',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000147-0015',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000147-0016',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000147-0017',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000147-0018',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000147-0019',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-020',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000147-0020',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-021',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000147-0021',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-022',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000147-0022',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-023',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000147-0023',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-024',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000147-0024',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-025',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000147-0025',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000147'),
      'EQ-43000147-026',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000147-0026',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000175', 'R4100089D', 'Colegio Virgen Milagrosa', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-10', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000175',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-10'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000175-0001',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000175-0002',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000175-0003',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000175-0004',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000175-0005',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000175-0006',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000175-0007',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000175'),
      'EQ-43000175-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000175-0008',
      'OPERATIVO',
      '2008-04-10',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000227', 'B91408542', 'Clínica Dental Azuanlu S.L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-04-11', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000227',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-04-11'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000227-0001',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000227-0002',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000227-0003',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000227-0004',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000227-0005',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000227-0006',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000227'),
      'EQ-43000227-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000227-0007',
      'OPERATIVO',
      '2008-04-11',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000300', 'S4111001F', 'CEIP José María del Campo', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2008-10-24', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000300'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000300',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2008-10-24'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000457', 'R4100198C', 'Religiosas de María Inmaculada (CES)', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-10-30', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000457',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-10-30'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000457-0001',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0002',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000457-0003',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000457-0004',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000457-0005',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000457-0006',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000457-0007',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0008',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000457-0009',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000457-0010',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000457-0011',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000457-0012',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000457-0013',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0014',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000457-0015',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000457-0016',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000457-0017',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000457-0018',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000457-0019',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-020',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0020',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-021',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000457-0021',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-022',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000457-0022',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-023',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000457-0023',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-024',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000457-0024',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-025',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000457-0025',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-026',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0026',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-027',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43000457-0027',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-028',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43000457-0028',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-029',
      'SOBREMESA',
      'Cisco',
      'Standard Series',
      'SN-43000457-0029',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-030',
      'PORTATIL',
      'Synology',
      'Standard Series',
      'SN-43000457-0030',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-031',
      'IMPRESORA',
      'HP',
      'Standard Series',
      'SN-43000457-0031',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-032',
      'SERVIDOR',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0032',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-033',
      'SWITCH',
      'Dell',
      'Standard Series',
      'SN-43000457-0033',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-034',
      'SAI',
      'Brother',
      'Standard Series',
      'SN-43000457-0034',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-035',
      'ROUTER',
      'Cisco',
      'Standard Series',
      'SN-43000457-0035',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-036',
      'SOBREMESA',
      'Synology',
      'Standard Series',
      'SN-43000457-0036',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-037',
      'PORTATIL',
      'HP',
      'Standard Series',
      'SN-43000457-0037',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-038',
      'IMPRESORA',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0038',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-039',
      'SERVIDOR',
      'Dell',
      'Standard Series',
      'SN-43000457-0039',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-040',
      'SWITCH',
      'Brother',
      'Standard Series',
      'SN-43000457-0040',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-041',
      'SAI',
      'Cisco',
      'Standard Series',
      'SN-43000457-0041',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-042',
      'ROUTER',
      'Synology',
      'Standard Series',
      'SN-43000457-0042',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-043',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000457-0043',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-044',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0044',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-045',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000457-0045',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-046',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000457-0046',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-047',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000457-0047',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-048',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000457-0048',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-049',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000457-0049',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-050',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0050',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-051',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000457-0051',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-052',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000457-0052',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-053',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000457-0053',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-054',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000457-0054',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-055',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000457-0055',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-056',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0056',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-057',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000457-0057',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-058',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000457-0058',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-059',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000457-0059',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-060',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000457-0060',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-061',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000457-0061',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-062',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0062',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-063',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000457-0063',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-064',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000457-0064',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-065',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000457-0065',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-066',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000457-0066',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-067',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000457-0067',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-068',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000457-0068',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-069',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43000457-0069',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-070',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43000457-0070',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-071',
      'SOBREMESA',
      'Cisco',
      'Standard Series',
      'SN-43000457-0071',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000457'),
      'EQ-43000457-072',
      'PORTATIL',
      'Synology',
      'Standard Series',
      'SN-43000457-0072',
      'OPERATIVO',
      '2023-10-30',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000527', 'G41032863', 'Círculo Mercantil e Industrial de Sevilla', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2011-01-04', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000527',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2011-01-04'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000527-0001',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0002',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000527-0003',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000527-0004',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000527-0005',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000527-0006',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000527-0007',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0008',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000527-0009',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000527-0010',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000527-0011',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000527-0012',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000527-0013',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0014',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000527-0015',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000527-0016',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000527-0017',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000527-0018',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000527-0019',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-020',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0020',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-021',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000527-0021',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-022',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000527-0022',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-023',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000527-0023',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-024',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000527-0024',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-025',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000527-0025',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-026',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0026',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-027',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43000527-0027',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-028',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43000527-0028',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-029',
      'SOBREMESA',
      'Cisco',
      'Standard Series',
      'SN-43000527-0029',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-030',
      'PORTATIL',
      'Synology',
      'Standard Series',
      'SN-43000527-0030',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-031',
      'IMPRESORA',
      'HP',
      'Standard Series',
      'SN-43000527-0031',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-032',
      'SERVIDOR',
      'Lenovo',
      'Standard Series',
      'SN-43000527-0032',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000527'),
      'EQ-43000527-033',
      'SWITCH',
      'Dell',
      'Standard Series',
      'SN-43000527-0033',
      'OPERATIVO',
      '2011-01-04',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000716', 'B91813543', 'Polygroupe Europe S. L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2013-07-15', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000716',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2013-07-15'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000716-0001',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000716-0002',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000716-0003',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000716-0004',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000716-0005',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000716-0006',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000716-0007',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000716-0008',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000716-0009',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000716-0010',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000716-0011',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000716-0012',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000716-0013',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000716-0014',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000716-0015',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000716-0016',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000716-0017',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000716'),
      'EQ-43000716-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000716-0018',
      'OPERATIVO',
      '2013-07-15',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000746', 'S4111001F', 'CEIP Rico Cejudo', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2013-10-30', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000746',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2013-10-30'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000746-0001',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000746-0002',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000746-0003',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000746-0004',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000746-0005',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000746-0006',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000746-0007',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000746-0008',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000746-0009',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000746-0010',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000746-0011',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000746-0012',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000746-0013',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000746-0014',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000746-0015',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000746-0016',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000746-0017',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000746'),
      'EQ-43000746-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000746-0018',
      'OPERATIVO',
      '2013-10-30',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000780', 'R4100199A', 'Religiosas de María Inmaculada (R)', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-10-30', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000780'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000780',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-10-30'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000784', 'S4111001F', 'CEIP San Jacinto', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2014-02-10', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000784',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2014-02-10'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000784-0001',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000784-0002',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000784-0003',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000784-0004',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000784-0005',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000784-0006',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000784'),
      'EQ-43000784-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000784-0007',
      'OPERATIVO',
      '2014-02-10',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000785', 'A85723005', 'Grupo Educativo Delta S. A.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2014-03-07', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000785',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2014-03-07'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000785-0001',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000785-0002',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000785-0003',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000785-0004',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000785-0005',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000785-0006',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000785-0007',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000785-0008',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000785-0009',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000785-0010',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000785-0011',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000785-0012',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000785-0013',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000785-0014',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000785-0015',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000785-0016',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000785-0017',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000785-0018',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000785'),
      'EQ-43000785-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000785-0019',
      'OPERATIVO',
      '2014-03-07',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000883', 'S4111001F', 'CEIP Josefa Navarro Zamora', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2014-12-20', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000883',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2014-12-20'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000883-0001',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000883-0002',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000883-0003',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000883-0004',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000883-0005',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000883-0006',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000883-0007',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000883-0008',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000883-0009',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000883-0010',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000883-0011',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000883-0012',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000883-0013',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000883'),
      'EQ-43000883-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000883-0014',
      'OPERATIVO',
      '2014-12-20',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000884', 'G86992807', 'Fundación Spínola (SE)', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2010-09-17', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000884',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2010-09-17'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000884-0001',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0002',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000884-0003',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000884-0004',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000884-0005',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000884-0006',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000884-0007',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0008',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000884-0009',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000884-0010',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000884-0011',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000884-0012',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000884-0013',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0014',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000884-0015',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000884-0016',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000884-0017',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000884-0018',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000884-0019',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-020',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0020',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-021',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000884-0021',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-022',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000884-0022',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-023',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000884-0023',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-024',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000884-0024',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-025',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000884-0025',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-026',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0026',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-027',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43000884-0027',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-028',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43000884-0028',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-029',
      'SOBREMESA',
      'Cisco',
      'Standard Series',
      'SN-43000884-0029',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-030',
      'PORTATIL',
      'Synology',
      'Standard Series',
      'SN-43000884-0030',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-031',
      'IMPRESORA',
      'HP',
      'Standard Series',
      'SN-43000884-0031',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-032',
      'SERVIDOR',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0032',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-033',
      'SWITCH',
      'Dell',
      'Standard Series',
      'SN-43000884-0033',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-034',
      'SAI',
      'Brother',
      'Standard Series',
      'SN-43000884-0034',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-035',
      'ROUTER',
      'Cisco',
      'Standard Series',
      'SN-43000884-0035',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-036',
      'SOBREMESA',
      'Synology',
      'Standard Series',
      'SN-43000884-0036',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-037',
      'PORTATIL',
      'HP',
      'Standard Series',
      'SN-43000884-0037',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-038',
      'IMPRESORA',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0038',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-039',
      'SERVIDOR',
      'Dell',
      'Standard Series',
      'SN-43000884-0039',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-040',
      'SWITCH',
      'Brother',
      'Standard Series',
      'SN-43000884-0040',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-041',
      'SAI',
      'Cisco',
      'Standard Series',
      'SN-43000884-0041',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-042',
      'ROUTER',
      'Synology',
      'Standard Series',
      'SN-43000884-0042',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-043',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000884-0043',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-044',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0044',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-045',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000884-0045',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-046',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000884-0046',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-047',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000884-0047',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-048',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000884-0048',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-049',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000884-0049',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-050',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0050',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-051',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000884-0051',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-052',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000884-0052',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-053',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000884-0053',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-054',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000884-0054',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-055',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000884-0055',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-056',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0056',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-057',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000884-0057',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-058',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000884-0058',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-059',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000884-0059',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-060',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43000884-0060',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-061',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43000884-0061',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-062',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0062',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-063',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43000884-0063',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-064',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43000884-0064',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-065',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43000884-0065',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-066',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43000884-0066',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-067',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43000884-0067',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-068',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0068',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-069',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43000884-0069',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-070',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43000884-0070',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-071',
      'SOBREMESA',
      'Cisco',
      'Standard Series',
      'SN-43000884-0071',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-072',
      'PORTATIL',
      'Synology',
      'Standard Series',
      'SN-43000884-0072',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-073',
      'IMPRESORA',
      'HP',
      'Standard Series',
      'SN-43000884-0073',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-074',
      'SERVIDOR',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0074',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-075',
      'SWITCH',
      'Dell',
      'Standard Series',
      'SN-43000884-0075',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-076',
      'SAI',
      'Brother',
      'Standard Series',
      'SN-43000884-0076',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-077',
      'ROUTER',
      'Cisco',
      'Standard Series',
      'SN-43000884-0077',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-078',
      'SOBREMESA',
      'Synology',
      'Standard Series',
      'SN-43000884-0078',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-079',
      'PORTATIL',
      'HP',
      'Standard Series',
      'SN-43000884-0079',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-080',
      'IMPRESORA',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0080',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-081',
      'SERVIDOR',
      'Dell',
      'Standard Series',
      'SN-43000884-0081',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-082',
      'SWITCH',
      'Brother',
      'Standard Series',
      'SN-43000884-0082',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-083',
      'SAI',
      'Cisco',
      'Standard Series',
      'SN-43000884-0083',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-084',
      'ROUTER',
      'Synology',
      'Standard Series',
      'SN-43000884-0084',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-085',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000884-0085',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-086',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0086',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-087',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000884-0087',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-088',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000884-0088',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-089',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000884-0089',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-090',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000884-0090',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-091',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000884-0091',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-092',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0092',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-093',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000884-0093',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-094',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000884-0094',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-095',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000884-0095',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-096',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000884-0096',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-097',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43000884-0097',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-098',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43000884-0098',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-099',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43000884-0099',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-100',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43000884-0100',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000884'),
      'EQ-43000884-101',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43000884-0101',
      'OPERATIVO',
      '2010-09-17',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000891', 'B41694720', 'El Esparragal', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2015-01-21', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000891',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2015-01-21'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000891-0001',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000891-0002',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000891-0003',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000891-0004',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000891-0005',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000891-0006',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000891-0007',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000891-0008',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000891-0009',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000891-0010',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000891-0011',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000891'),
      'EQ-43000891-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000891-0012',
      'OPERATIVO',
      '2015-01-21',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000892', 'B91662254', 'Abaxial Informática S.L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2020-01-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000892'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000892',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2020-01-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000900', 'G14085674', 'Federación Andaluza de Baloncesto', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2015-02-13', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000900',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2015-02-13'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000900-0001',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43000900-0002',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43000900-0003',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43000900-0004',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43000900-0005',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43000900-0006',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43000900-0007',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43000900-0008',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43000900-0009',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43000900-0010',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43000900-0011',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000900'),
      'EQ-43000900-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43000900-0012',
      'OPERATIVO',
      '2015-02-13',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000916', 'B90195975', 'Morales Kongsvold y Asociados S.L', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2015-04-24', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000916'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000916',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2015-04-24'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000916'),
      'EQ-43000916-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43000916-0001',
      'OPERATIVO',
      '2015-04-24',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43000918', 'B91542464', 'Gubia Sevilla S. L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2018-02-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43000918'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43000918',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2018-02-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001014', 'S4111001F', 'CEIP Juan Ramón Jiménez', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2016-10-03', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001014',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2016-10-03'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'EQ-43001014-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43001014-0001',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'EQ-43001014-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43001014-0002',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'EQ-43001014-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43001014-0003',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'EQ-43001014-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43001014-0004',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001014'),
      'EQ-43001014-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43001014-0005',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001016', 'S4111001F', 'CEPR SAR Infanta Leonor', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2016-11-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001016',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2016-11-01'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43001016-0001',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43001016-0002',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43001016-0003',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43001016-0004',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43001016-0005',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43001016-0006',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43001016-0007',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43001016-0008',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-009',
      'PORTATIL',
      'Dell',
      'Standard Series',
      'SN-43001016-0009',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-010',
      'IMPRESORA',
      'Brother',
      'Standard Series',
      'SN-43001016-0010',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-011',
      'SERVIDOR',
      'Cisco',
      'Standard Series',
      'SN-43001016-0011',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-012',
      'SWITCH',
      'Synology',
      'Standard Series',
      'SN-43001016-0012',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-013',
      'SAI',
      'HP',
      'Standard Series',
      'SN-43001016-0013',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-014',
      'ROUTER',
      'Lenovo',
      'Standard Series',
      'SN-43001016-0014',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-015',
      'SOBREMESA',
      'Dell',
      'Standard Series',
      'SN-43001016-0015',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-016',
      'PORTATIL',
      'Brother',
      'Standard Series',
      'SN-43001016-0016',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-017',
      'IMPRESORA',
      'Cisco',
      'Standard Series',
      'SN-43001016-0017',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-018',
      'SERVIDOR',
      'Synology',
      'Standard Series',
      'SN-43001016-0018',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-019',
      'SWITCH',
      'HP',
      'Standard Series',
      'SN-43001016-0019',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-020',
      'SAI',
      'Lenovo',
      'Standard Series',
      'SN-43001016-0020',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-021',
      'ROUTER',
      'Dell',
      'Standard Series',
      'SN-43001016-0021',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-022',
      'SOBREMESA',
      'Brother',
      'Standard Series',
      'SN-43001016-0022',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-023',
      'PORTATIL',
      'Cisco',
      'Standard Series',
      'SN-43001016-0023',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-024',
      'IMPRESORA',
      'Synology',
      'Standard Series',
      'SN-43001016-0024',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-025',
      'SERVIDOR',
      'HP',
      'Standard Series',
      'SN-43001016-0025',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-026',
      'SWITCH',
      'Lenovo',
      'Standard Series',
      'SN-43001016-0026',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-027',
      'SAI',
      'Dell',
      'Standard Series',
      'SN-43001016-0027',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001016'),
      'EQ-43001016-028',
      'ROUTER',
      'Brother',
      'Standard Series',
      'SN-43001016-0028',
      'OPERATIVO',
      '2016-11-01',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001028', '28581215N', 'Francisco de Asís Serrano Castro', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2016-10-03', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001028',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2016-10-03'
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-001',
      'SOBREMESA',
      'HP',
      'Standard Series',
      'SN-43001028-0001',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-002',
      'PORTATIL',
      'Lenovo',
      'Standard Series',
      'SN-43001028-0002',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-003',
      'IMPRESORA',
      'Dell',
      'Standard Series',
      'SN-43001028-0003',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-004',
      'SERVIDOR',
      'Brother',
      'Standard Series',
      'SN-43001028-0004',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-005',
      'SWITCH',
      'Cisco',
      'Standard Series',
      'SN-43001028-0005',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-006',
      'SAI',
      'Synology',
      'Standard Series',
      'SN-43001028-0006',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-007',
      'ROUTER',
      'HP',
      'Standard Series',
      'SN-43001028-0007',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
    INSERT INTO equipos (cliente_id, codigo_inventario, tipo, marca, modelo, numero_serie, estado, fecha_alta, activo)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001028'),
      'EQ-43001028-008',
      'SOBREMESA',
      'Lenovo',
      'Standard Series',
      'SN-43001028-0008',
      'OPERATIVO',
      '2016-10-03',
      true
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001120', 'B41806191', 'Soluzion Iluminacion Profesional Andalucía S. L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2018-02-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001120'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001120',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2018-02-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001146', 'S4111001F', 'IES Triana', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-16', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001146'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001146',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-16'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001203', 'B80518822', 'Avante Comunicación S.L', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2020-07-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001203'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001203',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2020-07-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001207', 'S4111001F', 'CEIP Victoria Díez', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-12', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001207'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001207',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-12'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001229', 'A28165298', 'Fertiberia S.A', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2019-06-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001229'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001229',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2019-06-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001267', 'S4111001F', 'IES Cavaleri', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2020-01-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001267'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001267',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2020-01-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001380', 'S4111001F', 'IES Alixar', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-18', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001380'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001380',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-18'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001478', 'R11002971', 'Religiosas de María Reparadora Cdad. de Cádiz', 'ALTA', 'ESTANDAR', 'Sede Central', 'Cádiz', 'Cádiz', '2023-09-28', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001478'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001478',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-09-28'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001489', 'S4111001F', 'CEIP Almotamid', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-17', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001489'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001489',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-17'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001507', 'S4111001F', 'IES Juan de Mairena', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2022-01-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001507'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001507',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2022-01-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001529', '80091550584', 'Reparadoras Roma', 'ALTA', 'ESTANDAR', 'Sede Central', 'Roma', 'Roma (Italia)', '2022-05-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001529'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001529',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2022-05-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001559', 'G41105065', 'Asociación Montetabor', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-16', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001559'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001559',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-16'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001565', 'G91616219', 'Fundación Montetabor', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2023-01-16', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001565'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001565',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2023-01-16'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001631', 'B56482029', 'Aprende a Vivir Alcalá de Guadaira S. L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2020-01-01', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001631'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001631',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2020-01-01'
    );
    
  INSERT INTO clientes (codigo, nif_cif, nombre, estado, mantenimiento, direccion, poblacion, provincia, fecha_alta, activo)
  VALUES ('43001771', 'B10533909', 'Gestión Holding Aprende a vivir S. L.', 'ALTA', 'ESTANDAR', 'Sede Central', 'Sevilla', 'Sevilla', '2025-04-29', true);
  
    INSERT INTO webs (cliente_id, nombre, url, estado, descripcion, fecha_registro)
    VALUES (
      (SELECT id FROM clientes WHERE codigo = '43001771'),
      'Portal Soporte Abaxial',
      'https://soporte.abaxial.es/cli_info.php?codigo=43001771',
      'ONLINE',
      'Enlace al registro histórico en soporte.abaxial.es',
      '2025-04-29'
    );
    
COMMIT;
