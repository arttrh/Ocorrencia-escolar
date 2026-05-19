CREATE TABLE ocorrencia(
    id_ocorrencia SERIAL PRIMARY KEY,
    id_turma INTEGER NOT NULL,
    id_aluno INTEGER NOT NULL,
    id_categoria_ocorrencia INTEGER NOT NULL,
    id_tipo_ocorrencia INTEGER NOT NULL,
    data_da_ocorrencia DATE NOT NULL,
    hora TIME NOT NULL,
    descricao_da_ocorrencia VARCHAR(100) NOT NULL,

    CONSTRAINT fk_turma FOREIGN KEY(id_turma) REFERENCES turma(id_turma),
    CONSTRAINT fk_aluno FOREIGN KEY(id_aluno) REFERENCES aluno(id_aluno),
    CONSTRAINT fk_categoria_ocorrenca FOREIGN KEY(id_categoria_ocorrencia) REFERENCES categoria_ocorrencia(id_categoria_ocorrencia),
    CONSTRAINT fk_tipo_ocorrencia FOREIGN KEY(id_tipo_ocorrencia) REFERENCES tipo_ocorrencia(id_tipo_ocorrencia),


)