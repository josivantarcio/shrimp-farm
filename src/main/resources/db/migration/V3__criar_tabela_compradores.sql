-- V3__criar_tabela_compradores.sql
CREATE TABLE compradores (
                             id BIGSERIAL PRIMARY KEY,
                             nome VARCHAR(100) NOT NULL,
                             cnpj VARCHAR(20),
                             cpf VARCHAR(20),
                             telefone VARCHAR(20),
                             email VARCHAR(100),
                             endereco VARCHAR(200),
                             cidade VARCHAR(100),
                             estado VARCHAR(2),
                             cep VARCHAR(10),
                             contato VARCHAR(50),
                             observacoes TEXT,
                             ativo BOOLEAN NOT NULL DEFAULT TRUE,
                             data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_compradores_nome ON compradores(nome);
CREATE INDEX idx_compradores_ativo ON compradores(ativo);

-- Comentários
COMMENT ON TABLE compradores IS 'Compradores de camarão (clientes)';
