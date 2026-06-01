CREATE TABLE class(
    id_class BIGSERIAL PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    shift VARCHAR(15) NOT NULL,
    class_year INT NOT NULL CHECK(class_year >= 2000 AND class_year <= 2100),
    turma_enum VARCHAR(100) NOT NULL
);