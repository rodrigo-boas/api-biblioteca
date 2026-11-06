# API Biblioteca

API REST para gerenciar uma biblioteca: cadastro de livros e membros e controle de empréstimos e devoluções, com regras de negócio sobre estoque e limite de empréstimos por membro.

Projeto de estudo feito para praticar Spring Boot, JPA e testes unitários.

## Tecnologias

- Java 21
- Spring Boot 4.1 (Web, Data JPA, Validation)
- PostgreSQL 18 (via Docker Compose)
- MapStruct (conversão entre entidades e DTOs)
- Lombok
- JUnit 5 e Mockito
- Maven (com Maven Wrapper)

## Como rodar

Pré-requisitos: Java 21 e Docker.

```bash
# 1. subir o banco de dados
docker compose up -d

# 2. rodar a aplicação
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

A API fica disponível em `http://localhost:8080`. As tabelas são criadas automaticamente na primeira execução.

Para parar o banco: `docker compose down`. Para apagar também os dados: `docker compose down -v`.

### Testes

```bash
./mvnw test
```

O teste de contexto (`BibliotecaApplicationTests`) conecta no banco, então o `docker compose up -d` precisa estar rodando antes.

## Endpoints

### Livros

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/livros` | Lista todos os livros |
| GET | `/livros/{id}` | Busca um livro pelo id |
| POST | `/livros/cadastrar` | Cadastra um livro |
| POST | `/livros/cadastrar/varios` | Cadastra vários livros de uma vez |
| PUT | `/livros/{id}` | Atualiza um livro |
| DELETE | `/livros/{id}` | Remove um livro |

### Membros

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/membros` | Lista todos os membros |
| GET | `/membros/{id}` | Busca um membro pelo id |
| POST | `/membros/cadastrar` | Cadastra um membro |
| POST | `/membros/cadastrar/varios` | Cadastra vários membros de uma vez |
| PUT | `/membros/{id}` | Atualiza um membro |
| DELETE | `/membros/{id}` | Remove um membro |

### Empréstimos

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/emprestimos` | Realiza um empréstimo |
| GET | `/emprestimos?membroId={id}` | Lista os empréstimos de um membro |
| PATCH | `/emprestimos/{id}/devolucao` | Registra a devolução |

## Códigos de resposta

| Situação | Código |
|----------|--------|
| Listagem ou busca por id | `200 OK` |
| Cadastro de livro ou membro (único ou em lote) | `201 Created` |
| Atualização, remoção, empréstimo e devolução | `204 No Content` |
| Dados inválidos (campo vazio, e-mail inválido, quantidade negativa, data no passado) | `400 Bad Request` |
| Empréstimo não permitido (sem estoque, limite de 3 empréstimos, já devolvido) | `400 Bad Request` |
| Livro, membro ou empréstimo não encontrado | `404 Not Found` |
| E-mail já cadastrado, ou remoção de livro/membro que possui empréstimos | `409 Conflict` |

## Exemplos de requisição

Cadastrar um livro:

```json
POST /livros/cadastrar

{
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "isbn": "9780132350884",
  "quantidade": 3
}
```

Cadastrar um membro:

```json
POST /membros/cadastrar

{
  "nome": "Ana Souza",
  "email": "ana@email.com",
  "senha": "minhasenha123"
}
```

Realizar um empréstimo:

```json
POST /emprestimos

{
  "idLivro": 1,
  "idMembro": 1,
  "dataPrevistaDevolucao": "2026-12-01"
}
```

## Regras de negócio

- Um livro só pode ser emprestado se houver exemplares em estoque.
- Cada membro pode ter no máximo **3 empréstimos ativos** ao mesmo tempo.
- Ao realizar um empréstimo, o estoque do livro diminui em 1. Ao registrar a devolução, volta a aumentar em 1.
- Um empréstimo já devolvido não pode ser devolvido de novo.
- O e-mail de cada membro é único, inclusive no cadastro em lote (a lista não pode ter e-mails repetidos).
- Um livro ou membro que já tem empréstimos registrados (ativos ou devolvidos) não pode ser removido, para preservar o histórico. A API responde com `409 Conflict`.
- Os e-mails são normalizados antes de salvar (espaços removidos e letras minúsculas), então `Ana@Email.com` e `ana@email.com` contam como o mesmo e-mail.
- A senha do membro é guardada com hash BCrypt e nunca é retornada nas respostas.

### Validações

- Livro: título, autor e ISBN obrigatórios; a quantidade não pode ser negativa.
- Membro: nome, e-mail (em formato válido) e senha obrigatórios.
- Empréstimo: `idLivro`, `idMembro` e `dataPrevistaDevolucao` obrigatórios; a data prevista não pode estar no passado.

## Estrutura do projeto

```
src/main/java/exercicio/api
├── config       # configuração de beans (PasswordEncoder com BCrypt)
├── controller   # endpoints REST
├── service      # regras de negócio
├── repository   # acesso ao banco (Spring Data JPA)
├── model        # entidades JPA e enums
├── dto          # objetos de requisição e resposta
├── mapper       # conversão com MapStruct
└── exceptions   # exceções de negócio
```

## Limitações conhecidas

- A API não tem autenticação nem autorização.
- Não há controle de concorrência no estoque: duas requisições simultâneas para o último exemplar podem ambas passar pela validação.
- Os testes cobrem services e mappers; ainda não há testes de controller.