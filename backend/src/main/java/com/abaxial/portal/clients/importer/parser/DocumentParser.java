package com.abaxial.portal.clients.importer.parser;

import com.abaxial.portal.clients.importer.dto.ParsedImportData;

import java.io.InputStream;

public interface DocumentParser {
    boolean supports(String filename, String contentType);
    ParsedImportData parse(InputStream inputStream, String filename);
}
