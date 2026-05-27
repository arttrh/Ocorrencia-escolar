CREATE TABLE class(
    id_class SERIAL PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    shift VARCHAR(15) NOT NULL,
    class_year INT NOT NULL CHECK(class_year >= 2000 AND class_year <= 2100)
);