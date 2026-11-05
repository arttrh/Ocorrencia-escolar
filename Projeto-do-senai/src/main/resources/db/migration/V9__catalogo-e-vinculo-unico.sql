ALTER TABLE vinculo ADD CONSTRAINT uk_vinculo_student UNIQUE (id_student);
INSERT INTO category_occurrence (name_category) VALUES ('Disciplinar'), ('Pedagógica');
INSERT INTO type_occurrence (name_occurrence, id_category_occurrence) VALUES
('Atraso', (SELECT id_category_occurrence FROM category_occurrence WHERE name_category='Disciplinar')),
('Conflito', (SELECT id_category_occurrence FROM category_occurrence WHERE name_category='Disciplinar')),
('Dificuldade', (SELECT id_category_occurrence FROM category_occurrence WHERE name_category='Pedagógica'));
