package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Escolaridade;
import com.example.demo.domain.Status;
import com.example.demo.domain.TipoProduto;
import com.example.demo.dto.ProdutoCursoResponse;
import com.example.demo.dto.ProdutoResponse;
import com.example.demo.dto.ProdutoSimuladoResponse;
import com.example.demo.util.FoxUtils;

@Service
public class ProdutoService {
    
    private final JdbcTemplate jdbcTemplate;

    public ProdutoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProdutoResponse> obterProdutosNaoMatriculados(UUID usuarioId) {
        
        List<ProdutoResponse> produtos = new ArrayList<ProdutoResponse>();

        String queryCursosNaoMatriculados = """
            select c.*, b.nome from cursos c join bancas b on c.banca_id = b.id 
            where c.status = ? and not exists (select 1 from matriculas m 
            where m.produto_id = c.id and m.usuario_id = ? 
            and m.tipo_produto = ?)                
        """;

        jdbcTemplate.query(queryCursosNaoMatriculados, 
            (rs, rowNum) -> {
                
                ProdutoCursoResponse produto = new ProdutoCursoResponse();
                produto.setId(UUID.fromString(rs.getString("id")));
                produto.setTipoProduto(TipoProduto.CURSO);
                produto.setTitulo(rs.getString("titulo"));
                produto.setBanca(rs.getString("nome"));
                String escolaridadeStr = rs.getString("escolaridade");
                Escolaridade escolaridade = Escolaridade.valueOf(escolaridadeStr);
                produto.setEscolaridade(escolaridade);
                produto.setDataInicio(FoxUtils.convertLocalDateToDate
                    (rs.getObject("data_inicio", java.time.LocalDate.class)));
                produto.setDataTermino(FoxUtils.convertLocalDateToDate(
                    rs.getObject("data_termino", java.time.LocalDate.class)));
                Status status = Status.valueOf(rs.getString("status"));
                produto.setStatus(status);
                
                produtos.add(produto);

                return produto;
        }, Status.ATIVO.name(), usuarioId, TipoProduto.CURSO.name());
        
        String simuladosNaoMatriculados = """
            select s.* from simulados s  
            where not exists (select 1 from matriculas m where m.produto_id = s.id 
            and m.usuario_id = ? and m.tipo_produto = ?) 
            and not exists (select 1 from matriculas m join cursos c on m.produto_id = c.id 
            where s.curso_id = c.id and c.status = ? and m.usuario_id = ? and m.tipo_produto = ?)
        """;

        jdbcTemplate.query(simuladosNaoMatriculados, 
            (rs, rowNum) -> {

                ProdutoSimuladoResponse produto = new ProdutoSimuladoResponse();
                produto.setId(UUID.fromString(rs.getString("id")));
                produto.setTipoProduto(TipoProduto.SIMULADO);
                produto.setTitulo(rs.getString("titulo"));
                produto.setData(FoxUtils.convertLocalDateTimeToDate
                    (rs.getObject("data_inicio", java.time.LocalDateTime.class)));
                produto.setQuantidadeQuestoes(rs.getInt("quantidade_questoes"));
                produto.setDuracao(rs.getString("duracao"));

                produtos.add(produto);

                return produto;

            }, 
            usuarioId, TipoProduto.SIMULADO.name(), Status.ATIVO.name(), 
            usuarioId, TipoProduto.CURSO.name());

        return produtos;
    }

}
