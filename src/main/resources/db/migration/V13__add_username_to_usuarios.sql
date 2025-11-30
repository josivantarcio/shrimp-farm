-- V13__add_username_to_usuarios.sql
-- Adiciona coluna username na tabela usuarios

-- 1. Adiciona coluna permitindo NULL temporariamente
ALTER TABLE usuarios
    ADD COLUMN username VARCHAR(50);

-- 2. Popular username com base no email para registros existentes
-- Pega tudo antes do @ do email e converte para minúscula
UPDATE usuarios
SET username = LOWER(SUBSTRING(email FROM 1 FOR POSITION('@' IN email) - 1))
WHERE username IS NULL;

-- 3. Agora torna NOT NULL (todos os registros já têm valor)
ALTER TABLE usuarios
    ALTER COLUMN username SET NOT NULL;

-- 4. Adiciona constraint de unicidade
ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_username UNIQUE (username);

-- 5. Cria índice para performance
CREATE INDEX idx_usuarios_username ON usuarios(username);

-- Comentário
COMMENT ON COLUMN usuarios.username IS 'Username/login do usuário (alternativa ao email)';
