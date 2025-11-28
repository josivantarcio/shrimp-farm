-- V6__criar_tabela_lotes.sql
CREATE TABLE lotes (
                       id BIGSERIAL PRIMARY KEY,
                       viveiro_id BIGINT NOT NULL,
                       codigo VARCHAR(50) NOT NULL UNIQUE,
                       data_povoamento DATE NOT NULL,
                       data_despesca DATE,
                       quantidade_pos_larvas INTEGER NOT NULL,
                       custo_pos_larvas NUMERIC(12, 2),
                       densidade_inicial NUMERIC(8, 2),
                       status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
                       dias_cultivo INTEGER,
                       observacoes TEXT,
                       data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                       CONSTRAINT fk_lotes_viveiro FOREIGN KEY (viveiro_id)
                           REFERENCES viveiros(id) ON DELETE CASCADE,

    -- Validações
                       CONSTRAINT chk_lotes_quantidade_pos_larvas CHECK (quantidade_pos_larvas > 0),
                       CONSTRAINT chk_lotes_densidade_inicial CHECK (densidade_inicial > 0),
                       CONSTRAINT chk_lotes_datas CHECK (data_despesca IS NULL OR data_despesca >= data_povoamento)
);

-- Índices
CREATE INDEX idx_lotes_viveiro_id ON lotes(viveiro_id);
CREATE INDEX idx_lotes_status ON lotes(status);
CREATE INDEX idx_lotes_data_povoamento ON lotes(data_povoamento);
CREATE INDEX idx_lotes_codigo ON lotes(codigo);

-- Comentários
COMMENT ON TABLE lotes IS 'Lotes de cultivo de camarão';
COMMENT ON COLUMN lotes.codigo IS 'Código único do lote (ex: LOTE01_2025)';
COMMENT ON COLUMN lotes.quantidade_pos_larvas IS 'Quantidade inicial de pós-larvas';
COMMENT ON COLUMN lotes.densidade_inicial IS 'Densidade de povoamento (PLs por m²)';
COMMENT ON COLUMN lotes.status IS 'PLANEJADO, ATIVO, FINALIZADO, CANCELADO';
COMMENT ON COLUMN lotes.dias_cultivo IS 'Dias desde o povoamento (calculado)';
