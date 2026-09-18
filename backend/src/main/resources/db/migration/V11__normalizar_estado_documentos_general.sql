-- V11__normalizar_estado_documentos_general.sql
-- Normalización del campo estado/categoría en todos los documentos a 'GENERAL'
-- Migración controlada, segura y transaccional:
-- Sin DROP TABLE, sin TRUNCATE, sin DELETE, sin modificar versiones ni archivos físicos.

UPDATE documentos
SET categoria = 'GENERAL'
WHERE categoria IS NULL OR categoria <> 'GENERAL';
