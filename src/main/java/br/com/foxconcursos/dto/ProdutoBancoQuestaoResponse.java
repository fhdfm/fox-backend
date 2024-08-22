package br.com.foxconcursos.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import br.com.foxconcursos.util.FoxUtils;

public class ProdutoBancoQuestaoResponse extends ProdutoResponse {

    private Date expiraEm;
    private boolean expirado;

    public ProdutoBancoQuestaoResponse(Date expiraEm) {
        if (expiraEm != null) {
            LocalDateTime dataExpiracao = 
                    Instant.ofEpochMilli(expiraEm.getTime())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

            this.expiraEm = FoxUtils.convertLocalDateTimeToDate(dataExpiracao);

            if (dataExpiracao.isAfter(LocalDateTime.now())) {
                this.expirado = true;
            }
        }
    }

    public Date getExpiraEm() {
        return this.expiraEm;
    }

    public boolean isExpirado() {
        return this.expirado;
    }
    
}
