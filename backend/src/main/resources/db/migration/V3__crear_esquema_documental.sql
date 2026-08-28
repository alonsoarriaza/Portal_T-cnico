-- V3__crear_esquema_documental.sql
-- ABAXIAL PORTAL TÉCNICO: Esquema Documental y Versionado

CREATE TABLE IF NOT EXISTS documentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    nombre_original VARCHAR(255) NOT NULL,
    categoria VARCHAR(100) DEFAULT 'GENERAL',
    descripcion TEXT,
    version_actual INTEGER NOT NULL DEFAULT 1,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    fecha_eliminacion TIMESTAMP WITH TIME ZONE,
    eliminado_por BIGINT REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS documento_versiones (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL REFERENCES documentos(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_interno VARCHAR(255) NOT NULL UNIQUE,
    extension VARCHAR(30) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    tamano BIGINT NOT NULL,
    ruta_storage VARCHAR(500) NOT NULL,
    usuario_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    fecha_subida TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_doc_version UNIQUE (documento_id, version)
);
