-- V7__criar_tabela_biometrias.sql
CREATE TABLE biometrias (
                            id BIGSERIAL PRIMARY KEY,
                            lote_id BIGINT NOT NULL,
                            data_biometria DATE NOT NULL,
                            dia_cultivo INTEGER NOT NULL,
                            peso_medio NUMERIC(8, 3) NOT NULL,
                            quantidade_amostrada INTEGER NOT NULL,
                            peso_total_amostra NUMERIC(10, 3),
                            ganho_peso_diario NUMERIC(8, 4),
                            biomassa_estimada NUMERIC(12, 2),
                            sobrevivencia_estimada NUMERIC(5, 2),
                            fator_conversao_alimentar NUMERIC(5, 3),
                            observacoes TEXT,
                            data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                            CONSTRAINT fk_biometrias_lote FOREIGN KEY (lote_id)
                                REFERENCES lotes(id) ON DELETE CASCADE,

    -- Validações
                            CONSTRAINT chk_biometrias_peso_medio CHECK (peso_medio > 0),
                            CONSTRAINT chk_biometrias_quantidade CHECK (quantidade_amostrada > 0),
                            CONSTRAINT chk_biometrias_dia_cultivo CHECK (dia_cultivo >= 0)
);

-- Índices
CREATE INDEX idx_biometrias_lote_id ON biometrias(lote_id);
CREATE INDEX idx_biometrias_data ON biometrias(data_biometria);
CREATE INDEX idx_biometrias_lote_data ON biometrias(lote_id, data_biometria);

-- Comentários
COMMENT ON TABLE biometrias IS 'Medições de peso e crescimento dos camarões';
COMMENT ON COLUMN biometrias.peso_medio IS 'Peso médio em gramas';
COMMENT ON COLUMN biometrias.ganho_peso_diario IS 'GPD - Ganho de Peso Diário em g/dia';
COMMENT ON COLUMN biometrias.biomassa_estimada IS 'Biomassa total estimada em kg';
COMMENT ON COLUMN biometrias.sobrevivencia_estimada IS 'Taxa de sobrevivência estimada em %';
COMMENT ON COLUMN biometrias.fator_conversao_alimentar IS 'FCA - Fator de Conversão Alimentar';
