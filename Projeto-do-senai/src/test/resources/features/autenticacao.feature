# language: pt
Funcionalidade: Autenticacao e protecao do login
  Como responsavel pelo sistema
  Quero que o acesso seja controlado e resistente a tentativas em massa
  Para proteger os dados dos alunos

  Contexto:
    Dado que existe o usuario "coordenador@escola.com" com o perfil "COORDENADOR"

  Cenario: Entrar com as credenciais corretas
    Quando eu tento entrar com o login "coordenador@escola.com" e a senha "senha123"
    Entao a requisicao e' aceita com o status 200
    E eu recebo um token de acesso

  Cenario: Recusar senha incorreta
    Quando eu tento entrar com o login "coordenador@escola.com" e a senha "errada"
    Entao a requisicao e' recusada com o status 401

  Cenario: Nao revelar se a conta existe
    Quando eu tento entrar com o login "coordenador@escola.com" e a senha "errada"
    E eu guardo a mensagem de erro recebida
    Quando eu tento entrar com o login "naoexiste@escola.com" e a senha "errada"
    Entao a mensagem de erro e' igual a anterior

  Cenario: Bloquear apos tentativas repetidas do mesmo login
    Quando eu erro a senha de "coordenador@escola.com" 3 vezes
    E eu tento entrar com o login "coordenador@escola.com" e a senha "errada"
    Entao a requisicao e' recusada com o status 429
    E a resposta informa em quantos segundos posso tentar de novo

  Cenario: O bloqueio de um login nao afeta os demais usuarios
    Dado que existe o usuario "professor@escola.com" com o perfil "PROFESSOR"
    Quando eu erro a senha de "coordenador@escola.com" 4 vezes
    E eu tento entrar com o login "professor@escola.com" e a senha "senha123"
    Entao a requisicao e' aceita com o status 200
    E eu recebo um token de acesso

  Cenario: Usuario inativo nao entra
    Dado que o usuario "coordenador@escola.com" esta inativo
    Quando eu tento entrar com o login "coordenador@escola.com" e a senha "senha123"
    Entao a requisicao e' recusada com o status 400

  Cenario: Recurso protegido exige token
    Quando eu consulto as ocorrencias sem token
    Entao a requisicao e' recusada com o status 401
