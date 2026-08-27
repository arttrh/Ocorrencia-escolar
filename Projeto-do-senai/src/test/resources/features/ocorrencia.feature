# language: pt
Funcionalidade: Registro e acompanhamento de ocorrencias
  Como coordenador da escola
  Quero registrar ocorrencias disciplinares e acompanhar o atendimento
  Para manter o historico do aluno e nao perder nenhum caso de vista

  Contexto:
    Dado que existe a turma "DS-01" ativa
    E que existe o aluno "Ana Souza" matriculado na turma "DS-01"
    E que existe a categoria "DISCIPLINAR" com o tipo "INDISCIPLINA EM SALA"
    E que estou autenticado como "COORDENADOR"

  Cenario: Registrar uma ocorrencia para um aluno da turma
    Quando eu registro uma ocorrencia para "Ana Souza" na turma "DS-01" com a descricao "Conversa excessiva durante a aula"
    Entao a requisicao e' aceita com o status 201
    E a ocorrencia fica com a situacao "AGUARDANDO"
    E a ocorrencia aparece na listagem de ocorrencias

  Cenario: Nao registrar ocorrencia para aluno de outra turma
    Dado que existe o aluno "Bruno Lima" sem turma
    Quando eu registro uma ocorrencia para "Bruno Lima" na turma "DS-01" com a descricao "Fato qualquer"
    Entao a requisicao e' recusada com o status 400
    E a mensagem de erro menciona "nao esta matriculado"

  Cenario: Nao registrar ocorrencia para aluno inativo
    Dado que o aluno "Ana Souza" esta inativo
    Quando eu registro uma ocorrencia para "Ana Souza" na turma "DS-01" com a descricao "Fato qualquer"
    Entao a requisicao e' recusada com o status 400

  Cenario: Nao registrar ocorrencia em turma cancelada
    Dado que a turma "DS-01" esta cancelada
    Quando eu registro uma ocorrencia para "Ana Souza" na turma "DS-01" com a descricao "Fato qualquer"
    Entao a requisicao e' recusada com o status 400

  Cenario: Acompanhar o atendimento ate a resolucao
    Dado que existe uma ocorrencia registrada para "Ana Souza"
    Quando eu mudo a situacao da ocorrencia para "ATENDENDO"
    Entao a ocorrencia fica com a situacao "ATENDENDO"
    Quando eu mudo a situacao da ocorrencia para "RESOLVIDA"
    Entao a ocorrencia fica com a situacao "RESOLVIDA"

  Cenario: Nao reabrir uma ocorrencia ja concluida
    Dado que existe uma ocorrencia registrada para "Ana Souza"
    E que a ocorrencia foi levada ate a situacao "RESOLVIDA"
    Quando eu mudo a situacao da ocorrencia para "ATENDENDO"
    Entao a requisicao e' recusada com o status 400

  Esquema do Cenario: Nao pular etapas do atendimento
    Dado que existe uma ocorrencia registrada para "Ana Souza"
    Quando eu mudo a situacao da ocorrencia para "<situacao>"
    Entao a requisicao e' recusada com o status 400

    Exemplos:
      | situacao      |
      | RESOLVIDA     |
      | NAO_RESOLVIDA |

  Cenario: Cancelar uma ocorrencia preserva o historico
    Dado que existe uma ocorrencia registrada para "Ana Souza"
    Quando eu cancelo a ocorrencia
    Entao a ocorrencia nao aparece mais na listagem de ocorrencias
    Mas a ocorrencia continua consultavel pelo seu id

  Cenario: O dashboard reflete as situacoes das ocorrencias
    Dado que existe uma ocorrencia registrada para "Ana Souza"
    E que a ocorrencia foi levada ate a situacao "ATENDENDO"
    Quando eu consulto o resumo de ocorrencias
    Entao o resumo mostra 1 ocorrencia em atendimento
    E o resumo agrupa 1 ocorrencia na categoria "DISCIPLINAR"
