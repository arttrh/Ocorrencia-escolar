CREATE TABLE vinculo(
    id BIGSERIAL PRIMARY KEY,
    id_student BIGSERIAL NOT NULL,
    id_class BIGSERIAL NOT NULL,

    FOREIGN KEY (id_student) REFERENCES student(id_student),
    FOREIGN KEY (id_class) REFERENCES class(id_class)
)