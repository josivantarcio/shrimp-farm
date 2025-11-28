-- V11__criar_tabela_custos_variaveis.sql
CREATE TABLE custos_variaveis (
                                  id BIGSERIAL PRIMARY KEY,
                                  lote_id BIGINT NOT NULL,
                                  data_lancamento DATE NOT NULL,
                                  categoria VARCHAR(30) NOT NULL,
                                  descricao VARCHAR(200) NOT NULL,
                                  valor NUMERIC(12, 2) NOT NULL,
                                  observacoes TEXT,
                                  data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                                  CONSTRAINT fk_custos_variaveis_lote FOREIGN KEY (lote_id)
                                      REFERENCES lotes(id) ON DELETE CASCADE,

    -- Validações
                                  CONSTRAINT chk_custos_variaveis_valor CHECK (valor >= 0)
);

-- Índices
CREATE INDEX idx_custos_variaveis_lote_id ON custos_variaveis(lote_id);
CREATE INDEX idx_custos_variaveis_data ON custos_variaveis(data_lancamento);
CREATE INDEX idx_custos_variaveis_categoria ON custos_variaveis(categoria);

-- Comentários
COMMENT ON TABLE custos_variaveis IS 'Outros custos variáveis do lote';
COMMENT ON COLUMN custos_variaveis.categoria IS 'RACAO, NUTRIENTE, FERTILIZACAO, POS_LARVA, ENERGIA, COMBUSTIVEL, MAO_OBRA, MANUTENCAO, OUTROS';
