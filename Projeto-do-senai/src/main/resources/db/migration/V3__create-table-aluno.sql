CREATE TABLE aluno(
    id_aluno SERIAL PRIMARY KEY,
    foto TEXT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL

)