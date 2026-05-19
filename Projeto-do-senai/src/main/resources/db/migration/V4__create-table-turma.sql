CREATE TABLE turma(
    id_turma SERIAL PRIMARY KEY,
    nome_da_turma VARCHAR(50) NOT NULL,
    turno VARCHAR(15) NOT NULL,
    ano_turma INT NOT NULL CHECK(ano_turma >= 2000 AND ano_turma<= 2100)
)