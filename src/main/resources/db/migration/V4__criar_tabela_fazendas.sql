-- V4__criar_tabela_fazendas.sql
CREATE TABLE fazendas (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          proprietario VARCHAR(100),
                          endereco VARCHAR(200),
                          cidade VARCHAR(100),
                          estado VARCHAR(2),
                          cep VARCHAR(10),
                          area_total NUMERIC(10, 2),
                          area_util NUMERIC(10, 2),
                          telefone VARCHAR(20),
                          email VARCHAR(100),
                          observacoes TEXT,
                          ativa BOOLEAN NOT NULL DEFAULT TRUE,
                          data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_fazendas_nome ON fazendas(nome);
CREATE INDEX idx_fazendas_ativa ON fazendas(ativa);

-- Comentários
COMMENT ON TABLE fazendas IS 'Fazendas de carcinicultura';
COMMENT ON COLUMN fazendas.area_total IS 'Área total em hectares';
COMMENT ON COLUMN fazendas.area_util IS 'Área útil de viveiros em hectares';
