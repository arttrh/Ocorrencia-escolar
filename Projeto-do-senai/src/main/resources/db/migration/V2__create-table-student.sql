CREATE TABLE student(
    id_student BIGSERIAL PRIMARY KEY,
    photo TEXT NOT NULL,
    name VARCHAR(100) NOT NULL,
    date_birth DATE NOT NULL,
    aluno_enum VARCHAR(50) NOT NULL
);