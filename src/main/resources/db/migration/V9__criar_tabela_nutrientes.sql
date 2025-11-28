-- V9__criar_tabela_nutrientes.sql
CREATE TABLE nutrientes (
                            id BIGSERIAL PRIMARY KEY,
                            lote_id BIGINT NOT NULL,
                            fornecedor_id BIGINT,
                            data_aplicacao DATE NOT NULL,
                            tipo_nutriente VARCHAR(30) NOT NULL,
                            produto VARCHAR(100) NOT NULL,
                            quantidade NUMERIC(10, 3) NOT NULL,
                            unidade VARCHAR(10) NOT NULL,
                            custo_unitario NUMERIC(10, 2),
                            custo_total NUMERIC(12, 2),
                            observacoes TEXT,
                            data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                            CONSTRAINT fk_nutrientes_lote FOREIGN KEY (lote_id)
                                REFERENCES lotes(id) ON DELETE CASCADE,
                            CONSTRAINT fk_nutrientes_fornecedor FOREIGN KEY (fornecedor_id)
                                REFERENCES fornecedores(id) ON DELETE SET NULL,

    -- Validações
                            CONSTRAINT chk_nutrientes_quantidade CHECK (quantidade > 0),
                            CONSTRAINT chk_nutrientes_custo_unitario CHECK (custo_unitario IS NULL OR custo_unitario >= 0)
);

-- Índices
CREATE INDEX idx_nutrientes_lote_id ON nutrientes(lote_id);
CREATE INDEX idx_nutrientes_fornecedor_id ON nutrientes(fornecedor_id);
CREATE INDEX idx_nutrientes_data ON nutrientes(data_aplicacao);
CREATE INDEX idx_nutrientes_tipo ON nutrientes(tipo_nutriente);

-- Comentários
COMMENT ON TABLE nutrientes IS 'Registro de probióticos e suplementos aplicados';
COMMENT ON COLUMN nutrientes.tipo_nutriente IS 'PROBIOTICO, VITAMINA, MINERAL, IMUNOESTIMULANTE, MELHORADOR_AGUA';
