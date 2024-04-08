CREATE TABLE bancas (
    id UUID PRIMARY key DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL
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
    cidade VARCHAR(100) NOT NULL,
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
    FOREIGN KEY (curso_id) REFERENCES cursos (id)
);


CREATE TABLE curso_disciplina (
    curso_id UUID NOT NULL,
    disciplina_id UUID NOT NULL,
    PRIMARY KEY (curso_id, disciplina_id),
    FOREIGN KEY (curso_id) REFERENCES cursos(id),
    FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id)
);
