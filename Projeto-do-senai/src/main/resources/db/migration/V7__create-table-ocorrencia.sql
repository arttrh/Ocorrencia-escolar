CREATE TABLE occurrence(
    id_occurrence SERIAL PRIMARY KEY,
    id_class INTEGER NOT NULL,
    id_student INTEGER NOT NULL,
    id_category_occurrence INTEGER NOT NULL,
    id_type_of_occurrence INTEGER NOT NULL,
    date_occurrence DATE NOT NULL,
    time TIME NOT NULL,
    description_of_occurrence VARCHAR(100) NOT NULL,

    CONSTRAINT fk_class FOREIGN KEY(id_class) REFERENCES class(id_class),
    CONSTRAINT fk_student FOREIGN KEY(id_student) REFERENCES student(id_student),
    CONSTRAINT fk_category_occurrence FOREIGN KEY(id_category_occurrence) REFERENCES category_occurrence(id_category_occurrence),
    CONSTRAINT fk_type_of_occurrence FOREIGN KEY(id_type_of_occurrence) REFERENCES type_of_occurrence(id_type_of_occurrence)
);