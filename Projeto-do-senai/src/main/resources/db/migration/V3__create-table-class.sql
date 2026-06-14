CREATE TABLE class(
    id_class BIGSERIAL PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    class_year TIMESTAMP NOT NULL,
    turma_enum VARCHAR(100),
    turma_turno VARCHAR(50),
    semestry TIMESTAMP NOT NULL
);