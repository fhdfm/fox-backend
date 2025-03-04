package br.com.foxconcursos.config;

import java.sql.SQLException;

import br.com.foxconcursos.exception.FoxException;
import br.com.foxconcursos.exception.NoContentException;
import br.com.foxconcursos.exception.NotFoundException;
import br.com.foxconcursos.util.ApiReturn;
import br.com.foxconcursos.util.LogUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FoxExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {

        SQLException sqlException = (SQLException) e.getCause();

        String mensagem = "Erro de integridade de dados";

        if (sqlException != null) {
            String sqlState = sqlException.getSQLState();
            switch (sqlState) {
                case "23503": // foreign key violation
                    mensagem = "Erro: O registro não pode ser excluído pois "
                        + "está sendo referenciado por outros registros";
                    break;
                case "23505": // unique constraint violation
                    mensagem = "Erro: Já existe um registro com os dados informados";
                    break;
                default:
                    mensagem = "Erro: " + sqlException.getMessage();
                    break;
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensagem);
    }


    @ExceptionHandler(FoxException.class)
    public ResponseEntity<ApiReturn<?>> handleFoxException(FoxException ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofException(ex);
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());

        LogUtil.log(ex.getClazz(), LogUtil.LogType.ERROR, ex);

        return new ResponseEntity<>(apiReturn, status);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ApiReturn<?>> handleNoContentException(NoContentException ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofNoContentException(ex);
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());

        LogUtil.log(ex.getClazz(), LogUtil.LogType.INFO, ex);

        return new ResponseEntity<>(apiReturn, status);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiReturn<?>> handleNotFoundUserException(UsernameNotFoundException ex) {
        return handleFoxException(FoxException.ofValidation("Usuário não encontrado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiReturn<?>> handleValidationException(MethodArgumentNotValidException ex) {
        return handleFoxException(FoxException.ofValidation(ex.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiReturn<?>> handleGeneralException(Exception ex) {
        ApiReturn<?> apiReturn = ApiReturn.ofException(ex);
        return new ResponseEntity<>(apiReturn, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
