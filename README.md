# 🏫 Sistema de Ocorrência Escolar

<div align="center">
  <img src="./Nano Banana 2 - Animate this pixel art isometric school scene with the following movementsStudents.png" alt="Sistema Escolar" width="500">

  **Registro e acompanhamento de ocorrências disciplinares, alunos e turmas**

  ![Java](https://img.shields.io/badge/Java-25-ED8936?style=for-the-badge&logo=openjdk)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=for-the-badge&logo=spring)
  ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql)
  ![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
</div>

---

## 📋 Sobre

API REST em **arquitetura hexagonal** para gestão de ocorrências escolares. O núcleo
de regras de negócio não depende de Spring, JPA nem HTTP — esses são detalhes plugados
por adaptadores nas bordas.

O front-end vive em [`Sistema-ocorrencia-frontEnd`](https://github.com/arttrh/Sistema-ocorrencia-frontEnd).

### Funcionalidades

| Recurso | O que faz |
|---|---|
| **Usuários** | Cadastro, perfis de acesso, troca de senha, ativação/inativação |
| **Alunos** | Cadastro, foto, matrícula em turma, inativação lógica |
| **Turmas** | Cadastro por turno/ano/semestre, capacidade máxima, cancelamento |
| **Ocorrências** | Registro, atualização, máquina de estados do atendimento, histórico por aluno, cancelamento lógico |
| **Dashboard** | Totais por situação e agregações por categoria, tipo, turma e aluno |
| **Segurança** | JWT, autorização por perfil, rate limit no login |

---

## 🏛️ Arquitetura

```
br.com.project_sena
│
├── application/                    ← NÚCLEO (sem framework)
│   ├── core/
│   │   ├── domain/
│   │   │   ├── model/              Usuario, Aluno, Turma, Ocorrencia…
│   │   │   ├── enums/              situações, perfis, turnos
│   │   │   ├── vo/                 Pagina, PaginaRequest, ResumoOcorrencias
│   │   │   └── exception/          exceções de negócio
│   │   └── usecase/                orquestração + validações
│   └── port/
│       ├── in/                     o que a aplicação oferece (use cases)
│       └── out/                    o que a aplicação exige (repos, token, eventos…)
│
├── adapter/                        ← BORDAS
│   ├── in/web/                     controllers, DTOs, mappers, filtros, handler de erro
│   └── out/
│       ├── persistence/            entidades JPA, Spring Data, mappers
│       ├── security/               JWT, BCrypt
│       ├── ratelimit/              Bucket4j
│       ├── messaging/              RabbitMQ (ou log)
│       └── transaction/            unidade de trabalho
│
└── config/                         ← RAIZ DE COMPOSIÇÃO (só o Spring mora aqui)
```

**A regra é uma só: as setas apontam para dentro.** Os adaptadores conhecem a
aplicação; a aplicação conhece apenas as próprias portas.

Isso é verificado automaticamente de duas formas — veja
[Verificação da arquitetura](#-verificação-da-arquitetura).

---

## 🚀 Como rodar

### Pré-requisitos

- **Java 25**
- **Maven 3.9+** (ou use o `./mvnw` incluso)
- **Docker + Docker Compose** (caminho mais rápido)
- **PostgreSQL 16** (se for rodar sem Docker)

### Com Docker (recomendado)

```bash
cp Projeto-do-senai/.env.example Projeto-do-senai/.env
# edite o .env: defina POSTGRES_PASSWORD e um JWT_SECRET de 32+ caracteres
#   openssl rand -base64 48

cd Projeto-do-senai
docker compose up --build
```

A API sobe em `http://localhost:8080`. Para incluir o RabbitMQ:

```bash
docker compose --profile messaging up --build
```

### Sem Docker

```bash
createdb ocorrenciaescolar

export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ocorrenciaescolar
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=sua-senha
export JWT_SECRET=$(openssl rand -base64 48)

cd Projeto-do-senai
./mvnw spring-boot:run
```

O Flyway aplica as migrations na subida. O primeiro acesso usa o administrador
criado na migration `V1`:

| Login | Senha |
|---|---|
| `admin@sistema.com` | *(a definida na carga inicial — troque no primeiro acesso)* |

### Configuração

Tudo por variável de ambiente; os padrões estão em `application.yml`.

| Variável | Padrão | Para que serve |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/ocorrenciaescolar` | Conexão com o banco |
| `SPRING_DATASOURCE_USERNAME` / `_PASSWORD` | `postgres` | Credenciais do banco |
| `JWT_SECRET` | — **obrigatório** | Assinatura do token (mín. 32 caracteres) |
| `JWT_EXPIRATION_HOURS` | `2` | Validade do token |
| `APP_CORS_ALLOWED_ORIGINS` | `http://127.0.0.1:5500,http://localhost:5500` | Origens do front |
| `APP_TRUSTED_PROXY` | `false` | Confiar em `X-Forwarded-For` (só atrás de proxy reverso) |
| `LOGIN_RATE_LIMIT_TENTATIVAS` | `5` | Tentativas de login por janela |
| `LOGIN_RATE_LIMIT_JANELA` | `1m` | Duração da janela |
| `APP_RABBIT_ENABLED` | `false` | Publicar eventos no RabbitMQ |

---

## 🔌 API

Documentação interativa em `http://localhost:8080/swagger-ui.html`.

Toda requisição (exceto `POST /login`) exige `Authorization: Bearer <token>`.

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/login` | `{login, password}` → `{tokenJWT, id, login, role}` |

### Usuários — `ADMIN`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/users` | Cadastrar |
| `GET` | `/users` | Listar ativos (paginado) |
| `GET` | `/users/inactive` | Listar inativos |
| `GET` | `/users/roles` | Perfis disponíveis |
| `GET` | `/users/{id}` | Detalhar |
| `PUT` | `/users` | Atualizar (parcial) |
| `PATCH` | `/users` | Alterar perfil de acesso |
| `PATCH` | `/users/password` | Trocar senha *(qualquer autenticado, só a própria)* |
| `DELETE` | `/users/{id}` | Inativar |
| `PATCH` | `/users/{id}/reactivate` | Reativar |

### Alunos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/students` | Cadastrar |
| `GET` | `/students` | Listar ativos (paginado) |
| `GET` | `/students/inactive` | Listar inativos |
| `GET` | `/students/{id}` | Detalhar |
| `PUT` | `/students` | Atualizar (parcial) |
| `PATCH` | `/students/{id}/image` | Enviar foto (multipart, campo `image`) |
| `DELETE` | `/students/{id}` | Inativar |
| `PATCH` | `/students/{id}/reactivate` | Reativar |

### Turmas

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/schoolclasses` | Cadastrar |
| `GET` | `/schoolclasses` | Listar ativas (paginado) |
| `GET` | `/schoolclasses/canceled` | Listar canceladas |
| `GET` | `/schoolclasses/shifts` · `/semesters` | Turnos e semestres |
| `GET` | `/schoolclasses/{id}` | Detalhar |
| `PUT` | `/schoolclasses` | Atualizar (parcial) |
| `DELETE` | `/schoolclasses/{id}` | Cancelar |
| `PATCH` | `/schoolclasses/{id}/reactivate` | Reativar |
| `POST` | `/schoolclasses/{id}/students` | Matricular aluno |
| `GET` | `/schoolclasses/{id}/students` | Listar alunos da turma |

### Ocorrências

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/incidents` | Registrar |
| `GET` | `/incidents` | Listar (paginado, exclui canceladas) |
| `GET` | `/incidents/{id}` | Detalhar |
| `PUT` | `/incidents` | Atualizar (parcial) |
| `PATCH` | `/incidents/status` | Mudar a situação |
| `DELETE` | `/incidents/{id}` | Cancelar (exclusão lógica) |
| `GET` | `/incidents/status` | Situações possíveis |
| `GET` | `/incidents/status/{slug}` | Filtrar por situação |
| `GET` | `/incidents/categories` · `/types/{categoria}` | Catálogo |
| `GET` | `/incidents/summary` | Resumo do dashboard |
| `GET` | `/incidents/students/{id}/history` | Histórico do aluno |

### Máquina de estados da ocorrência

```
                    ┌──────────────┐
                    │  AGUARDANDO  │  (waiting)
                    └──────┬───────┘
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ ATENDENDO│◄►│  ATIVA   │ │ FECHADA  │ ← final
        └────┬─────┘ └────┬─────┘ └──────────┘
             └──────┬─────┘
          ┌─────────┴─────────┐
          ▼                   ▼
   ┌─────────────┐   ┌────────────────┐
   │  RESOLVIDA  │   │ NAO_RESOLVIDA  │  ← finais
   └─────────────┘   └────────────────┘
```

Situações finais não voltam atrás. Cancelamento é ortogonal ao status: marca
`deleted` e some das listagens, mas o registro fica no histórico.

### Formato de erro

Todas as respostas de erro têm o mesmo corpo:

```json
{
  "timestamp": "2026-08-27T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Aluno Bruno Lima nao esta matriculado na turma DS-01",
  "path": "/incidents",
  "fields": [{ "field": "description", "message": "Descricao e' obrigatoria" }]
}
```

---

## 🧪 Testes

```bash
cd Projeto-do-senai
./mvnw test
```

**186 testes** distribuídos em:

| Camada | O que cobre |
|---|---|
| **Domínio** (`domain/`) | Regras puras: máquina de estados, capacidade da turma, política de senha |
| **Casos de uso** (`usecase/`) | Orquestração contra dublês em memória das portas |
| **Integração** (`integration/`) | Contexto Spring completo, MockMvc, banco H2 |
| **Contrato** (`ContratoDoFrontEndIT`) | Rotas, campos e formatos que o front realmente consome |
| **Segurança** (`security/`) | JWT, perfis, rate limit, CORS, vazamento em erros |
| **BDD** (`bdd/`) | 17 cenários Gherkin em português |
| **Arquitetura** (`architecture/`) | 14 regras ArchUnit |
| **Desempenho** (`performance/`) | Contagem de SQL (N+1), latência, concorrência |

Relatório BDD em `target/cucumber-report.html`.

Para pular os testes de desempenho (úteis mas sensíveis à máquina):

```bash
./mvnw test -Dgroups='!performance'
```

---

## 🔍 Verificação da arquitetura

Duas ferramentas complementares:

### 1. Teste ArchUnit — roda no build

```bash
./mvnw test -Dtest=ArquiteturaHexagonalTest
```

Quebra o build quando um import atravessa uma fronteira proibida.

### 2. Scanner de linha de comando — varre os fontes

```bash
python3 tools/arch_scan.py                      # relatório no terminal
python3 tools/arch_scan.py --html relatorio.html
python3 tools/arch_scan.py --json saida.json    # para consumo por CI
python3 tools/arch_scan.py --strict             # avisos também falham
```

O scanner lê os `.java` diretamente, então:

- aponta **arquivo e linha** do import que viola a regra;
- desenha o **mapa das camadas** e a matriz de dependências;
- funciona **mesmo com o projeto sem compilar**;
- as regras vivem em `tools/arch_rules.json` — editar o JSON muda o que é cobrado,
  sem tocar no código do scanner.

Saída:

```
CAMADAS
Dominio                      43 arquivo(s)   Modelos, enums, value objects e excecoes
  Casos de uso               14 arquivo(s)   Orquestracao das regras de negocio
  ...

DEPENDENCIAS ENTRE CAMADAS
  Casos de uso             --> Dominio                  (55)
  Adaptadores de entrada   --> Portas de entrada        (19)
  --> para dentro ou lateral (ok)   ==> para fora (violacao)

VIOLACOES
  Nenhuma violacao encontrada.
```

Regras cobertas: direção das dependências (`HEX-*`), isolamento de framework
(`FWK-*`), convenções de pacote e nomenclatura (`NOM-*`) e higiene de código
(`HIG-*`).

---

## 📦 Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.0 (WebMVC, Security, Data JPA, Validation, Actuator) |
| Banco | PostgreSQL 16 + Flyway |
| Token | java-jwt (HMAC-256) |
| Rate limit | Bucket4j |
| Mensageria | Spring AMQP / RabbitMQ *(opcional)* |
| Documentação | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, MockMvc, H2, ArchUnit, Cucumber |

---

## 📁 Estrutura do repositório

```
.
├── Projeto-do-senai/          Aplicação Spring Boot
│   ├── src/main/java/         Código
│   ├── src/main/resources/    application.yml + migrations Flyway
│   ├── src/test/java/         Testes (unidade, integração, BDD, arquitetura, carga)
│   ├── src/test/resources/    application-test.yml + features Gherkin
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── .env.example
├── tools/
│   ├── arch_scan.py           Scanner de arquitetura
│   └── arch_rules.json        Definição das camadas e regras
└── .github/workflows/ci.yml   Arquitetura · testes · migrations
```
