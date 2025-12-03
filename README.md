# 🦐 Shrimp Farm Management System

Sistema de gerenciamento de fazendas de camarão desenvolvido com Spring Boot, utilizando arquitetura hexagonal e boas práticas de engenharia de software.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.9+-red?logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker-required-blue?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Sobre o Projeto

O **Shrimp Farm Management** é uma aplicação backend REST API para gerenciamento completo do ciclo de cultivo de camarão, incluindo:

- Gestão de fazendas, viveiros e lotes
- Controle de povoamento e despesca
- Registro de biometrias e monitoramento de crescimento
- Aplicação de ração, nutrientes e fertilização
- Controle de custos variáveis
- Relatórios de custos por lote e KPIs do dashboard
- Cadastro de fornecedores e compradores

## Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.8** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Validation** - Validação de entrada
- **Hibernate 6.6** - ORM

### Banco de Dados
- **PostgreSQL 15** - Banco de dados relacional
- **Flyway** - Versionamento e migração de schema

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks e stubs
- **Testcontainers** - Testes de integração com containers Docker
- **JaCoCo** - Cobertura de código

### Ferramentas
- **Lombok** - Redução de boilerplate
- **MapStruct 1.6** - Mapeamento entre DTOs e entidades
- **Maven** - Gerenciamento de dependências e build

## Pré-requisitos

Certifique-se de ter instalado:

- **Java 21** ou superior
- **Maven 3.9+**
- **Docker** e **Docker Compose** (para executar PostgreSQL)
- **Git**

## Instalação e Execução

### 1. Clone o repositório

git clone https://github.com/josivantarcio/shrimp-farm.git
cd shrimp-farm

### 2. Configure o banco de dados

Inicie o PostgreSQL via Docker Compose:

docker-compose up -d

O banco será criado automaticamente na porta `5432` com as seguintes credenciais:

- **Database:** `shrimpfarm`
- **Usuário:** `postgres`
- **Senha:** `postgres`

### 3. Execute a aplicação

mvn spring-boot:run

A aplicação estará disponível em `http://localhost:8080`.

### 4. Acesse os endpoints

Use ferramentas como **Postman**, **Insomnia** ou **cURL** para testar os endpoints REST.

Exemplo:

curl http://localhost:8080/v1/fazendas

## Estrutura do Projeto

O projeto segue a **arquitetura hexagonal** (ports and adapters):

src/
├── main/
│ ├── java/com/jtarcio/shrimpfarm/
│ │ ├── api/ # Camada de API (Controllers)
│ │ ├── application/ # Casos de uso (Services, DTOs, Mappers)
│ │ ├── domain/ # Modelo de domínio (Entidades, Enums, Exceptions)
│ │ ├── infrastructure/ # Infraestrutura (Repositories, Configurações)
│ │ └── ShrimpFarmApplication.java
│ └── resources/
│ ├── db/migration/ # Scripts Flyway
│ ├── application.yml
│ └── application-test.yml
└── test/
└── java/com/jtarcio/shrimpfarm/
└── api/controller/ # Testes de integração dos controllers

## Endpoints Principais

### Fazendas
- `GET /v1/fazendas` - Lista todas as fazendas
- `POST /v1/fazendas` - Cria uma nova fazenda
- `GET /v1/fazendas/{id}` - Busca fazenda por ID
- `PUT /v1/fazendas/{id}` - Atualiza uma fazenda
- `DELETE /v1/fazendas/{id}` - Remove uma fazenda

### Viveiros
- `GET /v1/viveiros` - Lista todos os viveiros
- `POST /v1/viveiros` - Cria um novo viveiro
- `GET /v1/viveiros/{id}` - Busca viveiro por ID
- `PUT /v1/viveiros/{id}` - Atualiza um viveiro

### Lotes
- `GET /v1/lotes` - Lista todos os lotes
- `POST /v1/lotes` - Cria um novo lote
- `GET /v1/lotes/{id}` - Busca lote por ID
- `PUT /v1/lotes/{id}` - Atualiza um lote
- `POST /v1/lotes/{id}/finalizar` - Finaliza um lote

### Biometrias
- `POST /v1/biometrias` - Registra uma biometria
- `GET /v1/biometrias/lote/{loteId}` - Lista biometrias de um lote

### Rações
- `POST /v1/racoes` - Registra aplicação de ração
- `GET /v1/racoes/lote/{loteId}` - Lista rações de um lote

### Relatórios
- `GET /v1/relatorios/dashboard` - KPIs gerais do dashboard
- `GET /v1/relatorios/lotes/{loteId}/custos` - Relatório de custos de um lote
- `GET /v1/relatorios/lotes/ativos/custos` - Relatórios de todos os lotes ativos

## Testes

### Executar todos os testes

mvn test

### Executar apenas testes de integração

mvn test -Dtest=*IntegrationTest

### Gerar relatório de cobertura (JaCoCo)

mvn clean test jacoco:report

O relatório estará disponível em `target/site/jacoco/index.html`.

### Gerar relatório de testes (Surefire)

mvn test surefire-report:report

O relatório estará disponível em `target/site/surefire-report.html`.

## Variáveis de Ambiente

O arquivo `application.yml` possui as seguintes configurações principais:

spring:
datasource:
url: jdbc:postgresql://localhost:5432/shrimpfarm
username: postgres
password: postgres
jpa:
hibernate:
ddl-auto: validate
flyway:
enabled: true

Para ambientes diferentes (dev, staging, production), crie arquivos `application-{profile}.yml`.

## Migrations (Flyway)

Os scripts SQL estão em `src/main/resources/db/migration` e seguem o padrão:

V1__criar_tabela_usuarios.sql
V2__criar_tabela_fornecedores.sql
...

Para criar uma nova migração, adicione um novo arquivo SQL seguindo a numeração sequencial.

## Contribuindo

Contribuições são bem-vindas. Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/minha-feature`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova feature'`)
4. Push para a branch (`git push origin feature/minha-feature`)
5. Abra um Pull Request

Siga os padrões de commit do [Conventional Commits](https://www.conventionalcommits.org/).

## Autor

**Josevan Oliveira**

- GitHub: [@josivantarcio](https://github.com/josivantarcio)

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## Contato

Para dúvidas ou sugestões, abra uma issue no repositório.
