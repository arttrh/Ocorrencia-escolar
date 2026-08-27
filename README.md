# 🏫 Sistema de Ocorrência Escolar

<div align="center">
  <img src="./Nano Banana 2 - Animate this pixel art isometric school scene with the following movementsStudents.png" alt="Sistema Escolar" width="500">
  
  **Um sistema completo para gerenciar ocorrências e registros escolares**
  
  ![Java](https://img.shields.io/badge/Java-17+-ED8936?style=for-the-badge&logo=java)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3+-6DB33F?style=for-the-badge&logo=spring)
  ![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
</div>

---

## 📋 Sobre o Projeto

O **Sistema de Ocorrência Escolar** é uma aplicação desenvolvida em **Java com Spring Boot** para gerenciar e registrar ocorrências escolares, turmas e alunos de forma eficiente e centralizada.

### ✨ Funcionalidades

- ✅ Cadastro e gerenciamento de turmas
- ✅ Controle de alunos e suas informações
- ✅ Registro e acompanhamento de ocorrências
- ✅ Sistema de vinculação entre alunos e turmas
- ✅ Autenticação e controle de acesso
- ✅ Rate limiting para proteção da API
- ✅ Paginação e filtros avançados

---

## 🚀 Como Rodar

### Pré-requisitos

- **Java 17+**
- **Maven 3.8+**
- **Docker e Docker Compose** (opcional)
- **PostgreSQL 16**

### Instalação Local

#### 1. Clone o repositório

```bash
git clone https://github.com/arttrh/Ocorrencia-escolar.git
cd Ocorrencia-escolar
```

#### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis de sistema:

```env
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/escola
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha

# Autenticação
JWT_SECRET=uma-chave-longa-e-aleatoria

# Server
SERVER_PORT=8080
```

#### 3. Navegue para a pasta do projeto

```bash
cd Projeto-do-senai
```

#### 4. Compile o projeto com Maven

```bash
mvn clean install
```

#### 5. Execute a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

---

### 🐳 Rodando com Docker

#### 1. Build da imagem

```bash
docker build -t sistema-escolar .
```

#### 2. Execute o container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/escola \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  sistema-escolar
```

#### 3. Ou use Docker Compose (se disponível)

```bash
docker-compose up -d
```

---

## 📁 Estrutura do Projeto

```
Projeto-do-senai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/project_sena/
│   │   │       ├── adapter/
│   │   │       │   ├── in/          # Controllers e DTOs
│   │   │       │   └── out/         # Repositórios e Mappers
│   │   │       ├── application/
│   │   │       │   ├── core/        # Domain models e use cases
│   │   │       │   └── port/        # Interfaces
│   │   │       └── config/          # Configurações (Security, Rate Limit)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── Dockerfile
```

---

## 🔌 Endpoints Principais

### Autenticação
- `POST /login` - Autentica e devolve o JWT

### Turmas
- `POST /turmas/cadastrar` - Criar nova turma
- `GET /turmas/listar/ativo` - Listar turmas ativas
- `GET /turmas/listar/inativo` - Listar turmas inativas
- `GET /turmas/detalhar/{id}` - Detalhes de uma turma
- `PUT /turmas/atualizar/{id}` - Atualizar turma
- `DELETE /turmas/excluir/{id}` - Desativar turma
- `PATCH /turmas/reativar/{id}` - Reativar turma
- `POST /turmas/vincular/aluno/{id}` - Vincular aluno à turma

### Alunos
- `POST /aluno/cadastrar` - Cadastrar aluno
- `GET /aluno/ativos` - Listar alunos ativos
- `GET /aluno/inativos` - Listar alunos inativos
- `GET /aluno/{id}` - Detalhes de um aluno
- `PUT /aluno/atualizar/{id}` - Atualizar aluno
- `DELETE /aluno/delete/{id}` - Desativar aluno
- `PATCH /aluno/reativar/{id}` - Reativar aluno

### Usuários
- `POST /usuario/cadastrar` - Cadastrar usuário
- `GET /usuario/ativos` · `GET /usuario/inativos` - Listagens
- `GET /usuario/{id}` - Detalhes
- `PUT /usuario/atualizar/{id}` - Atualizar
- `DELETE /usuario/delete/{id}` - Desativar
- `PATCH /usuario/reativar/{id}` - Reativar

> Nada é apagado de verdade: `DELETE` muda o enum de status da entidade, e por
> isso todo recurso tem listagem de ativos, de inativos e uma rota de reativação.

---

## 🔐 Autenticação

O sistema implementa segurança com:
- **Spring Security + JWT** — autenticação stateless, sem sessão no servidor
- **Seis perfis de acesso**: `PROFESSOR`, `ANALISTA`, `COORDENADOR`, `PROFESSOR_ADMINISTRATIVO`, `ADMINISTRATIVO` e `ADMIN`
- **Rate limiting** com Bucket4j num filtro de entrada: 5 requisições por minuto, repostas de uma vez
- **Validação de entrada** nos DTOs de todos os endpoints

Autenticação em `POST /login`, que devolve o token. As demais rotas esperam
`Authorization: Bearer <token>`.

---

## 📊 Banco de Dados

O schema é versionado com **Flyway** em `src/main/resources/db/migration` (8
migrations), e o Hibernate roda com `ddl-auto: validate` — quem cria tabela é a
migration, não o framework.

### Tabelas principais

```sql
-- Turmas (Classes)
CREATE TABLE class (
    id_class BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    teacher_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Alunos (Students)
CREATE TABLE student (
    id_student BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    photo VARCHAR(255),
    date_birth DATE,
    aluno_enum VARCHAR(50),
    id_class BIGINT REFERENCES class(id_class)
);
```

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 17+ | Linguagem principal |
| Spring Boot | 3.x | Framework |
| Spring Security | 6.x | Autenticação |
| JPA/Hibernate | 6.x | ORM |
| PostgreSQL | 16 | Banco de dados |
| Flyway | - | Migrations versionadas |
| Bucket4j | - | Rate limiting |
| java-jwt | - | Emissão e validação do token |
| springdoc-openapi | - | Swagger UI |
| Docker | Latest | Containerização |
| Maven | 3.8+ | Gerenciador de dependências |

---

## 📝 Exemplos de Uso

### Criar uma nova turma

```bash
curl -X POST http://localhost:8080/turmas/cadastrar \
  -H "Content-Type: application/json" \
  -d '{
    "name": "9º Ano A",
    "teacher_id": 1,
    "status": "ATIVO"
  }'
```

### Listar turmas ativas

```bash
curl http://localhost:8080/turmas/listar/ativo?page=0&size=10
```

### Obter detalhes de uma turma

```bash
curl http://localhost:8080/turmas/detalhar/1
```

---

## 🐛 Troubleshooting

### Erro: "Connection refused" ao banco de dados
- Certifique-se de que o PostgreSQL está rodando
- Verifique as credenciais no arquivo `.env`

### Erro de validação de schema na subida
- O `ddl-auto` é `validate`: se as tabelas não existirem, a aplicação não sobe
- Deixe o Flyway rodar as migrations, ou derrube o volume (`docker compose down -v`) e suba de novo

### Erro 429 nas requisições
- É o rate limit: 5 requisições por minuto. Espere a janela virar.

### Erro de compilação Java
- Limpe o cache: `mvn clean`
- Verifique a versão do Java: `java -version`
- Deve ser Java 17 ou superior

### Porta 8080 já em uso
- Altere a porta em `application.yml`:
  ```yaml
  server:
    port: 8081
  ```

---

## 📚 Documentação Adicional

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)
- [Docker Documentation](https://docs.docker.com/)

---

## 👨‍💻 Desenvolvimento

### Padrões de Código

Este projeto segue **arquitetura hexagonal** (ports & adapters):

- **application/core**: entidades de domínio, casos de uso e validações — não importa Spring
- **application/port**: interfaces de entrada (o que pedem ao domínio) e de saída (o que o domínio precisa)
- **adapter/in**: controllers REST, requests e responses
- **adapter/out**: persistência JPA, entidades e mappers
- **config**: security, rate limit, RabbitMQ

O ponto do desenho é esse: trocar PostgreSQL por outro banco, ou REST por outro
transporte, é mexer só em `adapter/`.

### Executar Testes

```bash
mvn test
```

---

## 👤 Autor

**Arthur Lucas**  
GitHub: [@arttrh](https://github.com/arttrh)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

<div align="center">
  
**Desenvolvido com ❤️ para fins educacionais - SENAI**

⭐ Se este projeto foi útil, deixe uma estrela!

</div>
