CREATE TABLE occurrence(
    id_occurrence BIGSERIAL PRIMARY KEY,
    id_class BIGSERIAL NOT NULL,
    id_student BIGSERIAL NOT NULL,
    id_category_occurrence BIGSERIAL NOT NULL,
    id_type_occurrence BIGSERIAL NOT NULL,
    date_occurrence DATE NOT NULL,
    time TIME NOT NULL,
    description_occurrence VARCHAR(100) NOT NULL,
    occurrence_enum VARCHAR(50) NOT NULL,

    CONSTRAINT fk_class FOREIGN KEY(id_class) REFERENCES class(id_class),
    CONSTRAINT fk_student FOREIGN KEY(id_student) REFERENCES student(id_student),
    CONSTRAINT fk_category_occurrence FOREIGN KEY(id_category_occurrence) REFERENCES category_occurrence(id_category_occurrence),
    CONSTRAINT fk_type_of_occurrence FOREIGN KEY(id_type_occurrence) REFERENCES type_occurrence(id_type_occurrence)
);