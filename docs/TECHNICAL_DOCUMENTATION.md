# Documentação Técnica - Shrimp Farm Management

## Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Modelo de Domínio](#modelo-de-domínio)
4. [Camadas da Aplicação](#camadas-da-aplicação)
5. [Padrões de Projeto](#padrões-de-projeto)
6. [Banco de Dados](#banco-de-dados)
7. [Segurança e Validações](#segurança-e-validações)
8. [Testes](#testes)
9. [Decisões Técnicas](#decisões-técnicas)

---

## Visão Geral

O **Shrimp Farm Management** é um sistema backend REST API desenvolvido para gerenciar o ciclo completo de cultivo de camarão em fazendas aquícolas. A aplicação foi projetada seguindo princípios de Clean Architecture e Domain-Driven Design (DDD).

### Objetivos do Sistema

- Controlar fazendas, viveiros e lotes de camarão
- Registrar e monitorar biometrias (peso, sobrevivência, biomassa)
- Gerenciar aplicação de ração, nutrientes e fertilização
- Calcular custos operacionais por lote
- Gerar relatórios de desempenho e KPIs

### Requisitos Não-Funcionais

- **Performance**: Resposta < 500ms para 95% das requisições
- **Disponibilidade**: 99.5% uptime
- **Escalabilidade**: Suporte a múltiplas fazendas e centenas de lotes simultâneos
- **Manutenibilidade**: Código testável e bem documentado

---

## Arquitetura

O projeto utiliza **Arquitetura Hexagonal** (Ports and Adapters), separando o domínio de negócio das preocupações técnicas.

### Diagrama de Camadas

┌─────────────────────────────────────────┐
│ API Layer (Controllers) │ ← Adaptadores de entrada (HTTP)
├─────────────────────────────────────────┤
│ Application Layer (Services, DTOs) │ ← Casos de uso e orquestração
├─────────────────────────────────────────┤
│ Domain Layer (Entities, Rules) │ ← Núcleo de negócio
├─────────────────────────────────────────┤
│ Infrastructure (Repositories, Config) │ ← Adaptadores de saída (DB, etc)
└─────────────────────────────────────────┘

### Fluxo de Requisição

1. **Controller** recebe requisição HTTP e valida entrada
2. **Service** orquestra lógica de negócio
3. **Repository** acessa banco de dados via JPA
4. **Mapper** converte entre Entity e DTO
5. **Controller** retorna response HTTP

---

## Modelo de Domínio

### Entidades Principais

#### Fazenda (Fazenda)
Representa uma unidade produtiva de camarão.

**Atributos:**
- `id`: Long (PK)
- `nome`: String
- `proprietario`: String
- `cidade`: String
- `estado`: String (2 caracteres)
- `ativa`: Boolean

**Relacionamentos:**
- 1:N com Viveiro

#### Viveiro (Viveiro)
Tanque ou área de cultivo dentro de uma fazenda.

**Atributos:**
- `id`: Long (PK)
- `fazenda`: Fazenda (FK)
- `codigo`: String (único)
- `nome`: String
- `area`: BigDecimal (hectares)
- `profundidade`: BigDecimal (metros)
- `status`: StatusViveiroEnum (DISPONIVEL, OCUPADO, MANUTENCAO)
- `ativo`: Boolean

**Relacionamentos:**
- N:1 com Fazenda
- 1:N com Lote

#### Lote (Lote)
Ciclo de cultivo de camarão em um viveiro.

**Atributos:**
- `id`: Long (PK)
- `viveiro`: Viveiro (FK)
- `codigo`: String (único)
- `dataPovoamento`: LocalDate
- `dataPrevisaoDespesca`: LocalDate
- `quantidadePosLarvas`: Integer
- `densidadeInicial`: BigDecimal
- `status`: StatusLoteEnum (ATIVO, FINALIZADO, CANCELADO)

**Relacionamentos:**
- N:1 com Viveiro
- 1:N com Biometria, Racao, Nutriente, Fertilizacao, CustoVariavel, Despesca

#### Biometria (Biometria)
Medição de crescimento do camarão.

**Atributos:**
- `id`: Long (PK)
- `lote`: Lote (FK)
- `dataBiometria`: LocalDate
- `pesoMedio`: BigDecimal (gramas)
- `populacaoEstimada`: Integer
- `biomassaEstimada`: BigDecimal (kg)
- `sobrevivenciaEstimada`: BigDecimal (%)
- `tamanhoAmostra`: Integer

**Regras de Negócio:**
- Data não pode ser anterior ao povoamento
- Peso médio deve ser positivo
- Biomassa calculada automaticamente: `populacao * pesoMedio / 1000`

#### Racao (Racao)
Aplicação de ração no lote.

**Atributos:**
- `id`: Long (PK)
- `lote`: Lote (FK)
- `fornecedor`: Fornecedor (FK, opcional)
- `dataAplicacao`: LocalDate
- `tipoRacao`: TipoRacaoEnum
- `marca`: String
- `quantidade`: BigDecimal (kg)
- `custoUnitario`: BigDecimal
- `custoTotal`: BigDecimal (calculado automaticamente)
- `proteinaPercentual`: BigDecimal

**Cálculo Automático:**
@PrePersist
@PreUpdate
protected void calcularCustoTotal() {
if (quantidade != null && custoUnitario != null) {
this.custoTotal = quantidade.multiply(custoUnitario);
}
}

#### Despesca (Despesca)
Registro de colheita do camarão.

**Atributos:**
- `id`: Long (PK)
- `lote`: Lote (FK)
- `comprador`: Comprador (FK, opcional)
- `dataDespesca`: LocalDate
- `quantidadeKg`: BigDecimal
- `precoMedioKg`: BigDecimal
- `receitaTotal`: BigDecimal (calculado)
- `taxaSobrevivencia`: BigDecimal (%)

---

## Camadas da Aplicação

### 1. API Layer (`api/controller`)

Responsável por expor endpoints REST e gerenciar requisições HTTP.

**Responsabilidades:**
- Receber e validar requisições
- Invocar services apropriados
- Retornar responses padronizadas
- Tratar exceções HTTP

**Exemplo:**
@RestController
@RequestMapping("/v1/lotes")
@RequiredArgsConstructor
public class LoteController {
private final LoteService loteService;

@PostMapping
public ResponseEntity<LoteResponse> criar(@Valid @RequestBody LoteRequest request) {
LoteResponse response = loteService.criar(request);
return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
}

### 2. Application Layer (`application/service`)

Contém a lógica de aplicação e orquestra operações de domínio.

**Responsabilidades:**
- Implementar casos de uso
- Coordenar múltiplas entidades
- Aplicar regras de negócio transversais
- Gerenciar transações

**Componentes:**
- **Services**: Lógica de negócio
- **DTOs** (Request/Response): Contratos de API
- **Mappers**: Conversão entre Entity e DTO

**Exemplo de Service:**
@Service
@RequiredArgsConstructor
@Transactional
public class LoteService {
private final LoteRepository loteRepository;
private final ViveiroRepository viveiroRepository;
private final LoteMapper loteMapper;

public LoteResponse criar(LoteRequest request) {
// Valida viveiro
Viveiro viveiro = viveiroRepository.findById(request.getViveiroId())
.orElseThrow(() -> new EntityNotFoundException("Viveiro", request.getViveiroId()));

    // Valida disponibilidade
    if (viveiro.getStatus() == StatusViveiroEnum.OCUPADO) {
        throw new BusinessException("Viveiro já está ocupado");
    }
    
    // Cria lote
    Lote lote = loteMapper.toEntity(request, viveiro);
    lote = loteRepository.save(lote);
    
    // Atualiza status do viveiro
    viveiro.setStatus(StatusViveiroEnum.OCUPADO);
    viveiroRepository.save(viveiro);
    
    return loteMapper.toResponse(lote);
}
}

### 3. Domain Layer (`domain/entity`)

Núcleo da aplicação contendo entidades e regras de negócio.

**Responsabilidades:**
- Definir modelo de domínio
- Encapsular regras de negócio
- Garantir invariantes de domínio

**Componentes:**
- **Entities**: Entidades JPA
- **Enums**: Tipos enumerados de domínio
- **Exceptions**: Exceções de negócio
- **Value Objects**: Objetos de valor (ex: Dinheiro)

### 4. Infrastructure Layer (`infrastructure/persistence`)

Implementa adaptadores de saída (banco de dados, cache, etc).

**Responsabilidades:**
- Acesso a dados via JPA
- Queries customizadas
- Configurações técnicas

**Exemplo de Repository:**
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

List<Lote> findByStatus(StatusLoteEnum status);

Optional<Lote> findByCodigo(String codigo);

@Query("SELECT l FROM Lote l WHERE l.viveiro.fazenda.id = :fazendaId")
List<Lote> findByFazendaId(@Param("fazendaId") Long fazendaId);
}

---

## Padrões de Projeto

### DTO (Data Transfer Object)

Objetos imutáveis para transferência de dados entre camadas.

**Request DTO:**
@Data
public class LoteRequest {

@NotNull(message = "Viveiro é obrigatório")
private Long viveiroId;

@NotBlank(message = "Código é obrigatório")
@Size(max = 50)
private String codigo;

@NotNull(message = "Data de povoamento é obrigatória")
@PastOrPresent
private LocalDate dataPovoamento;

@NotNull
@Min(1000)
private Integer quantidadePosLarvas;
}

### Mapper Pattern (MapStruct)

Conversão automática entre Entity e DTO.

@Mapper(componentModel = "spring")
public interface LoteMapper {

@Mapping(target = "viveiroId", source = "viveiro.id")
@Mapping(target = "viveiroNome", source = "viveiro.nome")
LoteResponse toResponse(Lote lote);

@Mapping(target = "id", ignore = true)
@Mapping(target = "viveiro", source = "viveiro")
Lote toEntity(LoteRequest request, Viveiro viveiro);
}

### Repository Pattern

Abstração de acesso a dados seguindo interface do Spring Data JPA.

### Service Layer Pattern

Centralização de lógica de negócio em services transacionais.

---

## Banco de Dados

### Tecnologia

- **PostgreSQL 15**: Banco de dados relacional
- **Flyway**: Versionamento de schema
- **Hibernate/JPA**: ORM

### Estratégia de Migrations

Arquivos SQL em `src/main/resources/db/migration`:

V1__criar_tabela_usuarios.sql
V2__criar_tabela_fornecedores.sql
V3__criar_tabela_compradores.sql
V4__criar_tabela_fazendas.sql
V5__criar_tabela_viveiros.sql
V6__criar_tabela_lotes.sql
V7__criar_tabela_biometrias.sql
V8__criar_tabela_racoes.sql
V9__criar_tabela_nutrientes.sql
V10__criar_tabela_fertilizacoes.sql
V11__criar_tabela_custos_variaveis.sql
V12__criar_tabela_despescas.sql

### Exemplo de Migration

-- V6__criar_tabela_lotes.sql
CREATE TABLE lotes (
id BIGSERIAL PRIMARY KEY,
viveiro_id BIGINT NOT NULL,
codigo VARCHAR(50) NOT NULL UNIQUE,
data_povoamento DATE NOT NULL,
data_previsao_despesca DATE,
quantidade_pos_larvas INTEGER NOT NULL,
densidade_inicial DECIMAL(10,2),
status VARCHAR(20) NOT NULL,
data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
data_atualizacao TIMESTAMP,
CONSTRAINT fk_lotes_viveiro FOREIGN KEY (viveiro_id)
REFERENCES viveiros(id) ON DELETE RESTRICT
);

CREATE INDEX idx_lotes_viveiro ON lotes(viveiro_id);
CREATE INDEX idx_lotes_status ON lotes(status);

### Queries Customizadas

Para relatórios complexos, são usadas queries JPQL ou SQL nativo:

@Query("""
SELECT COALESCE(SUM(r.custoTotal), 0)
FROM Racao r
WHERE r.lote.id = :loteId
""")
BigDecimal calcularCustoTotalRacaoByLoteId(@Param("loteId") Long loteId);

---

## Segurança e Validações

### Validação de Entrada

Uso de **Bean Validation** (JSR-380) nos DTOs:

@NotNull(message = "Campo obrigatório")
@Min(value = 1, message = "Valor deve ser positivo")
@Email(message = "Email inválido")
@Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ter 2 letras")

### Tratamento de Exceções

Handler global para padronizar respostas de erro:

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
ErrorResponse error = new ErrorResponse(
HttpStatus.NOT_FOUND.value(),
ex.getMessage(),
LocalDateTime.now()
);
return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
}

### Regras de Negócio

Validações customizadas nos services:

- Lote só pode ser criado em viveiro disponível
- Biometria não pode ter data anterior ao povoamento
- Despesca só pode ser registrada em lotes ativos
- Ração só pode ser aplicada em lotes ativos

---

## Testes

### Pirâmide de Testes


/\
/E2E\        (Planejado)
/------\
/  IT   \      (Integration Tests)
/----------\
/ Unit \ (Unit Tests)
/--------------\

### Testes Unitários

- **Framework**: JUnit 5 + Mockito
- **Cobertura**: Services e mappers
- **Objetivo**: Testar lógica de negócio isolada

**Exemplo:**
@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

@Mock
private LoteRepository loteRepository;

@InjectMocks
private LoteService loteService;

@Test
void deveCriarLoteComSucesso() {
// given
LoteRequest request = new LoteRequest();
// ...

    // when
    LoteResponse response = loteService.criar(request);
    
    // then
    assertNotNull(response);
    verify(loteRepository).save(any(Lote.class));
}
}

### Testes de Integração

- **Framework**: JUnit 5 + Testcontainers
- **Escopo**: Controllers + Services + Repositories
- **Banco**: PostgreSQL em container Docker

**Configuração:**
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class LoteControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Container
static PostgreSQLContainer<?> postgres =
new PostgreSQLContainer<>("postgres:15-alpine");
}

### Cobertura de Código

- **Ferramenta**: JaCoCo
- **Meta**: > 80% de cobertura
- **Comando**: `mvn clean test jacoco:report`

---

## Decisões Técnicas

### Por que Arquitetura Hexagonal?

- Separação clara entre domínio e infraestrutura
- Facilita testes unitários (isolamento de dependências)
- Permite trocar adaptadores sem afetar o núcleo
- Promove código limpo e manutenível

### Por que Spring Boot 3.x?

- Suporte a Java 21 e records
- Performance melhorada
- Observabilidade nativa (Micrometer)
- Ecossistema maduro e bem documentado

### Por que PostgreSQL?

- Suporte robusto a tipos de dados (numeric, date, arrays)
- Performance excelente para consultas complexas
- Suporte a JSON para flexibilidade futura
- Open source e amplamente adotado

### Por que Testcontainers?

- Testes de integração com banco real (não in-memory)
- Isolamento total entre testes
- Reproduz ambiente de produção
- Elimina dependência de banco externo

### Por que MapStruct?

- Performance superior a reflection-based mappers
- Geração de código em compile-time
- Type-safe
- Reduz boilerplate manual

### Por que Flyway?

- Versionamento declarativo de schema
- Auditoria de mudanças no banco
- Rollback controlado
- Suporte a múltiplos ambientes

---

## Melhorias Futuras

### Curto Prazo
- Implementar autenticação e autorização (Spring Security + JWT)
- Adicionar cache (Redis) para relatórios
- Implementar paginação em todas as listagens

### Médio Prazo
- Adicionar observabilidade (logs estruturados, métricas, tracing)
- Implementar API de notificações (alertas de baixa sobrevivência)
- Criar dashboard web (frontend React ou Angular)

### Longo Prazo
- Migrar para microserviços (separar módulos por contexto)
- Implementar CQRS para relatórios complexos
- Adicionar machine learning para previsão de despesca

---

## Referências

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design (Eric Evans)](https://www.domainlanguage.com/ddd/)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Versão:** 1.0  
**Última atualização:** 03/12/2025  
**Autor:** Josevan Oliveira
Onde colocar
Crie a estrutura:

bash
mkdir -p docs
touch docs/TECHNICAL_DOCUMENTATION.md
E cole o conteúdo acima.

Depois adicione ao commit:

bash
git add docs/TECHNICAL_DOCUMENTATION.md
git commit -m "docs: adiciona documentação técnica detalhada. Refs #30"