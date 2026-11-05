# language: pt
Funcionalidade: Vincular alunos a turmas
  Cenário: Aluno ativo pode entrar em uma turma com vaga
    Dado um aluno ativo sem turma
    Quando vinculo o aluno a uma turma ativa com vaga
    Então o vínculo é permitido
  Cenário: Aluno não pode pertencer a duas turmas
    Dado um aluno ativo já vinculado
    Quando vinculo o aluno a uma turma ativa com vaga
    Então o vínculo é rejeitado
