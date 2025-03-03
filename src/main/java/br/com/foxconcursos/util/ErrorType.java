package br.com.foxconcursos.util;

public enum ErrorType {
    /**
     * Consulta realizada mas sem resultados
     */
    NO_CONTENT(204),
    /**
     * Erro de uma validação
     */
    VALIDATION(400),
    /**
     * Erro de sem autorização
     */
    UNAUTHORIZED(401),
    /**
     * Erro de não encontrado
     */
    NOT_FOUND(404),
    /**
     * Erro retornado por conflito de informação.
     */
    CONFLICT(409),
    /**
     * Exception inexperada.
     */
    EXCEPTION(500);

    private final short code;

    ErrorType(int code) {
        this.code = (short) code;
    }

    /**
     * Recupera o tipo de erro com o mesmo código recebido.
     */
    public static ErrorType getErrorTypeForCode(int code) {
        switch (code) {
            case 400:
                return VALIDATION;
            case 401:
                return UNAUTHORIZED;
            case 404:
                return NOT_FOUND;
            case 409:
                return CONFLICT;
            default:
                return EXCEPTION;
        }
    }

    public short getCode() {
        return code;
    }
}
