CREATE TABLE bancas (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    status VARCHAR(50) NOT NULL,
    telefone VARCHAR(20) NULL;
);

CREATE TABLE disciplinas (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE cursos (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    data_inicio DATE NOT NULL,
    data_termino DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    banca UUID REFERENCES bancas(id) NOT NULL,
    escolaridade VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NULL,
    valor NUMERIC(10,2) NOT NULL
);

CREATE TABLE simulados (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    curso_id UUID NOT NULL,
    alternativas_por_questao INTEGER NOT NULL,
    data_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    duracao VARCHAR(50) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    quantidade_questoes INTEGER NULL,
    version INT NOT NULL DEFAULT 0
    FOREIGN KEY (curso_id) REFERENCES cursos (id)
);


CREATE TABLE curso_disciplina (
    curso_id UUID NOT NULL,
    disciplina_id UUID NOT NULL,
    PRIMARY KEY (curso_id, disciplina_id),
    FOREIGN KEY (curso_id) REFERENCES cursos(id),
    FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id)
);

CREATE TABLE questoes_simulado (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    ordem INTEGER NOT NULL,
    simulado_id UUID NOT NULL,
    enunciado TEXT NOT NULL,
    disciplina_id UUID NOT NULL,
    anulada BOOLEAN NOT NULL DEFAULT false,
    version INT NOT NULL DEFAULT 0
    FOREIGN KEY (simulado_id) REFERENCES simulados(id),
    FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id)
);

CREATE TABLE itens_questao_simulado (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    questao_simulado_id UUID NOT NULL,
    ordem INTEGER NOT NULL,
    descricao TEXT NOT NULL,
    correta BOOLEAN NOT NULL,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (questao_simulado_id) REFERENCES questoes_simulado(id)
);

CREATE TABLE respostas_simulado (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    simulado_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    acertos INTEGER,
    acertos_ultimas_15 INTEGER,
    status VARCHAR(20) NOT NULL,
    version INT DEFAULT 0,
    FOREIGN KEY (simulado_id) REFERENCES simulados(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE respostas_simulado_questao (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    simuladoId UUID NOT NULL,
    questaoId UUID NOT NULL,
    itemQuestaoId UUID NOT NULL,
    correta BOOLEAN NOT NULL,
    version INT DEFAULT 0,
    FOREIGN KEY (simuladoId) REFERENCES respostas_simulado(id),
    FOREIGN KEY (questaoId) REFERENCES questoes_simulado(id),
    FOREIGN KEY (itemQuestaoId) REFERENCES itens_questao_simulado(id)
);

CREATE TABLE curso_simulado_key (
    id UUID NOT NULL UNIQUE
);

-- Trigger para inserir e deletar id na tabela curso_simulado_key
CREATE OR REPLACE FUNCTION inserir_id()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO curso_simulado_key (id)
    VALUES (NEW.id);  
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_insert_cursos
AFTER INSERT ON cursos
FOR EACH ROW
EXECUTE FUNCTION inserir_id();

CREATE TRIGGER trigger_insert_simulados
AFTER INSERT ON simulados
FOR EACH ROW
EXECUTE FUNCTION inserir_id();

CREATE OR REPLACE FUNCTION deletar_id()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM curso_simulado_key WHERE id = OLD.id;  -- Supondo que o ID do curso ou simulado é "id"
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_delete_cursos
AFTER DELETE ON cursos
FOR EACH ROW
EXECUTE FUNCTION deletar_id();

CREATE TRIGGER trigger_delete_simulados
AFTER DELETE ON simulados
FOR EACH ROW
EXECUTE FUNCTION deletar_id();

-- fim da trigger para inserir e deletar id na tabela curso_simulado_key

CREATE TABLE transacoes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    transaction_id VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE matriculas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    usuario_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    transacao_id UUID NOT NULL,
    tipo_produto VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (produto_id) REFERENCES curso_simulado_key(id),
    FOREIGN KEY (transacao_id) REFERENCES transacoes(id)
);

CREATE TABLE recuperar_password (
    token TEXT NOT NULL PRIMARY KEY,
    usuario_id UUID NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE recursos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    usuario_id UUID NOT NULL,
    questao_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, -- Adapte o tamanho conforme necessário
    fundamentacao TEXT NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (questao_id) REFERENCES questoes_simulado(id)
);

CREATE TABLE tarefas_agendadas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    data_execucao TIMESTAMP NOT NULL,
    target_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0
);

CREATE TABLE questoes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    enunciado TEXT NOT NULL,
    alternativa_correta CHAR(1),
    disciplina_id UUID NOT NULL,
    assunto_id UUID NOT NULL,
    banca_id UUID NOT NULL,
    instituicao_id UUID NOT NULL,
    cargo_id UUID NOT NULL,
    ano INT NOT NULL,
    uf VARCHAR(2) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    escolaridade VARCHAR(50) NOT NULL,
    status CHAR(1) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_disciplina
        FOREIGN KEY(disciplina_id) 
        REFERENCES disciplinas(id),
    CONSTRAINT fk_assunto
        FOREIGN KEY(assunto_id) 
        REFERENCES assunto(id),
    CONSTRAINT fk_banca
        FOREIGN KEY(banca_id) 
        REFERENCES bancas(id),
    CONSTRAINT fk_instituicao
        FOREIGN KEY(instituicao_id) 
        REFERENCES instituicao(id),
    CONSTRAINT fk_cargo
        FOREIGN KEY(cargo_id) 
        REFERENCES cargo(id)
);


CREATE TABLE alternativas (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() NOT null UNIQUE,
    questao_id UUID NOT NULL,
    letra VARCHAR(1) NOT NULL,
    descricao TEXT NOT NULL,
    correta BOOLEAN NOT NULL,
    version INTEGER NOT NULL,
    CONSTRAINT fk_questao
        FOREIGN KEY (questao_id) 
        REFERENCES questoes (id)
);

CREATE TABLE comentarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    questao_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    descricao TEXT NOT NULL,
    data TIMESTAMP NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_questao
        FOREIGN KEY(questao_id) 
        REFERENCES questoes(id),
    CONSTRAINT fk_usuario
        FOREIGN KEY(usuario_id) 
        REFERENCES usuarios(id)
);

CREATE TABLE respostas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    questaoId UUID NOT NULL,
    alternativaId UUID NOT NULL,
    usuarioId UUID NOT NULL,
    acerto BOOLEAN NOT NULL,
    data TIMESTAMP NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_questao FOREIGN KEY (questaoId) REFERENCES questoes(id),
    CONSTRAINT fk_alternativa FOREIGN KEY (alternativaId) REFERENCES alternativas(id),
    CONSTRAINT fk_usuario FOREIGN KEY (usuarioId) REFERENCES usuarios(id)
);

CREATE TABLE performance (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    usuarioId UUID NOT NULL,
    mes INT NOT NULL,
    ano INT NOT NULL,
    acertos INT NOT NULL,
    erros INT NOT NULL,
    version INT NOT NULL,
    CONSTRAINT fk_usuario
        FOREIGN KEY(usuarioId) 
        REFERENCES usuarios(id)
);