CREATE TABLE student(
    id_student SERIAL PRIMARY KEY,
    photo TEXT NOT NULL,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL
);