CREATE TABLE usuario(
    id_usuario SERIAL PRIMARY KEY,
    name VARCHAR(50),
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(5) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    profile VARCHAR(10) NOT NULL
);