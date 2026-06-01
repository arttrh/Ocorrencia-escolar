 CREATE TABLE type_occurrence(
    id_type_occurrence BIGSERIAL PRIMARY KEY,
    name_occurrence VARCHAR(20),
    id_category_occurrence BIGSERIAL NOT NULL,

     FOREIGN KEY(id_category_occurrence)
     REFERENCES category_occurrence(id_category_occurrence)
 );