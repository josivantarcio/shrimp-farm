-- V8__criar_tabela_racoes.sql
CREATE TABLE racoes (
                        id BIGSERIAL PRIMARY KEY,
                        lote_id BIGINT NOT NULL,
                        fornecedor_id BIGINT,
                        data_aplicacao DATE NOT NULL,
                        tipo_racao VARCHAR(20) NOT NULL,
                        marca VARCHAR(100) NOT NULL,
                        quantidade NUMERIC(10, 3) NOT NULL,
                        unidade VARCHAR(10) NOT NULL,
                        custo_unitario NUMERIC(10, 2),
                        custo_total NUMERIC(12, 2),
                        proteina_percentual NUMERIC(5, 2),
                        observacoes TEXT,
                        data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                        CONSTRAINT fk_racoes_lote FOREIGN KEY (lote_id)
                            REFERENCES lotes(id) ON DELETE CASCADE,
                        CONSTRAINT fk_racoes_fornecedor FOREIGN KEY (fornecedor_id)
                            REFERENCES fornecedores(id) ON DELETE SET NULL,

    -- Validações
                        CONSTRAINT chk_racoes_quantidade CHECK (quantidade > 0),
                        CONSTRAINT chk_racoes_custo_unitario CHECK (custo_unitario IS NULL OR custo_unitario >= 0),
                        CONSTRAINT chk_racoes_proteina CHECK (proteina_percentual IS NULL OR (proteina_percentual >= 0 AND proteina_percentual <= 100))
);

-- Índices
CREATE INDEX idx_racoes_lote_id ON racoes(lote_id);
CREATE INDEX idx_racoes_fornecedor_id ON racoes(fornecedor_id);
CREATE INDEX idx_racoes_data ON racoes(data_aplicacao);
CREATE INDEX idx_racoes_tipo ON racoes(tipo_racao);

-- Comentários
COMMENT ON TABLE racoes IS 'Registro de alimentação (ração) dos lotes';
COMMENT ON COLUMN racoes.tipo_racao IS 'INICIAL, CRESCIMENTO, ENGORDA, FINALIZACAO';
COMMENT ON COLUMN racoes.quantidade IS 'Quantidade aplicada na unidade especificada';
COMMENT ON COLUMN racoes.proteina_percentual IS 'Percentual de proteína da ração';
