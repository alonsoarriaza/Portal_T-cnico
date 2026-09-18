package com.abaxial.portal.clients.importer.normalizer;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class DataNormalizer {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern NON_DIGITS = Pattern.compile("\\D");

    private DataNormalizer() {}

    /**
     * Normaliza un nombre para comparación:
     * Minúsculas, sin acentos/diacríticos, espacios únicos y recortados.
     */
    public static String normalizeName(String input) {
        if (input == null) return "";
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return "";

        // Remover acentos
        String decomposed = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        String withoutAccents = DIACRITICS.matcher(decomposed).replaceAll("");

        // Minúsculas y espacios normalizados
        String normalized = MULTIPLE_SPACES.matcher(withoutAccents.toLowerCase()).replaceAll(" ").trim();
        
        // Quitar sufijos societarios habituales para comparación más tolerante si procede
        return normalized;
    }

    /**
     * Normaliza un código de cliente:
     * Mayúsculas, sin espacios externos.
     */
    public static String normalizeCode(String input) {
        if (input == null) return "";
        return input.trim().toUpperCase();
    }

    /**
     * Normaliza un CIF/NIF:
     * Mayúsculas, sin guiones ni espacios.
     */
    public static String normalizeTaxId(String input) {
        if (input == null) return "";
        return input.trim().toUpperCase().replaceAll("[\\s-]", "");
    }

    /**
     * Normaliza un teléfono:
     * Sólo dígitos.
     */
    public static String normalizePhone(String input) {
        if (input == null) return "";
        return NON_DIGITS.matcher(input).replaceAll("");
    }

    /**
     * Normaliza un email:
     * Minúsculas y sin espacios.
     */
    public static String normalizeEmail(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase();
    }

    /**
     * Normaliza referencia de equipo:
     * Mayúsculas, sin espacios.
     */
    public static String normalizeEquipmentRef(String input) {
        if (input == null) return "";
        return input.trim().toUpperCase();
    }
}
