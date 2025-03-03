package br.com.foxconcursos.util;

import br.com.foxconcursos.exception.FoxException;
import br.com.foxconcursos.exception.NoContentException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiReturn<T> {
    private boolean success;
    private ErrorType errorType;
    private short errorCode;
    private String error;
    private String internalException;

    @JsonProperty("return")
    private T result;

    ApiReturn(T result) {
        this.success = true;
        this.result = result;
    }

    ApiReturn(ErrorType errorType, short errorCode, String errorMessage) {
        this.success = false;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.error = errorMessage;
    }

    ApiReturn(ErrorType errorType, short errorCode, String errorMessage, Throwable internalException) {
        this(errorType, errorCode, errorMessage);
        if (internalException != null) {
            this.internalException = internalException.getMessage();
        }
    }

    ApiReturn(boolean sucess, ErrorType errorType, short errorCode, String errorMessage, Throwable internalException) {
        this(errorType, errorCode, errorMessage);
        if (internalException != null) {
            this.internalException = internalException.getMessage();
        }
        this.success = sucess;
    }

    public static <T> ApiReturn<T> of(T t) {
        return new ApiReturn<>(t);
    }

    public static ApiReturn<String> of(ErrorType errorType, short errorCode, String errorMessage, Throwable internalException) {
        return new ApiReturn<>(errorType, errorCode, errorMessage, internalException);
    }

    public static ApiReturn<String> of(ErrorType errorType, short errorCode, String errorMessage, Throwable internalException, boolean sucess) {
        return new ApiReturn<>(sucess, errorType, errorCode, errorMessage, internalException);
    }

    public static ApiReturn<String> ofException(Exception exception) {
        return of(ErrorType.EXCEPTION, ErrorType.EXCEPTION.getCode(), exception.getMessage(), exception.getCause());
    }

    public static ApiReturn<String> ofEscolaException(FoxException ex) {
        return of(ex.getErrorType(), ex.getErrorCode(), ex.getMessage(), ex);
    }

    public static ApiReturn<String> ofNoContentException(NoContentException ex) {
        return of(ex.getErrorType(), ex.getErrorCode(), ex.getMessage(), ex, true);
    }

    public boolean isSuccess() {
        return success;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public String getInternalException() {
        return internalException;
    }

    public T getResult() {
        return result;
    }
}
