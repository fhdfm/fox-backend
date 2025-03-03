package br.com.foxconcursos.exception;


import br.com.foxconcursos.util.ErrorType;

public class ValidationException extends FoxException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message, Class<?> clazz) {
        super(ErrorType.VALIDATION, message, clazz);
    }

    public ValidationException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.VALIDATION, ErrorType.VALIDATION.getCode(), message, cause, clazz);
    }
}

