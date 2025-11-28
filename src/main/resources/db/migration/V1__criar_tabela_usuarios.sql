-- V1__criar_tabela_usuarios.sql
CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          nome VARCHAR(100) NOT NULL,
                          telefone VARCHAR(20),
                          papel VARCHAR(20) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

-- Comentários
COMMENT ON TABLE usuarios IS 'Usuários do sistema';
COMMENT ON COLUMN usuarios.papel IS 'Role do usuário: ADMIN, GERENTE, OPERACIONAL, VISUALIZADOR';
