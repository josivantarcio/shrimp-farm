-- V2__criar_tabela_fornecedores.sql
CREATE TABLE fornecedores (
                              id BIGSERIAL PRIMARY KEY,
                              nome VARCHAR(100) NOT NULL,
                              cnpj VARCHAR(20),
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
CREATE INDEX idx_fornecedores_nome ON fornecedores(nome);
CREATE INDEX idx_fornecedores_ativo ON fornecedores(ativo);
CREATE INDEX idx_fornecedores_cnpj ON fornecedores(cnpj);

-- Comentários
COMMENT ON TABLE fornecedores IS 'Fornecedores de insumos (ração, probióticos, etc)';
