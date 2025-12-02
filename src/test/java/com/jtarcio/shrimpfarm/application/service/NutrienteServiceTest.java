package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.NutrienteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.NutrienteResponse;
import com.jtarcio.shrimpfarm.application.mapper.NutrienteMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.NutrienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NutrienteService - Testes Unitários")
class NutrienteServiceTest {

    @Mock
    private NutrienteRepository nutrienteRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private NutrienteMapper nutrienteMapper;

    @InjectMocks
    private NutrienteService nutrienteService;

    private Lote lote;
    private Fornecedor fornecedor;
    private Nutriente nutriente;
    private NutrienteRequest request;
    private NutrienteResponse response;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(1L)
                .status(StatusLoteEnum.ATIVO)
                .dataPovoamento(LocalDate.now().minusDays(10))
                .build();

        fornecedor = Fornecedor.builder()
                .id(1L)
                .nome("Fornecedor Teste")
                .build();

        nutriente = Nutriente.builder()
                .id(1L)
                .lote(lote)
                .fornecedor(fornecedor)
                .produto("Vitamina C")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.00"))
                .custoTotal(new BigDecimal("200.00"))
                .build();

        request = NutrienteRequest.builder()
                .loteId(1L)
                .fornecedorId(1L)
                .produto("Vitamina C")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.00"))
                .build();

        response = NutrienteResponse.builder()
                .id(1L)
                .loteId(1L)
                .fornecedorNome("Fornecedor Teste")
                .produto("Vitamina C")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.00"))
                .custoTotal(new BigDecimal("200.00"))
                .build();
    }

    @Test
    @DisplayName("Deve criar nutriente com sucesso")
    void deveCriarNutrienteComSucesso() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(nutrienteMapper.toEntity(request, lote, fornecedor)).thenReturn(nutriente);
        when(nutrienteRepository.save(any(Nutriente.class))).thenReturn(nutriente);
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        // Act
        NutrienteResponse resultado = nutrienteService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getProduto()).isEqualTo("Vitamina C");
        assertThat(resultado.getCustoTotal()).isEqualByComparingTo("200.00");
        verify(nutrienteRepository).save(any(Nutriente.class));
    }

    @Test
    @DisplayName("Deve criar nutriente sem fornecedor")
    void deveCriarNutrienteSemFornecedor() {
        // Arrange
        request.setFornecedorId(null);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(nutrienteMapper.toEntity(request, lote, null)).thenReturn(nutriente);
        when(nutrienteRepository.save(any(Nutriente.class))).thenReturn(nutriente);
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        // Act
        NutrienteResponse resultado = nutrienteService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fornecedorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar nutriente em lote não ativo")
    void deveLancarExcecaoAoCriarEmLoteNaoAtivo() {
        // Arrange
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act & Assert
        assertThatThrownBy(() -> nutrienteService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Só é possível registrar nutrientes em lotes ativos");

        verify(nutrienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar nutriente com data anterior ao povoamento")
    void deveLancarExcecaoDataAnteriorPovoamento() {
        // Arrange
        request.setDataAplicacao(LocalDate.now().minusDays(20));
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act & Assert
        assertThatThrownBy(() -> nutrienteService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Data de aplicação não pode ser anterior ao povoamento");
    }

    @Test
    @DisplayName("Deve buscar nutriente por ID")
    void deveBuscarNutrientePorId() {
        // Arrange
        when(nutrienteRepository.findById(1L)).thenReturn(Optional.of(nutriente));
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        // Act
        NutrienteResponse resultado = nutrienteService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar nutriente inexistente")
    void deveLancarExcecaoAoBuscarNutrienteInexistente() {
        // Arrange
        when(nutrienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> nutrienteService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nutriente");
    }

    @Test
    @DisplayName("Deve listar nutrientes por lote")
    void deveListarNutrientesPorLote() {
        // Arrange
        when(loteRepository.existsById(1L)).thenReturn(true);
        when(nutrienteRepository.findByLoteIdOrderByDataAplicacaoAsc(1L))
                .thenReturn(Arrays.asList(nutriente));
        when(nutrienteMapper.toResponse(any(Nutriente.class))).thenReturn(response);

        // Act
        List<NutrienteResponse> resultado = nutrienteService.listarPorLote(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(loteRepository).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar nutrientes de lote inexistente")
    void deveLancarExcecaoAoListarPorLoteInexistente() {
        // Arrange
        when(loteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> nutrienteService.listarPorLote(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote");
    }

    @Test
    @DisplayName("Deve calcular custo total de nutrientes")
    void deveCalcularCustoTotal() {
        // Arrange
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("800.00"));

        // Act
        BigDecimal resultado = nutrienteService.calcularCustoTotalPorLote(1L);

        // Assert
        assertThat(resultado).isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("Deve retornar zero quando não há nutrientes")
    void deveRetornarZeroQuandoNaoHaNutrientes() {
        // Arrange
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L)).thenReturn(null);

        // Act
        BigDecimal resultado = nutrienteService.calcularCustoTotalPorLote(1L);

        // Assert
        assertThat(resultado).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve listar nutrientes paginados")
    void deveListarNutrientesPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Nutriente> page = new PageImpl<>(Arrays.asList(nutriente));
        when(nutrienteRepository.findAll(pageable)).thenReturn(page);
        when(nutrienteMapper.toResponse(any(Nutriente.class))).thenReturn(response);

        // Act
        Page<NutrienteResponse> resultado = nutrienteService.listarPaginado(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar nutriente com sucesso")
    void deveAtualizarNutrienteComSucesso() {
        // Arrange
        when(nutrienteRepository.findById(1L)).thenReturn(Optional.of(nutriente));
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(nutrienteRepository.save(any(Nutriente.class))).thenReturn(nutriente);
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        // Act
        NutrienteResponse resultado = nutrienteService.atualizar(1L, request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(nutrienteMapper).updateEntity(nutriente, request, lote, fornecedor);
        verify(nutrienteRepository).save(nutriente);
    }

    @Test
    @DisplayName("Deve deletar nutriente com sucesso")
    void deveDeletarNutrienteComSucesso() {
        // Arrange
        when(nutrienteRepository.findById(1L)).thenReturn(Optional.of(nutriente));

        // Act
        nutrienteService.deletar(1L);

        // Assert
        verify(nutrienteRepository).delete(nutriente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar nutriente inexistente")
    void deveLancarExcecaoAoDeletarNutrienteInexistente() {
        // Arrange
        when(nutrienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> nutrienteService.deletar(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
