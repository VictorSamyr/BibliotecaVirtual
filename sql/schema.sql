-- Schema refatorado da Biblioteca Virtual (PostgreSQL)
-- Corrige limitações do schema original de 2021:
-- 1. Coluna 'senha' ampliada de VARCHAR(64) para VARCHAR(255) para armazenar hashes PBKDF2 com salt.
-- 2. Restrições NOT NULL e CHECK adicionadas para garantir integridade no banco de dados.
-- 3. Coluna 'admin' adicionada para controle explícito de acesso ao painel administrativo.

CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    sexo CHAR(1) NOT NULL CHECK (sexo IN ('M', 'F', 'X')),
    idade INT NOT NULL CHECK (idade > 0 AND idade <= 130),
    generos_preferidos TEXT NOT NULL DEFAULT '',
    admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS livros (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    sinopse TEXT NOT NULL,
    genero VARCHAR(60) NOT NULL,
    autor VARCHAR(120) NOT NULL,
    ano_lancamento INT NOT NULL,
    imagem VARCHAR(255) NOT NULL
);
