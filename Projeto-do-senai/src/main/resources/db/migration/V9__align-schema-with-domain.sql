-- ---------------------------------------------------------------------------
-- Alinha o schema ao modelo de dominio e ao contrato consumido pelo front-end.
--
-- Todas as conversoes preservam os dados existentes: colunas novas sao populadas
-- a partir das antigas antes de qualquer DROP.
-- ---------------------------------------------------------------------------

-- ============================ usuario ======================================
-- "email" passa a se chamar "login": o campo e' o identificador de acesso e e'
-- esse o nome que o formulario de login envia.
ALTER TABLE usuario RENAME COLUMN email TO login;

-- O enum de status tinha a constante grafada "INVATIVO".
UPDATE usuario SET usuario_enum = 'INATIVO' WHERE usuario_enum = 'INVATIVO';
UPDATE usuario SET usuario_enum = 'ATIVO'   WHERE usuario_enum IS NULL;
ALTER TABLE usuario ALTER COLUMN usuario_enum SET DEFAULT 'ATIVO';
ALTER TABLE usuario ALTER COLUMN usuario_enum SET NOT NULL;

UPDATE usuario SET name = 'Sem nome' WHERE name IS NULL;
ALTER TABLE usuario ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE usuario ALTER COLUMN name SET NOT NULL;

-- ============================ student ======================================
-- "photo" vira "image_url" e deixa de ser obrigatoria: o cadastro de aluno do
-- front nao envia imagem, o upload e' um passo separado.
ALTER TABLE student RENAME COLUMN photo TO image_url;
ALTER TABLE student ALTER COLUMN image_url DROP NOT NULL;
ALTER TABLE student ALTER COLUMN image_url TYPE TEXT;

UPDATE student SET aluno_enum = 'INATIVO' WHERE aluno_enum = 'INVATIVO';
UPDATE student SET aluno_enum = 'ATIVO'   WHERE aluno_enum IS NULL;
ALTER TABLE student ALTER COLUMN aluno_enum SET DEFAULT 'ATIVO';

-- ============================= class =======================================
-- O ano da turma era TIMESTAMP mas conceitualmente e' um numero (o front envia
-- <input type="number">).
ALTER TABLE class ADD COLUMN class_year_novo INTEGER;
UPDATE class SET class_year_novo = EXTRACT(YEAR FROM class_year)::INTEGER;
UPDATE class SET class_year_novo = EXTRACT(YEAR FROM NOW())::INTEGER WHERE class_year_novo IS NULL;
ALTER TABLE class DROP COLUMN class_year;
ALTER TABLE class RENAME COLUMN class_year_novo TO class_year;
ALTER TABLE class ALTER COLUMN class_year SET NOT NULL;

-- "semestry" (TIMESTAMP) vira "semester" (enum): o front oferece um select.
ALTER TABLE class ADD COLUMN semester VARCHAR(20);
UPDATE class SET semester = CASE
        WHEN semestry IS NULL THEN 'PRIMEIRO'
        WHEN EXTRACT(MONTH FROM semestry) <= 6 THEN 'PRIMEIRO'
        ELSE 'SEGUNDO'
    END;
ALTER TABLE class DROP COLUMN semestry;
ALTER TABLE class ALTER COLUMN semester SET NOT NULL;

UPDATE class SET turma_enum  = 'ATIVA'   WHERE turma_enum IS NULL;
UPDATE class SET turma_turno = 'MANHA'   WHERE turma_turno IS NULL;
ALTER TABLE class ALTER COLUMN turma_enum TYPE VARCHAR(20);
ALTER TABLE class ALTER COLUMN turma_enum SET DEFAULT 'ATIVA';
ALTER TABLE class ALTER COLUMN turma_enum SET NOT NULL;
ALTER TABLE class ALTER COLUMN turma_turno TYPE VARCHAR(20);
ALTER TABLE class ALTER COLUMN turma_turno SET NOT NULL;

-- ====================== categorias e tipos =================================
ALTER TABLE category_occurrence ALTER COLUMN name_category TYPE VARCHAR(60);
UPDATE category_occurrence SET name_category = 'SEM CATEGORIA' WHERE name_category IS NULL;
ALTER TABLE category_occurrence ALTER COLUMN name_category SET NOT NULL;
ALTER TABLE category_occurrence ADD CONSTRAINT uk_category_occurrence_name UNIQUE (name_category);

ALTER TABLE type_occurrence ALTER COLUMN name_occurrence TYPE VARCHAR(60);
UPDATE type_occurrence SET name_occurrence = 'SEM TIPO' WHERE name_occurrence IS NULL;
ALTER TABLE type_occurrence ALTER COLUMN name_occurrence SET NOT NULL;
ALTER TABLE type_occurrence ADD CONSTRAINT uk_type_occurrence_name UNIQUE (name_occurrence);

-- Chaves estrangeiras foram declaradas como BIGSERIAL, o que criou uma sequence
-- por coluna: um INSERT que esquecesse o id preenchia um valor gerado em vez de
-- falhar. Aqui elas voltam a ser BIGINT simples.
ALTER TABLE type_occurrence ALTER COLUMN id_category_occurrence DROP DEFAULT;
DROP SEQUENCE IF EXISTS type_occurrence_id_category_occurrence_seq;

-- =========================== occurrence ====================================
-- Data e hora viram uma unica coluna: separadas, ordenar cronologicamente exigia
-- combinar dois campos e o front sempre tratou as duas como um instante so'.
-- A coluna chamava-se "time", que no PostgreSQL e' nome de tipo: sem aspas o
-- parser nao a reconhece como coluna.
ALTER TABLE occurrence ADD COLUMN register_date TIMESTAMP;
UPDATE occurrence SET register_date = (date_occurrence + "time");
UPDATE occurrence SET register_date = NOW() WHERE register_date IS NULL;
ALTER TABLE occurrence ALTER COLUMN register_date SET NOT NULL;
ALTER TABLE occurrence DROP COLUMN date_occurrence;
ALTER TABLE occurrence DROP COLUMN "time";

ALTER TABLE occurrence ADD COLUMN update_date TIMESTAMP;
ALTER TABLE occurrence ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE occurrence ALTER COLUMN description_occurrence TYPE VARCHAR(500);

-- Situacoes renomeadas para o singular; CANCELADAS/INATIVA passam a ser
-- representadas pela exclusao logica (deleted).
UPDATE occurrence SET deleted = TRUE WHERE occurrence_enum IN ('CANCELADAS', 'INATIVA');
UPDATE occurrence SET occurrence_enum = 'ATIVA'          WHERE occurrence_enum = 'ATIVAS';
UPDATE occurrence SET occurrence_enum = 'RESOLVIDA'      WHERE occurrence_enum = 'RESOLVIDAS';
UPDATE occurrence SET occurrence_enum = 'NAO_RESOLVIDA'  WHERE occurrence_enum = 'NAO_RESOLVIDAS';
UPDATE occurrence SET occurrence_enum = 'FECHADA'        WHERE occurrence_enum = 'FECHADAS';
UPDATE occurrence SET occurrence_enum = 'AGUARDANDO'
    WHERE occurrence_enum IS NULL
       OR occurrence_enum NOT IN ('AGUARDANDO', 'ATENDENDO', 'ATIVA',
                                  'RESOLVIDA', 'NAO_RESOLVIDA', 'FECHADA');
ALTER TABLE occurrence ALTER COLUMN occurrence_enum TYPE VARCHAR(30);
ALTER TABLE occurrence ALTER COLUMN occurrence_enum SET DEFAULT 'AGUARDANDO';
ALTER TABLE occurrence ALTER COLUMN occurrence_enum SET NOT NULL;

ALTER TABLE occurrence ALTER COLUMN id_class DROP DEFAULT;
ALTER TABLE occurrence ALTER COLUMN id_student DROP DEFAULT;
ALTER TABLE occurrence ALTER COLUMN id_category_occurrence DROP DEFAULT;
ALTER TABLE occurrence ALTER COLUMN id_type_occurrence DROP DEFAULT;
DROP SEQUENCE IF EXISTS occurrence_id_class_seq;
DROP SEQUENCE IF EXISTS occurrence_id_student_seq;
DROP SEQUENCE IF EXISTS occurrence_id_category_occurrence_seq;
DROP SEQUENCE IF EXISTS occurrence_id_type_occurrence_seq;

-- ============================= vinculo =====================================
-- Um aluno pertence a no maximo uma turma. Sem essa restricao a regra so'
-- existia no codigo e uma corrida entre duas matriculas a violava.
DELETE FROM vinculo v
 WHERE v.id NOT IN (SELECT MIN(id) FROM vinculo GROUP BY id_student);
ALTER TABLE vinculo ADD CONSTRAINT uk_vinculo_student UNIQUE (id_student);

ALTER TABLE vinculo ALTER COLUMN id_student DROP DEFAULT;
ALTER TABLE vinculo ALTER COLUMN id_class DROP DEFAULT;
DROP SEQUENCE IF EXISTS vinculo_id_student_seq;
DROP SEQUENCE IF EXISTS vinculo_id_class_seq;

-- ============================= indices =====================================
-- Sustentam as consultas de listagem por situacao, o historico por aluno e as
-- agregacoes do dashboard.
CREATE INDEX IF NOT EXISTS idx_occurrence_status   ON occurrence (occurrence_enum, deleted);
CREATE INDEX IF NOT EXISTS idx_occurrence_student  ON occurrence (id_student, deleted);
CREATE INDEX IF NOT EXISTS idx_occurrence_class    ON occurrence (id_class, deleted);
CREATE INDEX IF NOT EXISTS idx_occurrence_register ON occurrence (register_date DESC);
CREATE INDEX IF NOT EXISTS idx_vinculo_class       ON vinculo (id_class);
CREATE INDEX IF NOT EXISTS idx_student_status      ON student (aluno_enum);
CREATE INDEX IF NOT EXISTS idx_class_status        ON class (turma_enum);
CREATE INDEX IF NOT EXISTS idx_usuario_status      ON usuario (usuario_enum);
