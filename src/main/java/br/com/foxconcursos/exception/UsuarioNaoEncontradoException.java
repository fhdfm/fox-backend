package br.com.foxconcursos.exception;

import org.springframework.security.core.AuthenticationException;

public class UsuarioNaoEncontradoException extends AuthenticationException {
    public UsuarioNaoEncontradoException(String msg) {
        super(msg);
    }
}
//import br.com.foxconcursos.util.ErrorType;
//
//public class UsuarioNaoEncontradoException extends FoxException {
//    private static final long serialVersionUID = 1L;
//
//    public UsuarioNaoEncontradoException(String message, Class<?> clazz) {
//        super(ErrorType.CONFLICT, message, clazz);
//    }
//
//    public UsuarioNaoEncontradoException(String message, Throwable cause, Class<?> clazz) {
//        super(ErrorType.CONFLICT, ErrorType.CONFLICT.getCode(), message, cause, clazz);
//    }
//}
