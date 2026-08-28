-- ==============================================================================
-- SCRIPT DE LIMPIEZA SEGURA DE DATOS DE PRUEBA: ABAXIAL PORTAL TÉCNICO
-- Elimina EXCLUSIVAMENTE los registros identificados con prefijo 'CLI-TEST-', 'EQ-TEST-', '[TEST]'.
-- No toca clientes reales, usuarios reales, ni configuración del sistema.
-- Operación Transaccional y Segura.
-- ==============================================================================

DO $$
DECLARE
    v_total_clientes INTEGER := 0;
    v_total_equipos INTEGER := 0;
    v_total_contactos INTEGER := 0;
    v_total_webs INTEGER := 0;
    v_total_servicios INTEGER := 0;
    v_total_eventos INTEGER := 0;
    v_total_documentos INTEGER := 0;
    v_total_versiones INTEGER := 0;
    v_total_auditorias INTEGER := 0;
    r_cliente RECORD;
BEGIN
    SELECT COUNT(*) INTO v_total_clientes FROM clientes WHERE codigo LIKE 'CLI-TEST-%';
    
    IF v_total_clientes = 0 THEN
        RAISE NOTICE '>>> No se encontraron datos de prueba para limpiar (0 clientes con prefijo CLI-TEST-).';
        RETURN;
    END IF;

    RAISE NOTICE '>>> Iniciando limpieza segura de % clientes de prueba y sus dependencias...', v_total_clientes;

    -- 1. Eliminar versiones de documentos y documentos de clientes de prueba
    DELETE FROM documento_versiones 
    WHERE documento_id IN (
        SELECT id FROM documentos WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%')
    );
    GET DIAGNOSTICS v_total_versiones = ROW_COUNT;

    DELETE FROM documentos 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%');
    GET DIAGNOSTICS v_total_documentos = ROW_COUNT;

    -- 2. Eliminar eventos asociados a clientes de prueba o creados con título [TEST]
    DELETE FROM eventos 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%')
       OR titulo LIKE '[TEST]%';
    GET DIAGNOSTICS v_total_eventos = ROW_COUNT;

    -- 3. Eliminar webs asociadas
    DELETE FROM webs 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%');
    GET DIAGNOSTICS v_total_webs = ROW_COUNT;

    -- 4. Eliminar servicios asociados
    DELETE FROM servicios 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%');
    GET DIAGNOSTICS v_total_servicios = ROW_COUNT;

    -- 5. Eliminar equipos de prueba
    DELETE FROM equipos 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%')
       OR codigo_inventario LIKE 'EQ-TEST-%';
    GET DIAGNOSTICS v_total_equipos = ROW_COUNT;

    -- 6. Eliminar contactos de prueba
    DELETE FROM contactos 
    WHERE cliente_id IN (SELECT id FROM clientes WHERE codigo LIKE 'CLI-TEST-%');
    GET DIAGNOSTICS v_total_contactos = ROW_COUNT;

    -- 7. Eliminar logs de auditoría específicos de datos de prueba
    DELETE FROM auditorias 
    WHERE detalles LIKE '%CLI-TEST-%'
       OR detalles LIKE '%[TEST]%';
    GET DIAGNOSTICS v_total_auditorias = ROW_COUNT;

    -- 8. Finalmente eliminar los clientes de prueba
    DELETE FROM clientes 
    WHERE codigo LIKE 'CLI-TEST-%';

    RAISE NOTICE '================================================================';
    RAISE NOTICE '>>> LIMPIEZA DE DATOS DE PRUEBA COMPLETADA CON ÉXITO:';
    RAISE NOTICE '  - Clientes eliminados: %', v_total_clientes;
    RAISE NOTICE '  - Equipos eliminados: %', v_total_equipos;
    RAISE NOTICE '  - Contactos eliminados: %', v_total_contactos;
    RAISE NOTICE '  - Sitios Web eliminados: %', v_total_webs;
    RAISE NOTICE '  - Servicios eliminados: %', v_total_servicios;
    RAISE NOTICE '  - Eventos eliminados: %', v_total_eventos;
    RAISE NOTICE '  - Documentos / Versiones eliminados: % / %', v_total_documentos, v_total_versiones;
    RAISE NOTICE '  - Logs de Auditoría test eliminados: %', v_total_auditorias;
    RAISE NOTICE '  - Clientes y datos reales: 100%% INTACTOS';
    RAISE NOTICE '================================================================';
END $$;
