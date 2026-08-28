-- V6__datos_iniciales_seed.sql
-- ABAXIAL PORTAL TÉCNICO: Roles y Permisos Iniciales

INSERT INTO roles (codigo, nombre, descripcion) VALUES
('SUPER_ADMIN', 'Super Administrador', 'Control total y administración global del sistema'),
('TECNICO', 'Técnico', 'Gestión técnica de clientes, equipos, servicios y documentación')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO permisos (codigo, nombre, categoria, descripcion) VALUES
-- Clientes
('CLIENTE_VER', 'Ver Clientes', 'CLIENTES', 'Visualizar listado y detalle de clientes'),
('CLIENTE_CREAR', 'Crear Cliente', 'CLIENTES', 'Registrar nuevos clientes en el sistema'),
('CLIENTE_EDITAR', 'Editar Cliente', 'CLIENTES', 'Modificar datos de clientes existentes'),
('CLIENTE_ELIMINAR', 'Eliminar Cliente', 'CLIENTES', 'Desactivar o archivar clientes'),

-- Contactos
('CONTACTO_VER', 'Ver Contactos', 'CONTACTOS', 'Visualizar contactos de clientes'),
('CONTACTO_GESTIONAR', 'Gestionar Contactos', 'CONTACTOS', 'Crear, editar o desactivar contactos'),

-- Equipos
('EQUIPO_VER', 'Ver Equipos', 'EQUIPOS', 'Visualizar parque y detalle de equipos'),
('EQUIPO_CREAR', 'Crear Equipo', 'EQUIPOS', 'Añadir nuevos equipos al inventario'),
('EQUIPO_EDITAR', 'Editar Equipo', 'EQUIPOS', 'Modificar información de equipos'),
('EQUIPO_ELIMINAR', 'Eliminar Equipo', 'EQUIPOS', 'Desactivar o dar de baja equipos'),

-- Servicios
('SERVICIO_VER', 'Ver Servicios', 'SERVICIOS', 'Visualizar servicios asignados'),
('SERVICIO_GESTIONAR', 'Gestionar Servicios', 'SERVICIOS', 'Crear, editar o desactivar servicios'),

-- Webs
('WEB_VER', 'Ver Webs', 'WEBS', 'Visualizar webs y dominios gestionados'),
('WEB_GESTIONAR', 'Gestionar Webs', 'WEBS', 'Crear, editar o desactivar páginas web'),

-- Documentos
('DOCUMENTO_VER', 'Ver Documentos', 'DOCUMENTOS', 'Visualizar documentación de clientes'),
('DOCUMENTO_SUBIR', 'Subir Documentos', 'DOCUMENTOS', 'Subir nuevos documentos y versiones'),
('DOCUMENTO_DESCARGAR', 'Descargar Documentos', 'DOCUMENTOS', 'Descargar archivos y versiones'),
('DOCUMENTO_ELIMINAR', 'Eliminar Documentos', 'DOCUMENTOS', 'Desactivar o eliminar documentos'),

-- Cronograma
('CRONOGRAMA_VER', 'Ver Cronograma', 'CRONOGRAMA', 'Visualizar eventos y calendario'),
('CRONOGRAMA_GESTIONAR', 'Gestionar Cronograma', 'CRONOGRAMA', 'Crear, modificar y completar eventos'),

-- Historial y Auditoría
('HISTORIAL_VER', 'Ver Auditoría e Historial', 'AUDITORIA', 'Consultar logs de auditoría y actividad'),

-- Usuarios, Roles y Permisos
('USUARIO_VER', 'Ver Usuarios', 'SEGURIDAD', 'Visualizar usuarios del portal'),
('USUARIO_GESTIONAR', 'Gestionar Usuarios', 'SEGURIDAD', 'Crear, editar, activar y desactivar usuarios'),
('ROL_GESTIONAR', 'Gestionar Roles', 'SEGURIDAD', 'Crear y configurar roles'),
('PERMISO_GESTIONAR', 'Gestionar Permisos', 'SEGURIDAD', 'Asignar permisos a roles')
ON CONFLICT (codigo) DO NOTHING;

-- Asignar TODOS los permisos a SUPER_ADMIN
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.codigo = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- Asignar permisos por defecto a TECNICO
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM roles r, permisos p
WHERE r.codigo = 'TECNICO'
AND p.codigo IN (
    'CLIENTE_VER', 'CLIENTE_CREAR', 'CLIENTE_EDITAR',
    'CONTACTO_VER', 'CONTACTO_GESTIONAR',
    'EQUIPO_VER', 'EQUIPO_CREAR', 'EQUIPO_EDITAR',
    'SERVICIO_VER', 'SERVICIO_GESTIONAR',
    'WEB_VER', 'WEB_GESTIONAR',
    'DOCUMENTO_VER', 'DOCUMENTO_SUBIR', 'DOCUMENTO_DESCARGAR',
    'CRONOGRAMA_VER', 'CRONOGRAMA_GESTIONAR'
)
ON CONFLICT DO NOTHING;
