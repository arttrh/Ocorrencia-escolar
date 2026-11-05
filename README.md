# Ocorrência escolar

API Java 25 com Spring Boot 4, PostgreSQL, Flyway e autenticação JWT. Gerencia usuários, alunos, turmas, vínculos e ocorrências escolares.

O projeto mantém a estrutura iniciada pelo autor no commit `928ac97`: controllers e mappers em `adapter/in`, persistência em `adapter/out/repository`, regras em `application/core/usecase`, modelos em `application/core/domain` e interfaces em `application/port`.

## Executar

Em `Projeto-do-senai`, configure as variáveis no seu terminal ou no serviço de hospedagem:

| Variável | Uso |
| --- | --- |
| `DATABASE_URL` | URL JDBC, como `jdbc:postgresql://localhost:5432/ocorrenciaescolar` |
| `DATABASE_USERNAME` | Usuário do PostgreSQL |
| `DATABASE_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Segredo aleatório de pelo menos 32 caracteres |
| `ADMIN_EMAIL` | E-mail da primeira conta administradora |
| `ADMIN_PASSWORD` | Senha inicial forte, de 8 a 72 caracteres |
| `CORS_ALLOWED_ORIGINS` | Origens do frontend, separadas por vírgula |
| `PORT` | Porta HTTP; padrão 8080 |

Execute `./mvnw spring-boot:run`. Para Docker Compose, configure `POSTGRES_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL` e `ADMIN_PASSWORD` em um `.env` local ignorado pelo Git e execute `docker compose up --build`.

Flyway cria as tabelas em um banco novo. A conta inicial só é criada se o e-mail ainda não existir. Não existe conta com senha fixa no código. Não aplique estas migrations em um banco de outra versão sem conferir compatibilidade e backups.

## Uso

1. Faça `POST /login` com `email` e `password` e envie o token retornado como `Authorization: Bearer ...`.
2. Cadastre aluno em `/aluno/cadastrar` e turma em `/turmas/cadastrar`.
3. Vincule o aluno por `POST /turmas/vincular/aluno/{turmaId}` com `studentId` no corpo.
4. Cadastre a ocorrência em `POST /ocorrencias` com aluno, turma, categoria, tipo, data, hora e descrição.
5. Use `PATCH /ocorrencias/{id}/status` para acompanhar o atendimento. Exclusões são lógicas.

O perfil ADMIN gerencia usuários e os demais cadastros. ADMINISTRATIVO gerencia alunos e turmas. Os demais perfis consultam e registram ocorrências. A documentação dos endpoints fica em `/swagger-ui/index.html`; `/health` verifica se a aplicação iniciou.

## Testes

`./mvnw verify` executa testes de API, Cucumber e ArchUnit. Os testes de integração usam PostgreSQL real, inclusive as migrations. Configure `TEST_DATABASE_URL`, `TEST_DATABASE_USERNAME` e `TEST_DATABASE_PASSWORD` para um banco exclusivo de testes. O CI cria seu próprio PostgreSQL.

Cucumber descreve regras de vínculo em português. ArchUnit verifica fronteiras da estrutura original; não exige uma reestruturação do projeto. Os testes de API cobrem autenticação, permissões e o fluxo aluno → turma → vínculo → ocorrência.

## Hospedagem

O backend usa o Dockerfile de `Projeto-do-senai` no Render. Configure as variáveis acima e `/health` como health check. O banco precisa persistir fora do container da aplicação.

O frontend está em [Sistema-ocorrencia-frontEnd](https://github.com/arttrh/Sistema-ocorrencia-frontEnd) e é publicado como site estático na Vercel. Seu caminho `/api` deve ser encaminhado ao backend HTTPS pelo `vercel.json`.
