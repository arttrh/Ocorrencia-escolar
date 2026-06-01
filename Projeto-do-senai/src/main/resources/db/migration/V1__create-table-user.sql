CREATE TABLE usuario(
    id_usuario BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile VARCHAR(50) NOT NULL,
    usuario_enum VARCHAR(50)
);