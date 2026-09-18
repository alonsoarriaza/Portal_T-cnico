-- V12__ampliar_longitud_campos_contactos.sql
-- Ampliar longitud de campos en la tabla contactos para permitir números de teléfono múltiples, extensiones y nombres completos sin error de truncamiento

ALTER TABLE contactos ALTER COLUMN telefono TYPE VARCHAR(100);
ALTER TABLE contactos ALTER COLUMN telefono_fijo TYPE VARCHAR(100);
ALTER TABLE contactos ALTER COLUMN nombre TYPE VARCHAR(150);
ALTER TABLE contactos ALTER COLUMN apellidos TYPE VARCHAR(150);
ALTER TABLE contactos ALTER COLUMN cargo TYPE VARCHAR(150);
ALTER TABLE contactos ALTER COLUMN email TYPE VARCHAR(150);
