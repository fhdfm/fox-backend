package br.com.foxconcursos.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import br.com.foxconcursos.dto.VendasResponse;

public class VendasRowMapper implements RowMapper<VendasResponse> {

    @Override
    public VendasResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        VendasResponse venda = new VendasResponse();
        venda.setId(rs.getObject("id", UUID.class));
        venda.setUsuarioId(rs.getObject("usuario_id", UUID.class));
        venda.setUsuarioNome(rs.getString("nome"));
        venda.setProdutoId(rs.getObject("produto_id", UUID.class));
        venda.setProdutoNome(rs.getString("titulo"));
        venda.setMercadoPagoId(rs.getString("mp_id"));
        Date data = rs.getDate("data");
        venda.setData(data);
        
        String tipo = rs.getString("tipo");
        venda.setTipo(tipo);
        
        if ("QUESTOES".equals(tipo)) {
            int periodo = rs.getInt("periodo");
            LocalDateTime expiracao = data.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            venda.setDataExpiracao(Date.from(expiracao.plusMonths(periodo).atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }

        venda.setTelefone(rs.getString("telefone"));

        if ("APOSTILA".equals(tipo)) {
            venda.setEntrega(rs.getBoolean("para_entrega"));
            venda.setEnviado(rs.getBoolean("produto_enviado"));
        }

        return venda;

    }

}
