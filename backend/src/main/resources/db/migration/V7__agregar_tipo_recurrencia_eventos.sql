-- V7__agregar_tipo_recurrencia_eventos.sql
-- ABAXIAL PORTAL TÉCNICO: Añadir soporte para tipos de eventos y recurrencia

ALTER TABLE eventos ADD COLUMN IF NOT EXISTS tipo VARCHAR(30) DEFAULT 'PUNTUAL';
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS recurrencia VARCHAR(50);
