# Manual do Desenvolvedor - Shrimp Farm Management

## Índice

1. [Configuração do Ambiente](#configuração-do-ambiente)
2. [Estrutura do Projeto](#estrutura-do-projeto)
3. [Convenções de Código](#convenções-de-código)
4. [Fluxo de Desenvolvimento](#fluxo-de-desenvolvimento)
5. [Criando Novas Features](#criando-novas-features)
6. [Padrões de Commit](#padrões-de-commit)
7. [Testes](#testes)
8. [Banco de Dados e Migrations](#banco-de-dados-e-migrations)
9. [Debugging](#debugging)
10. [Troubleshooting](#troubleshooting)

---

## Configuração do Ambiente

### Requisitos

Certifique-se de ter instalado:

- **Java 21 JDK** (OpenJDK ou Oracle)
- **Maven 3.9+**
- **Docker** e **Docker Compose**
- **Git**
- **IDE recomendada**: IntelliJ IDEA, Eclipse ou VS Code

### Instalação do Java 21

**Linux/macOS (SDKMAN):**
curl -s "https://get.sdkman.io" | bash
sdk install java 21.0.1-open
sdk use java 21.0.1-open

**Windows:**
Baixe o instalador do [OpenJDK 21](https://adoptium.net/) e configure a variável `JAVA_HOME`.

### Verificação

java -version # deve mostrar Java 21
mvn -version # deve mostrar Maven 3.9+
docker --version

### Clone e Setup Inicial

Clone o repositório
git clone https://github.com/josivantarcio/shrimp-farm.git
cd shrimp-farm

Instale as dependências
mvn clean install -DskipTests

Suba o banco de dados
docker-compose up -d

Execute a aplicação
mvn spring-boot:run

A aplicação estará rodando em `http://localhost:8080`.

---

## Estrutura do Projeto

shrimp-farm/
├── docs/ # Documentação
│ ├── TECHNICAL_DOCUMENTATION.md
│ └── DEVELOPER_MANUAL.md
├── src/
│ ├── main/
│ │ ├── java/com/jtarcio/shrimpfarm/
│ │ │ ├── api/ # Controllers REST
│ │ │ │ └── controller/
│ │ │ │ ├── LoteController.java
│ │ │ │ ├── BiometriaController.java
│ │ │ │ └── ...
│ │ │ ├── application/ # Casos de uso
│ │ │ │ ├── dto/
│ │ │ │ │ ├── request/
│ │ │ │ │ └── response/
│ │ │ │ ├── mapper/
│ │ │ │ └── service/
│ │ │ ├── domain/ # Núcleo de negócio
│ │ │ │ ├── entity/
│ │ │ │ ├── enums/
│ │ │ │ ├── exception/
│ │ │ │ └── valueobject/
│ │ │ ├── infrastructure/ # Infraestrutura
│ │ │ │ ├── config/
│ │ │ │ └── persistence/
│ │ │ └── ShrimpFarmApplication.java
│ │ └── resources/
│ │ ├── db/migration/ # Scripts Flyway
│ │ ├── application.yml
│ │ └── application-test.yml
│ └── test/
│ └── java/com/jtarcio/shrimpfarm/
│ ├── api/controller/ # Testes de integração
│ └── application/service/ # Testes unitários
├── target/ # Gerado pelo Maven
├── docker-compose.yml
├── pom.xml
└── README.md

### Responsabilidades das Camadas

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| **API** | `api.controller` | Expor endpoints REST, validar entrada HTTP |
| **Application** | `application.service` | Casos de uso, orquestração de domínio |
| **Domain** | `domain.entity` | Regras de negócio, entidades, exceções |
| **Infrastructure** | `infrastructure.persistence` | Acesso a dados, configurações técnicas |

---

## Convenções de Código

### Nomenclatura

**Classes:**
- Entidades: `Lote`, `Fazenda`, `Biometria`
- Services: `LoteService`, `BiometriaService`
- Controllers: `LoteController`, `RelatorioController`
- DTOs: `LoteRequest`, `LoteResponse`
- Repositories: `LoteRepository`, `ViveiroRepository`
- Mappers: `LoteMapper`, `BiometriaMapper`

**Métodos:**
- CRUD: `criar()`, `buscarPorId()`, `atualizar()`, `deletar()`
- Queries: `listarPorStatus()`, `buscarPorCodigo()`
- Lógica: `calcularBiomassa()`, `finalizarLote()`

**Variáveis:**
- CamelCase: `pesoMedio`, `biomassaEstimada`
- Constantes: `VALOR_MAXIMO`, `STATUS_ATIVO`

### Formatação

- **Indentação**: 4 espaços (não tabs)
- **Linhas**: Máximo 120 caracteres
- **Imports**: Organize e remova não utilizados
- **Lombok**: Prefira `@RequiredArgsConstructor` sobre `@Autowired`

### Annotations

**Ordem de annotations em classes:**
@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {
// ...
}

**Ordem de annotations em métodos:**
@Transactional
@Override
public LoteResponse criar(LoteRequest request) {
// ...
}

### Comentários

Evite comentários óbvios. Use apenas quando necessário:

// BOM
// Calcula biomassa: população * peso médio / 1000 (kg)
BigDecimal biomassa = populacao.multiply(pesoMedio).divide(BigDecimal.valueOf(1000));

// RUIM
// Busca lote por ID
Lote lote = loteRepository.findById(id).orElseThrow();

---

## Fluxo de Desenvolvimento

### 1. Criar Branch

Sempre derive da branch principal atualizada
git checkout main
git pull origin main

Crie uma branch seguindo o padrão
git checkout -b feature/nome-da-feature

ou
git checkout -b fix/nome-do-bug

### 2. Desenvolver

- Escreva o código seguindo as convenções
- Adicione testes (unitários e/ou integração)
- Execute os testes localmente
- Commit incremental com mensagens descritivas

### 3. Testar

Testes unitários + integração
mvn test

Cobertura de código
mvn clean test jacoco:report

Verificar relatório em target/site/jacoco/index.html

### 4. Commit e Push

git add .
git commit -m "feat: adiciona endpoint de despesca"
git push origin feature/nome-da-feature

### 5. Pull Request

- Abra PR no GitHub
- Descreva as mudanças
- Referencie a issue: `Closes #25`
- Aguarde review e aprovação

---

## Criando Novas Features

### Exemplo: Adicionar Entidade "Fornecedor"

#### 1. Criar Entidade

`domain/entity/Fornecedor.java`:
@Entity
@Table(name = "fornecedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fornecedor {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, length = 100)
private String nome;

@Column(unique = true, length = 14)
private String cnpj;

@Column(length = 15)
private String telefone;

@Column(name = "ativo", nullable = false)
private Boolean ativo = true;
}

#### 2. Criar Migration

`db/migration/V13__criar_tabela_fornecedores.sql`:
CREATE TABLE fornecedores (
id BIGSERIAL PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
cnpj VARCHAR(14) UNIQUE,
telefone VARCHAR(15),
ativo BOOLEAN NOT NULL DEFAULT true
);

#### 3. Criar Repository

`infrastructure/persistence/FornecedorRepository.java`:
@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

Optional<Fornecedor> findByCnpj(String cnpj);

List<Fornecedor> findByAtivoTrue();
}

#### 4. Criar DTOs

`application/dto/request/FornecedorRequest.java`:
@Data
public class FornecedorRequest {

@NotBlank(message = "Nome é obrigatório")
@Size(max = 100)
private String nome;

@Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos")
private String cnpj;

@Pattern(regexp = "\\d{10,11}", message = "Telefone inválido")
private String telefone;
}

`application/dto/response/FornecedorResponse.java`:
@Data
@Builder
public class FornecedorResponse {
private Long id;
private String nome;
private String cnpj;
private String telefone;
private Boolean ativo;
}

#### 5. Criar Mapper

`application/mapper/FornecedorMapper.java`:
@Mapper(componentModel = "spring")
public interface FornecedorMapper {

FornecedorResponse toResponse(Fornecedor fornecedor);

Fornecedor toEntity(FornecedorRequest request);
}

#### 6. Criar Service

`application/service/FornecedorService.java`:
@Service
@RequiredArgsConstructor
@Slf4j
public class FornecedorService {

private final FornecedorRepository fornecedorRepository;
private final FornecedorMapper fornecedorMapper;

@Transactional
public FornecedorResponse criar(FornecedorRequest request) {
log.info("Criando fornecedor: {}", request.getNome());

    Fornecedor fornecedor = fornecedorMapper.toEntity(request);
    fornecedor = fornecedorRepository.save(fornecedor);
    
    return fornecedorMapper.toResponse(fornecedor);
}

@Transactional(readOnly = true)
public List<FornecedorResponse> listarAtivos() {
return fornecedorRepository.findByAtivoTrue()
.stream()
.map(fornecedorMapper::toResponse)
.collect(Collectors.toList());
}
}

#### 7. Criar Controller

`api/controller/FornecedorController.java`:
@RestController
@RequestMapping("/v1/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

private final FornecedorService fornecedorService;

@PostMapping
public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
FornecedorResponse response = fornecedorService.criar(request);
return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@GetMapping
public ResponseEntity<List<FornecedorResponse>> listarAtivos() {
return ResponseEntity.ok(fornecedorService.listarAtivos());
}
}

#### 8. Criar Testes

`test/.../FornecedorControllerIntegrationTest.java`:
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FornecedorControllerIntegrationTest extends BaseIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Test
void deveCriarFornecedorComSucesso() throws Exception {
String json = """
{
"nome": "Ração Potimar",
"cnpj": "12345678000199",
"telefone": "84999999999"
}
""";

    mockMvc.perform(post("/v1/fornecedores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nome").value("Ração Potimar"));
}
}

---

## Padrões de Commit

Siga o padrão **Conventional Commits**:

### Formato

<tipo>(<escopo>): <descrição curta>

[corpo opcional]

[rodapé opcional]

### Tipos

- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Alteração em documentação
- `style`: Formatação (sem mudança de lógica)
- `refactor`: Refatoração (sem mudança de funcionalidade)
- `test`: Adição ou correção de testes
- `chore`: Manutenção (deps, config, build)

### Exemplos

feat(lote): adiciona endpoint de finalização de lote

fix(biometria): corrige cálculo de biomassa quando peso é zero

docs: atualiza README com instruções de instalação

test(racao): adiciona testes de integração do endpoint de ração

refactor(service): extrai lógica de validação para método privado

chore(deps): atualiza Spring Boot para 3.5.8

### Referenciando Issues

feat(despesca): implementa registro de despesca. Closes #24

fix(viveiro): corrige validação de status. Fixes #32

---

## Testes

### Estrutura de Testes

src/test/java/
├── api/controller/ # Testes de integração (controllers)
│ ├── LoteControllerIntegrationTest.java
│ └── BiometriaControllerIntegrationTest.java
└── application/service/ # Testes unitários (services)
├── LoteServiceTest.java
└── BiometriaServiceTest.java

### Teste Unitário (Service)

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

@Mock
private LoteRepository loteRepository;

@Mock
private ViveiroRepository viveiroRepository;

@InjectMocks
private LoteService loteService;

@Test
void deveLancarExcecaoQuandoViveiroNaoExiste() {
// given
LoteRequest request = new LoteRequest();
request.setViveiroId(999L);

    when(viveiroRepository.findById(999L)).thenReturn(Optional.empty());
    
    // when/then
    assertThrows(EntityNotFoundException.class, 
        () -> loteService.criar(request));
}
}

### Teste de Integração (Controller)

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoteControllerIntegrationTest extends BaseIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private LoteRepository loteRepository;

@Test
void deveCriarLoteComSucesso() throws Exception {
String json = """
{
"viveiroId": 1,
"codigo": "LOTE001",
"dataPovoamento": "2025-01-01",
"quantidadePosLarvas": 50000
}
""";

    mockMvc.perform(post("/v1/lotes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.codigo").value("LOTE001"));
}
}

### Executar Testes

Todos os testes
mvn test

Apenas testes de integração
mvn test -Dtest=*IntegrationTest

Apenas testes unitários
mvn test -Dtest=*Test

Com cobertura
mvn clean test jacoco:report

---

## Banco de Dados e Migrations

### Criar Nova Migration

1. Crie o arquivo SQL em `src/main/resources/db/migration/`:

V<numero>__<descricao>.sql

Exemplo: `V13__adicionar_coluna_observacao_lote.sql`

2. Escreva o SQL:

ALTER TABLE lotes ADD COLUMN observacoes TEXT;

3. Execute a aplicação:

mvn spring-boot:run

O Flyway aplicará automaticamente a migration.

### Rollback de Migration

**Atenção**: Flyway não faz rollback automático. Para reverter:

1. Crie uma nova migration com o comando reverso:

`V14__remover_coluna_observacao_lote.sql`:
ALTER TABLE lotes DROP COLUMN observacoes;

### Verificar Status

mvn flyway:info

### Limpar Banco (Desenvolvimento)

docker-compose down -v
docker-compose up -d
mvn spring-boot:run

---

## Debugging

### IntelliJ IDEA

1. Configure breakpoints no código
2. Execute em modo debug: **Run → Debug 'ShrimpFarmApplication'**
3. Use o debug remoto se necessário (porta 5005)

### Logs

Ative logs detalhados em `application.yml`:

logging:
level:
com.jtarcio.shrimpfarm: DEBUG
org.hibernate.SQL: DEBUG
org.hibernate.type.descriptor.sql.BasicBinder: TRACE

### Postman/Insomnia

Importe a collection (se disponível) ou crie requests manualmente:

**Exemplo: Criar Lote**
POST http://localhost:8080/v1/lotes
Content-Type: application/json

{
"viveiroId": 1,
"codigo": "LOTE001",
"dataPovoamento": "2025-01-01",
"quantidadePosLarvas": 50000
}

---

## Troubleshooting

### Erro: "Port 8080 already in use"

Linux/macOS
lsof -i :8080
kill -9 <PID>

Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

### Erro: "Connection refused" (PostgreSQL)

Verifique se o container está rodando
docker ps

Se não estiver, suba novamente
docker-compose up -d

Verifique os logs
docker-compose logs postgres

### Erro: "Table doesn't exist"

Limpe o banco e recrie
docker-compose down -v
docker-compose up -d
mvn spring-boot:run


### Erro: "Flyway migration failed"

Verifique o histórico
mvn flyway:info

Se necessário, repare manualmente
mvn flyway:repair


### Testes falhando

Limpe o cache do Maven
mvn clean

Execute novamente
mvn test

Se persistir, verifique Testcontainers
docker ps -a

---

## Recursos Adicionais

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [MapStruct Guide](https://mapstruct.org/documentation/stable/reference/html/)

---

**Versão:** 1.0  
**Última atualização:** 03/12/2025  
**Autor:** Josevan Oliveira