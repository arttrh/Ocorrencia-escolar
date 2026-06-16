# 🏫 Sistema de Ocorrência Escolar

<div align="center">
  <img src="https://user-images.githubusercontent.com/your-username/school-system.png" alt="Sistema Escolar" width="500">
  
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
- **PostgreSQL 14+** (ou outro banco de dados configurado)

### Instalação Local

#### 1. Clone o repositório

```bash
git clone https://github.com/rthurlucas/Sistema-de-ocorr-ncia-escolar.git
cd Sistema-de-ocorr-ncia-escolar
```

#### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis de sistema:

```env
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/escola
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update

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

### Turmas
- `POST /turmas/cadastrar` - Criar nova turma
- `GET /turmas/listar/ativo` - Listar turmas ativas
- `GET /turmas/listar/inativo` - Listar turmas inativas
- `GET /turmas/detalhar/{id}` - Detalhes de uma turma
- `PUT /turmas/atualizar/{id}` - Atualizar turma
- `DELETE /turmas/excluir/{id}` - Deletar turma
- `PATCH /turmas/reativar/{id}` - Reativar turma
- `POST /turmas/vincular/{id}/{id}` - Vincular aluno à turma

---

## 🔐 Autenticação

O sistema implementa segurança com:
- **Spring Security** para autenticação
- **Rate Limiting** para proteção contra abuso
- **Validação de entrada** em todos os endpoints

---

## 📊 Banco de Dados

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
| PostgreSQL | 14+ | Banco de dados |
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

Este projeto segue **Clean Architecture** com camadas bem definidas:

- **Adapter In**: Controllers e DTOs de entrada
- **Adapter Out**: Repositórios e mapeadores
- **Application Core**: Casos de uso e entidades de domínio
- **Config**: Configurações globais

### Executar Testes

```bash
mvn test
```

---

## 📄 Licença

Este projeto é licenciado sob a [MIT License](LICENSE) - veja o arquivo LICENSE para mais detalhes.

---

## 👤 Autor

**Raul Lucas**  
GitHub: [@rthurlucas](https://github.com/rthurlucas)

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
