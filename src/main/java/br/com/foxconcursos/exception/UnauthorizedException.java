package br.com.foxconcursos.exception;

import br.com.foxconcursos.util.ErrorType;

public class UnauthorizedException extends FoxException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, message, clazz);
    }

    public UnauthorizedException(String message, Throwable cause, Class<?> clazz) {
        super(ErrorType.UNAUTHORIZED, ErrorType.UNAUTHORIZED.getCode(), message, cause, clazz);
    }
}
