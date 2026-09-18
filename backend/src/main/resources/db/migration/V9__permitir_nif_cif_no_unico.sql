-- V9__permitir_nif_cif_no_unico.sql
-- Permite que múltiples centros o delegaciones (p. ej. centros educativos públicos con CIF autonómico) compartan el mismo CIF/NIF institucional.
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_nif_cif_key;
