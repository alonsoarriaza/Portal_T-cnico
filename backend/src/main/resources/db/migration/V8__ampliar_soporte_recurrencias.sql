-- V8__ampliar_soporte_recurrencias.sql
-- ABAXIAL PORTAL TÉCNICO: Soporte completo para motor de recurrencias y excepciones de calendario

ALTER TABLE eventos ADD COLUMN IF NOT EXISTS fecha_fin_recurrencia TIMESTAMP;
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS dias_semana VARCHAR(50);
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS dia_mes INTEGER;
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS evento_padre_id BIGINT REFERENCES eventos(id) ON DELETE CASCADE;
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS fecha_original_ocurrencia TIMESTAMP;
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS es_excepcion BOOLEAN DEFAULT FALSE;
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS fechas_excluidas TEXT;

CREATE INDEX IF NOT EXISTS idx_eventos_padre ON eventos(evento_padre_id);
CREATE INDEX IF NOT EXISTS idx_eventos_tipo ON eventos(tipo);
