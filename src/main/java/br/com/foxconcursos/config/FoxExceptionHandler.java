package br.com.foxconcursos.config;

import java.sql.SQLException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

}
