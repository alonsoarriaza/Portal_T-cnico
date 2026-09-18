package com.abaxial.portal.clients.importer.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentParserFactory {

    private final List<DocumentParser> parsers;

    public DocumentParser getParser(String filename, String contentType) {
        return parsers.stream()
                .filter(p -> p.supports(filename, contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Formato de documento no soportado para importación: " + filename));
    }
}
