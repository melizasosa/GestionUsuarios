package com.codigo.Gestion.Usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> manejarUsuarioNoEncontradoException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("Los datos proporcionados no son válidos. Por favor, verifica la información enviada.", HttpStatus.BAD_REQUEST);
    }
}
