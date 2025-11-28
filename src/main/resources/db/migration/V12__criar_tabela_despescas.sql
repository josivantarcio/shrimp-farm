-- V12__criar_tabela_despescas.sql
CREATE TABLE despescas (
                           id BIGSERIAL PRIMARY KEY,
                           lote_id BIGINT NOT NULL UNIQUE,
                           comprador_id BIGINT,
                           data_despesca DATE NOT NULL,
                           peso_total NUMERIC(12, 2) NOT NULL,
                           quantidade_despescada INTEGER NOT NULL,
                           peso_medio_final NUMERIC(8, 3) NOT NULL,
                           taxa_sobrevivencia NUMERIC(5, 2),
                           preco_venda_kg NUMERIC(10, 2),
                           receita_total NUMERIC(12, 2),
                           custo_despesca NUMERIC(10, 2),
                           observacoes TEXT,
                           data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                           CONSTRAINT fk_despescas_lote FOREIGN KEY (lote_id)
                               REFERENCES lotes(id) ON DELETE CASCADE,
                           CONSTRAINT fk_despescas_comprador FOREIGN KEY (comprador_id)
                               REFERENCES compradores(id) ON DELETE SET NULL,

    -- Validações
                           CONSTRAINT chk_despescas_peso_total CHECK (peso_total > 0),
                           CONSTRAINT chk_despescas_quantidade CHECK (quantidade_despescada > 0),
                           CONSTRAINT chk_despescas_peso_medio CHECK (peso_medio_final > 0),
                           CONSTRAINT chk_despescas_taxa_sobrevivencia CHECK (taxa_sobrevivencia IS NULL OR (taxa_sobrevivencia >= 0 AND taxa_sobrevivencia <= 100)),
                           CONSTRAINT chk_despescas_preco_venda CHECK (preco_venda_kg IS NULL OR preco_venda_kg >= 0),
                           CONSTRAINT chk_despescas_receita CHECK (receita_total IS NULL OR receita_total >= 0)
);

-- Índices
CREATE INDEX idx_despescas_lote_id ON despescas(lote_id);
CREATE INDEX idx_despescas_comprador_id ON despescas(comprador_id);
CREATE INDEX idx_despescas_data ON despescas(data_despesca);

-- Comentários
COMMENT ON TABLE despescas IS 'Registro da colheita final (despesca) dos lotes';
COMMENT ON COLUMN despescas.peso_total IS 'Peso total colhido em kg';
COMMENT ON COLUMN despescas.quantidade_despescada IS 'Número de camarões colhidos';
COMMENT ON COLUMN despescas.peso_medio_final IS 'Peso médio final em gramas';
COMMENT ON COLUMN despescas.taxa_sobrevivencia IS 'Taxa de sobrevivência em %';
COMMENT ON COLUMN despescas.preco_venda_kg IS 'Preço de venda por kg em R$';
COMMENT ON COLUMN despescas.receita_total IS 'Receita total da venda em R$';
COMMENT ON COLUMN despescas.custo_despesca IS 'Custo operacional da despesca em R$';
