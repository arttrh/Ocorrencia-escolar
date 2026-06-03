CREATE TABLE student(
    id_student BIGSERIAL PRIMARY KEY,
    id_class BIGINT NOT NULL,
    photo TEXT NOT NULL,
    name VARCHAR(100) NOT NULL,
    date_birth DATE NOT NULL,
    aluno_enum VARCHAR(50) NOT NULL,

    CONSTRAINT fk_student_class
    FOREIGN KEY (id_class)
    REFERENCES class(id_class)
);