package br.com.foxconcursos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Escolaridade;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.dto.ProdutoBancoQuestaoResponse;
import br.com.foxconcursos.dto.ProdutoCursoResponse;
import br.com.foxconcursos.dto.ProdutoResponse;
import br.com.foxconcursos.dto.ProdutoSimuladoResponse;
import br.com.foxconcursos.events.MatriculaBancoQuestaoEvent;
import br.com.foxconcursos.util.FoxUtils;

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
            where c.status = ? and c.data_termino >= ? and not exists (select 1 from matriculas m 
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
                produto.setValor(rs.getBigDecimal("valor"));
                Status status = Status.valueOf(rs.getString("status"));
                produto.setStatus(status);
                
                produtos.add(produto);

                return produto;
        }, Status.ATIVO.name(), LocalDate.now(), usuarioId, TipoProduto.CURSO.name());
        
        String simuladosNaoMatriculados = """
            select s.* from simulados s  
            where not exists (select 1 from matriculas m where m.produto_id = s.id 
            and m.usuario_id = ? and m.tipo_produto = ?) 
            and not exists (select 1 from matriculas m join cursos c on m.produto_id = c.id 
            where s.curso_id = c.id and c.status = ? and m.usuario_id = ? and m.tipo_produto = ?) 
            AND s.data_inicio + (to_timestamp(s.duracao, 'HH24:MI')::time) >= ?
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
                produto.setValor(rs.getBigDecimal("valor"));

                produtos.add(produto);

                return produto;

            }, 
            usuarioId, TipoProduto.SIMULADO.name(), Status.ATIVO.name(), 
            usuarioId, TipoProduto.CURSO.name(), LocalDateTime.now());

        return produtos;
    }

    public List<ProdutoResponse> obterProdutosMatriculados(UUID usuarioId) {

        List<ProdutoResponse> produtos = new ArrayList<>();
        Set<UUID> simuladosAdicionados = new HashSet<>();
        Map<UUID, ProdutoCursoResponse> cursosMap = new HashMap<>();

        // Query para obter cursos e seus simulados associados
        String queryCursosMatriculados = """
            SELECT c.*, b.nome, s.id AS simulado_id, s.titulo AS simulado_titulo, s.data_inicio AS simulado_data_inicio, 
                s.quantidade_questoes AS simulado_quantidade_questoes, s.duracao AS simulado_duracao
            FROM matriculas m
            JOIN cursos c ON m.produto_id = c.id AND m.tipo_produto = 'CURSO'
            JOIN bancas b ON c.banca_id = b.id
            LEFT JOIN simulados s ON s.curso_id = c.id
            WHERE m.usuario_id = ? AND m.status = 'ATIVO'
            AND DATE(c.data_termino) >= CURRENT_DATE
        """;

        jdbcTemplate.query(queryCursosMatriculados, (rs) -> {
            UUID cursoId = UUID.fromString(rs.getString("id"));

            // Verificar se o curso já foi adicionado ao Map
            ProdutoCursoResponse curso = cursosMap.get(cursoId);
            if (curso == null) {
                curso = new ProdutoCursoResponse();
                curso.setId(cursoId);
                curso.setTipoProduto(TipoProduto.CURSO);
                curso.setTitulo(rs.getString("titulo"));
                curso.setBanca(rs.getString("nome"));
                curso.setEscolaridade(Escolaridade.valueOf(rs.getString("escolaridade")));
                curso.setDataInicio(FoxUtils.convertLocalDateToDate(rs.getObject("data_inicio", java.time.LocalDate.class)));
                curso.setDataTermino(FoxUtils.convertLocalDateToDate(rs.getObject("data_termino", java.time.LocalDate.class)));
                curso.setStatus(Status.valueOf(rs.getString("status")));
                curso.setSimulados(new ArrayList<>()); // Inicializa a lista de simulados
                cursosMap.put(cursoId, curso); // Adiciona o curso ao Map
            }

            // Verificar se há simulados associados ao curso e adicioná-los à lista de simulados
            UUID simuladoId = rs.getObject("simulado_id", UUID.class);
            if (simuladoId != null && !simuladosAdicionados.contains(simuladoId)) {
                ProdutoSimuladoResponse simulado = new ProdutoSimuladoResponse();
                simulado.setId(simuladoId);
                simulado.setTipoProduto(TipoProduto.SIMULADO);
                simulado.setTitulo(rs.getString("simulado_titulo"));
                simulado.setData(FoxUtils.convertLocalDateTimeToDate(rs.getObject("simulado_data_inicio", java.time.LocalDateTime.class)));
                simulado.setQuantidadeQuestoes(rs.getInt("simulado_quantidade_questoes"));
                simulado.setDuracao(rs.getString("simulado_duracao"));
                curso.getSimulados().add(simulado); // Adiciona o simulado à lista de simulados do curso
                simuladosAdicionados.add(simuladoId);
            }

        }, usuarioId);

        // Adicionar os cursos ao produto final
        produtos.addAll(cursosMap.values());

        if (!produtos.isEmpty()) {
            // adiciona o banco de questões

            ProdutoResponse curso = produtos.get(0);

            Date expiraEm = ((ProdutoCursoResponse) curso).getDataTermino();

            ProdutoBancoQuestaoResponse bqReponse = 
                new ProdutoBancoQuestaoResponse(expiraEm);
            bqReponse.setId(usuarioId);
            bqReponse.setTipoProduto(TipoProduto.QUESTOES);

            ((ProdutoCursoResponse) curso).setBancoQuestao(bqReponse);

        } else {

            String sql = "SELECT * FROM banco_questao_matricula bqm " +
                        "JOIN matriculas m ON bqm.matricula_id = m.id " +
                        "WHERE m.usuario_id = ? AND bqm.fim >= CURRENT_DATE";

            jdbcTemplate.query(sql, (rs) -> {

                ProdutoResponse response = 
                    new ProdutoBancoQuestaoResponse(
                        FoxUtils.convertLocalDateTimeToDate(rs.getObject(
                            "fim", java.time.LocalDateTime.class)));
                response.setTipoProduto(TipoProduto.QUESTOES);
                
                produtos.add(response);

            }, usuarioId);


        }

        // Query para obter simulados diretamente matriculados
        String querySimuladosMatriculados = """
            SELECT s.*
            FROM matriculas m
            JOIN simulados s ON m.produto_id = s.id AND m.tipo_produto = 'SIMULADO'            
            WHERE m.usuario_id = ? AND m.status = 'ATIVO'
        """;

        jdbcTemplate.query(querySimuladosMatriculados, (rs) -> {
            UUID simuladoId = UUID.fromString(rs.getString("id"));
            if (!simuladosAdicionados.contains(simuladoId)) {
                ProdutoSimuladoResponse produto = new ProdutoSimuladoResponse();
                produto.setId(simuladoId);
                produto.setTipoProduto(TipoProduto.SIMULADO);
                produto.setTitulo(rs.getString("titulo"));
                produto.setData(FoxUtils.convertLocalDateTimeToDate(rs.getObject("data_inicio", java.time.LocalDateTime.class)));
                produto.setQuantidadeQuestoes(rs.getInt("quantidade_questoes"));
                produto.setDuracao(rs.getString("duracao"));
                produtos.add(produto);
                simuladosAdicionados.add(simuladoId);
            }
        }, usuarioId);

        return produtos;
    }

    @EventListener
    public void handleMatricularBancoQuestoesEvent(MatriculaBancoQuestaoEvent event) {
        String sql = "INSERT INTO banco_questao_matricula (matricula_id, inicio, fim) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, event.getMatriculaId(), event.getInicio(), event.getFim());
    }

    public boolean estaMatriculado(UUID cursoId, UUID usuarioId) {

        String sql = """
            SELECT count(*) as qtd
            FROM matriculas m
            JOIN cursos c ON m.produto_id = c.id AND m.tipo_produto = 'CURSO'
            WHERE m.usuario_id = ? and c.id = ? AND m.status = 'ATIVO'
            AND DATE(c.data_termino) >= CURRENT_DATE
        """;
        
        int qtd = jdbcTemplate.query(sql, (rs) -> {
            return rs.getInt("qtd");
        }, usuarioId, cursoId);

        return qtd > 0;
    }
}
