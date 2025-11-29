package com.jtarcio.shrimpfarm.domain.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, Long id) {
        super(String.format("%s com ID %d não encontrado(a)", entity, id));
    }
}
