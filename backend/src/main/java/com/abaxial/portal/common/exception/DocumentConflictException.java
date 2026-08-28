package com.abaxial.portal.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DocumentConflictException extends RuntimeException {
    private final Long existingDocumentId;
    private final String existingDocumentName;
    private final int currentVersion;

    public DocumentConflictException(String message, Long existingDocumentId, String existingDocumentName, int currentVersion) {
        super(message);
        this.existingDocumentId = existingDocumentId;
        this.existingDocumentName = existingDocumentName;
        this.currentVersion = currentVersion;
    }
}
