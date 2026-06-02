CREATE TABLE usuario(
    id_usuario BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile VARCHAR(50) NOT NULL,
    usuario_enum VARCHAR(50)
);

INSERT INTO usuario (
    name,
    email,
    password,
    profile,
    usuario_enum
) VALUES (
             'admin',
             'admin@sistema.com',
             '$2a$10$uy7r8OyHAOCjq4u2YO61WO9ZWiJ.IVLmz1.fmNL3peq0TYvI4l1Sa',
             'ADMIN',
             'ATIVO'
         );

