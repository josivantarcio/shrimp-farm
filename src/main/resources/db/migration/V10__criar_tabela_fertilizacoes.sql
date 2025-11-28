-- V10__criar_tabela_fertilizacoes.sql
CREATE TABLE fertilizacoes (
                               id BIGSERIAL PRIMARY KEY,
                               lote_id BIGINT NOT NULL,
                               fornecedor_id BIGINT,
                               data_aplicacao DATE NOT NULL,
                               produto VARCHAR(100) NOT NULL,
                               quantidade NUMERIC(10, 3) NOT NULL,
                               unidade VARCHAR(10) NOT NULL,
                               custo_unitario NUMERIC(10, 2),
                               custo_total NUMERIC(12, 2),
                               finalidade VARCHAR(50),
                               observacoes TEXT,
                               data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                               CONSTRAINT fk_fertilizacoes_lote FOREIGN KEY (lote_id)
                                   REFERENCES lotes(id) ON DELETE CASCADE,
                               CONSTRAINT fk_fertilizacoes_fornecedor FOREIGN KEY (fornecedor_id)
                                   REFERENCES fornecedores(id) ON DELETE SET NULL,

    -- Validações
                               CONSTRAINT chk_fertilizacoes_quantidade CHECK (quantidade > 0),
                               CONSTRAINT chk_fertilizacoes_custo_unitario CHECK (custo_unitario IS NULL OR custo_unitario >= 0)
);

-- Índices
CREATE INDEX idx_fertilizacoes_lote_id ON fertilizacoes(lote_id);
CREATE INDEX idx_fertilizacoes_fornecedor_id ON fertilizacoes(fornecedor_id);
CREATE INDEX idx_fertilizacoes_data ON fertilizacoes(data_aplicacao);

-- Comentários
COMMENT ON TABLE fertilizacoes IS 'Registro de fertilização dos viveiros';
COMMENT ON COLUMN fertilizacoes.finalidade IS 'Ex: Preparação do viveiro, Manutenção';
