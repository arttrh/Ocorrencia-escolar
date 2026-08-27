-- ---------------------------------------------------------------------------
-- Catalogo de categorias e tipos de ocorrencia.
--
-- Sem esses registros os endpoints /incidents/categories e /incidents/types/{c}
-- devolvem lista vazia e nao e' possivel registrar nenhuma ocorrencia, ja que o
-- cadastro exige uma categoria e um tipo existentes.
-- ---------------------------------------------------------------------------

INSERT INTO category_occurrence (name_category) VALUES
    ('DISCIPLINAR'),
    ('PEDAGOGICA'),
    ('FREQUENCIA'),
    ('SAUDE'),
    ('PATRIMONIO')
ON CONFLICT (name_category) DO NOTHING;

INSERT INTO type_occurrence (name_occurrence, id_category_occurrence)
SELECT tipo.nome, c.id_category_occurrence
  FROM (VALUES
        ('INDISCIPLINA EM SALA',      'DISCIPLINAR'),
        ('DESRESPEITO AO COLEGA',     'DISCIPLINAR'),
        ('DESRESPEITO AO PROFESSOR',  'DISCIPLINAR'),
        ('USO INDEVIDO DE CELULAR',   'DISCIPLINAR'),
        ('NAO ENTREGA DE ATIVIDADE',  'PEDAGOGICA'),
        ('DIFICULDADE DE APRENDIZADO','PEDAGOGICA'),
        ('BAIXO RENDIMENTO',          'PEDAGOGICA'),
        ('FALTA NAO JUSTIFICADA',     'FREQUENCIA'),
        ('ATRASO RECORRENTE',         'FREQUENCIA'),
        ('SAIDA ANTECIPADA',          'FREQUENCIA'),
        ('MAL ESTAR',                 'SAUDE'),
        ('ACIDENTE ESCOLAR',          'SAUDE'),
        ('DANO AO PATRIMONIO',        'PATRIMONIO'),
        ('EXTRAVIO DE MATERIAL',      'PATRIMONIO')
       ) AS tipo(nome, categoria)
  JOIN category_occurrence c ON c.name_category = tipo.categoria
ON CONFLICT (name_occurrence) DO NOTHING;
