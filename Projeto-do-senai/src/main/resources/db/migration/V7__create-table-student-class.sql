CREATE TABLE student_class (
    id_student BIGINT NOT NULL,
    id_class BIGINT NOT NULL,

    PRIMARY KEY (id_student, id_class),

    CONSTRAINT fk_student_class_student
    FOREIGN KEY (id_student)
    REFERENCES student(id_student),

    CONSTRAINT fk_student_class_class
    FOREIGN KEY (id_class)
    REFERENCES class(id_class)
);