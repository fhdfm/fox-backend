package br.com.foxconcursos.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
//        Date data = rs.getDate("data");
//        venda.setData(data);

        String tipo = rs.getString("tipo");
        venda.setTipo(tipo);

        LocalDateTime data = rs.getTimestamp("data") != null
                ? rs.getTimestamp("data").toLocalDateTime()
                : null;

        venda.setData(data.toString());

        if ("QUESTOES".equals(tipo) && data != null) {
            int periodo = rs.getInt("periodo");

            LocalDateTime expiracao = data.plusMonths(periodo);
            venda.setDataExpiracao(Date.from(expiracao.atZone(ZoneId.systemDefault()).toInstant()));
        }


        venda.setTelefone(rs.getString("telefone"));

        if ("APOSTILA".equals(tipo)) {
            venda.setEntrega(rs.getBoolean("para_entrega"));
            venda.setEnviado(rs.getBoolean("produto_enviado"));
        }

        venda.setLogradouro(rs.getString("logradouro"));
        venda.setNumero(rs.getString("numero"));
        venda.setComplemento(rs.getString("complemento"));
        venda.setBairro(rs.getString("bairro"));
        venda.setCidade(rs.getString("cidade"));
        venda.setEstado(rs.getString("estado"));
        venda.setCep(rs.getString("cep"));

        return venda;

    }

}
