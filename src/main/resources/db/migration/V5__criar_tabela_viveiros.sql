-- V5__criar_tabela_viveiros.sql
CREATE TABLE viveiros (
                          id BIGSERIAL PRIMARY KEY,
                          fazenda_id BIGINT NOT NULL,
                          codigo VARCHAR(50) NOT NULL,
                          nome VARCHAR(100) NOT NULL,
                          area NUMERIC(10, 2),
                          profundidade_media NUMERIC(5, 2),
                          volume NUMERIC(12, 2),
                          status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
                          observacoes TEXT,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                          CONSTRAINT fk_viveiros_fazenda FOREIGN KEY (fazenda_id)
                              REFERENCES fazendas(id) ON DELETE CASCADE,

    -- Unique constraint
                          CONSTRAINT uk_viveiro_fazenda_codigo UNIQUE (fazenda_id, codigo)
);

-- Índices
CREATE INDEX idx_viveiros_fazenda_id ON viveiros(fazenda_id);
CREATE INDEX idx_viveiros_status ON viveiros(status);
CREATE INDEX idx_viveiros_ativo ON viveiros(ativo);

-- Comentários
COMMENT ON TABLE viveiros IS 'Viveiros (tanques) das fazendas de camarão';
COMMENT ON COLUMN viveiros.area IS 'Área em hectares';
COMMENT ON COLUMN viveiros.profundidade_media IS 'Profundidade média em metros';
COMMENT ON COLUMN viveiros.volume IS 'Volume em metros cúbicos';
COMMENT ON COLUMN viveiros.status IS 'DISPONIVEL, OCUPADO, MANUTENCAO, INATIVO';
