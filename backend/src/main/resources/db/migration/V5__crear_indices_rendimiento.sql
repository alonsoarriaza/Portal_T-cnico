-- V5__crear_indices_rendimiento.sql
-- ABAXIAL PORTAL TÉCNICO: Índices de Alto Rendimiento

CREATE INDEX IF NOT EXISTS idx_clientes_codigo ON clientes(codigo);
CREATE INDEX IF NOT EXISTS idx_clientes_nif_cif ON clientes(nif_cif);
CREATE INDEX IF NOT EXISTS idx_clientes_nombre ON clientes(nombre);
CREATE INDEX IF NOT EXISTS idx_clientes_estado ON clientes(estado);
CREATE INDEX IF NOT EXISTS idx_clientes_provincia ON clientes(provincia);
CREATE INDEX IF NOT EXISTS idx_clientes_activo ON clientes(activo);

CREATE INDEX IF NOT EXISTS idx_contactos_cliente_id ON contactos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_contactos_activo ON contactos(activo);

CREATE INDEX IF NOT EXISTS idx_equipos_cliente_id ON equipos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_equipos_numero_serie ON equipos(numero_serie);
CREATE INDEX IF NOT EXISTS idx_equipos_activo ON equipos(activo);

CREATE INDEX IF NOT EXISTS idx_servicios_cliente_id ON servicios(cliente_id);
CREATE INDEX IF NOT EXISTS idx_servicios_activo ON servicios(activo);

CREATE INDEX IF NOT EXISTS idx_webs_cliente_id ON webs(cliente_id);
CREATE INDEX IF NOT EXISTS idx_webs_activo ON webs(activo);

CREATE INDEX IF NOT EXISTS idx_documentos_cliente_id ON documentos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_documentos_nombre ON documentos(nombre_original);
CREATE INDEX IF NOT EXISTS idx_documentos_activo ON documentos(activo);
CREATE INDEX IF NOT EXISTS idx_doc_versiones_doc_id ON documento_versiones(documento_id);

CREATE INDEX IF NOT EXISTS idx_eventos_usuario_id ON eventos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_eventos_cliente_id ON eventos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_eventos_fecha_inicio ON eventos(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_eventos_activo ON eventos(activo);

CREATE INDEX IF NOT EXISTS idx_auditorias_usuario_id ON auditorias(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditorias_fecha ON auditorias(fecha DESC);
CREATE INDEX IF NOT EXISTS idx_auditorias_accion ON auditorias(accion);
CREATE INDEX IF NOT EXISTS idx_auditorias_entidad ON auditorias(entidad);
