package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FertilizacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FertilizacaoResponse;
import com.jtarcio.shrimpfarm.application.mapper.FertilizacaoMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FertilizacaoRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
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
@DisplayName("FertilizacaoService - Testes Unitários")
class FertilizacaoServiceTest {

    @Mock
    private FertilizacaoRepository fertilizacaoRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FertilizacaoMapper fertilizacaoMapper;

    @InjectMocks
    private FertilizacaoService fertilizacaoService;

    private Lote lote;
    private Fornecedor fornecedor;
    private Fertilizacao fertilizacao;
    private FertilizacaoRequest request;
    private FertilizacaoResponse response;

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

        fertilizacao = Fertilizacao.builder()
                .id(1L)
                .lote(lote)
                .fornecedor(fornecedor)
                .produto("Ureia")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("50.00"))
                .custoTotal(new BigDecimal("500.00"))
                .build();

        request = FertilizacaoRequest.builder()
                .loteId(1L)
                .fornecedorId(1L)
                .produto("Ureia")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("50.00"))
                .build();

        response = FertilizacaoResponse.builder()
                .id(1L)
                .loteId(1L)
                .fornecedorNome("Fornecedor Teste")
                .produto("Ureia")
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("50.00"))
                .custoTotal(new BigDecimal("500.00"))
                .build();
    }

    @Test
    @DisplayName("Deve criar fertilização com sucesso")
    void deveCriarFertilizacaoComSucesso() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fertilizacaoMapper.toEntity(request, lote, fornecedor)).thenReturn(fertilizacao);
        when(fertilizacaoRepository.save(any(Fertilizacao.class))).thenReturn(fertilizacao);
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        // Act
        FertilizacaoResponse resultado = fertilizacaoService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getProduto()).isEqualTo("Ureia");
        assertThat(resultado.getCustoTotal()).isEqualByComparingTo("500.00");
        verify(fertilizacaoRepository).save(any(Fertilizacao.class));
    }

    @Test
    @DisplayName("Deve criar fertilização sem fornecedor")
    void deveCriarFertilizacaoSemFornecedor() {
        // Arrange
        request.setFornecedorId(null);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fertilizacaoMapper.toEntity(request, lote, null)).thenReturn(fertilizacao);
        when(fertilizacaoRepository.save(any(Fertilizacao.class))).thenReturn(fertilizacao);
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        // Act
        FertilizacaoResponse resultado = fertilizacaoService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fornecedorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar fertilização em lote não ativo/planejado")
    void deveLancarExcecaoAoCriarEmLoteNaoAtivo() {
        // Arrange
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act & Assert
        assertThatThrownBy(() -> fertilizacaoService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Só é possível registrar fertilização em lotes ativos ou planejados");

        verify(fertilizacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar fertilização por ID")
    void deveBuscarFertilizacaoPorId() {
        // Arrange
        when(fertilizacaoRepository.findById(1L)).thenReturn(Optional.of(fertilizacao));
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        // Act
        FertilizacaoResponse resultado = fertilizacaoService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve listar fertilizações por lote")
    void deveListarFertilizacoesPorLote() {
        // Arrange
        when(loteRepository.existsById(1L)).thenReturn(true);
        when(fertilizacaoRepository.findByLoteIdOrderByDataAplicacaoAsc(1L))
                .thenReturn(Arrays.asList(fertilizacao));
        when(fertilizacaoMapper.toResponse(any(Fertilizacao.class))).thenReturn(response);

        // Act
        List<FertilizacaoResponse> resultado = fertilizacaoService.listarPorLote(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(loteRepository).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar fertilizações de lote inexistente")
    void deveLancarExcecaoAoListarPorLoteInexistente() {
        // Arrange
        when(loteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> fertilizacaoService.listarPorLote(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote");
    }

    @Test
    @DisplayName("Deve calcular custo total de fertilizações")
    void deveCalcularCustoTotal() {
        // Arrange
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.00"));

        // Act
        BigDecimal resultado = fertilizacaoService.calcularCustoTotalPorLote(1L);

        // Assert
        assertThat(resultado).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Deve retornar zero quando não há fertilizações")
    void deveRetornarZeroQuandoNaoHaFertilizacoes() {
        // Arrange
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L)).thenReturn(null);

        // Act
        BigDecimal resultado = fertilizacaoService.calcularCustoTotalPorLote(1L);

        // Assert
        assertThat(resultado).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve listar fertilizações paginadas")
    void deveListarFertilizacoesPaginadas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Fertilizacao> page = new PageImpl<>(Arrays.asList(fertilizacao));
        when(fertilizacaoRepository.findAll(pageable)).thenReturn(page);
        when(fertilizacaoMapper.toResponse(any(Fertilizacao.class))).thenReturn(response);

        // Act
        Page<FertilizacaoResponse> resultado = fertilizacaoService.listarPaginado(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar fertilização com sucesso")
    void deveAtualizarFertilizacaoComSucesso() {
        // Arrange
        when(fertilizacaoRepository.findById(1L)).thenReturn(Optional.of(fertilizacao));
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fertilizacaoRepository.save(any(Fertilizacao.class))).thenReturn(fertilizacao);
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        // Act
        FertilizacaoResponse resultado = fertilizacaoService.atualizar(1L, request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fertilizacaoMapper).updateEntity(fertilizacao, request, lote, fornecedor);
        verify(fertilizacaoRepository).save(fertilizacao);
    }

    @Test
    @DisplayName("Deve deletar fertilização com sucesso")
    void deveDeletarFertilizacaoComSucesso() {
        // Arrange
        when(fertilizacaoRepository.findById(1L)).thenReturn(Optional.of(fertilizacao));

        // Act
        fertilizacaoService.deletar(1L);

        // Assert
        verify(fertilizacaoRepository).delete(fertilizacao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar fertilização inexistente")
    void deveLancarExcecaoAoDeletarFertilizacaoInexistente() {
        // Arrange
        when(fertilizacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fertilizacaoService.deletar(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve aceitar fertilização em lote planejado")
    void deveAceitarFertilizacaoEmLotePlanejado() {
        // Arrange
        lote.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fertilizacaoMapper.toEntity(request, lote, fornecedor)).thenReturn(fertilizacao);
        when(fertilizacaoRepository.save(any(Fertilizacao.class))).thenReturn(fertilizacao);
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        // Act
        FertilizacaoResponse resultado = fertilizacaoService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fertilizacaoRepository).save(any(Fertilizacao.class));
    }
}
