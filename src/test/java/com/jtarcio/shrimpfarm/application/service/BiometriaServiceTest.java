package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.application.mapper.BiometriaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.RacaoRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BiometriaService - Testes Unitários")
class BiometriaServiceTest {

    @Mock
    private BiometriaRepository biometriaRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private RacaoRepository racaoRepository;

    @Mock
    private BiometriaMapper biometriaMapper;

    @InjectMocks
    private BiometriaService biometriaService;

    private Lote lote;
    private Biometria biometria;
    private BiometriaRequest request;
    private BiometriaResponse response;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(1L)
                .codigo("LOTE-001")
                .status(StatusLoteEnum.ATIVO)
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(100000)
                .build();

        biometria = Biometria.builder()
                .id(1L)
                .lote(lote)
                .dataBiometria(LocalDate.now())
                .diaCultivo(30)
                .pesoMedio(new BigDecimal("12.500"))
                .quantidadeAmostrada(100)
                .pesoTotalAmostra(new BigDecimal("1250.000"))
                .ganhoPesoDiario(new BigDecimal("0.4167"))
                .biomassaEstimada(new BigDecimal("1000.00"))
                .sobrevivenciaEstimada(new BigDecimal("80.00"))
                .fatorConversaoAlimentar(new BigDecimal("1.500"))
                .observacoes("Biometria padrão")
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        request = BiometriaRequest.builder()
                .loteId(1L)
                .dataBiometria(LocalDate.now())
                .pesoMedio(new BigDecimal("12.500"))
                .quantidadeAmostrada(100)
                .pesoTotalAmostra(new BigDecimal("1250.000"))
                .observacoes("Biometria padrão")
                .build();

        response = BiometriaResponse.builder()
                .id(1L)
                .loteId(1L)
                .loteCodigo("LOTE-001")
                .dataBiometria(LocalDate.now())
                .diaCultivo(30)
                .pesoMedio(new BigDecimal("12.500"))
                .quantidadeAmostrada(100)
                .pesoTotalAmostra(new BigDecimal("1250.000"))
                .ganhoPesoDiario(new BigDecimal("0.4167"))
                .biomassaEstimada(new BigDecimal("1000.00"))
                .sobrevivenciaEstimada(new BigDecimal("80.00"))
                .fatorConversaoAlimentar(new BigDecimal("1.500"))
                .observacoes("Biometria padrão")
                .build();
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Deve criar biometria com sucesso")
    void deveCriarBiometriaComSucesso() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPesoMedio()).isEqualByComparingTo("12.500");
        assertThat(resultado.getDiaCultivo()).isEqualTo(30);
        verify(biometriaRepository).save(any(Biometria.class));
        verify(loteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar biometria com lote inexistente")
    void deveLancarExcecaoAoCriarBiometriaComLoteInexistente() {
        // Arrange
        when(loteRepository.findById(999L)).thenReturn(Optional.empty());
        request.setLoteId(999L);

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.criar(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote");

        verify(biometriaRepository, never()).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar biometria em lote finalizado")
    void deveLancarExcecaoAoCriarBiometriaEmLoteFinalizado() {
        // Arrange
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("lotes ativos ou planejados");

        verify(biometriaRepository, never()).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar biometria com data anterior ao povoamento")
    void deveLancarExcecaoAoCriarBiometriaComDataAnteriorAoPovoamento() {
        // Arrange
        request.setDataBiometria(lote.getDataPovoamento().minusDays(1));
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pode ser anterior à data de povoamento");

        verify(biometriaRepository, never()).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve criar biometria mesmo com divergência pequena no peso total")
    void deveCriarBiometriaComDivergenciaPequenaNopesoTotal() {
        // Arrange - 3% de diferença (dentro da margem de 5%)
        request.setPesoTotalAmostra(new BigDecimal("1212.500")); // 3% menos
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaRepository).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve criar biometria em lote planejado")
    void deveCriarBiometriaEmLotePlanejado() {
        // Arrange
        lote.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaRepository).save(any(Biometria.class));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Deve buscar biometria por ID com sucesso")
    void deveBuscarBiometriaPorIdComSucesso() {
        // Arrange
        when(biometriaRepository.findById(1L)).thenReturn(Optional.of(biometria));
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(biometriaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar biometria inexistente")
    void deveLancarExcecaoAoBuscarBiometriaInexistente() {
        // Arrange
        when(biometriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Biometria");
    }

    @Test
    @DisplayName("Deve listar biometrias por lote")
    void deveListarBiometriasPorLote() {
        // Arrange
        List<Biometria> biometrias = Arrays.asList(biometria, biometria);
        when(loteRepository.existsById(1L)).thenReturn(true);
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biometrias);
        when(biometriaMapper.toResponse(any(Biometria.class))).thenReturn(response);

        // Act
        List<BiometriaResponse> resultado = biometriaService.listarPorLote(1L);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(loteRepository).existsById(1L);
        verify(biometriaRepository).findByLoteIdOrderByDataBiometriaAsc(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar biometrias de lote inexistente")
    void deveLancarExcecaoAoListarBiometriasDeloteInexistente() {
        // Arrange
        when(loteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.listarPorLote(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote");

        verify(biometriaRepository, never()).findByLoteIdOrderByDataBiometriaAsc(any());
    }

    @Test
    @DisplayName("Deve buscar última biometria do lote")
    void deveBuscarUltimaBiometriaDoLote() {
        // Arrange
        when(loteRepository.existsById(1L)).thenReturn(true);
        when(biometriaRepository.findUltimaBiometriaByLoteId(1L))
                .thenReturn(Optional.of(biometria));
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.buscarUltimaBiometriaDoLote(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(biometriaRepository).findUltimaBiometriaByLoteId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não houver biometria no lote")
    void deveLancarExcecaoQuandoNaoHouverBiometriaNoLote() {
        // Arrange
        when(loteRepository.existsById(1L)).thenReturn(true);
        when(biometriaRepository.findUltimaBiometriaByLoteId(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.buscarUltimaBiometriaDoLote(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nenhuma biometria encontrada");
    }

    @Test
    @DisplayName("Deve listar biometrias paginadas")
    void deveListarBiometriasPaginadas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Biometria> page = new PageImpl<>(Arrays.asList(biometria));
        when(biometriaRepository.findAll(pageable)).thenReturn(page);
        when(biometriaMapper.toResponse(any(Biometria.class))).thenReturn(response);

        // Act
        Page<BiometriaResponse> resultado = biometriaService.listarPaginado(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(biometriaRepository).findAll(pageable);
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deve atualizar biometria com sucesso")
    void deveAtualizarBiometriaComSucesso() {
        // Arrange
        when(biometriaRepository.findById(1L)).thenReturn(Optional.of(biometria));
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.atualizar(1L, request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaMapper).updateEntity(biometria, request, lote);
        verify(biometriaRepository).save(biometria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar biometria inexistente")
    void deveLancarExcecaoAoAtualizarBiometriaInexistente() {
        // Arrange
        when(biometriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.atualizar(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Biometria");

        verify(biometriaRepository, never()).save(any(Biometria.class));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deve deletar biometria com sucesso")
    void deveDeletarBiometriaComSucesso() {
        // Arrange
        when(biometriaRepository.findById(1L)).thenReturn(Optional.of(biometria));

        // Act
        biometriaService.deletar(1L);

        // Assert
        verify(biometriaRepository).delete(biometria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar biometria inexistente")
    void deveLancarExcecaoAoDeletarBiometriaInexistente() {
        // Arrange
        when(biometriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> biometriaService.deletar(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Biometria");

        verify(biometriaRepository, never()).delete(any(Biometria.class));
    }

    // ==================== TESTES DE CÁLCULOS ====================

    @Test
    @DisplayName("Deve calcular indicadores corretamente com dia cultivo zero")
    void deveCalcularIndicadoresComDiaCultivoZero() {
        // Arrange
        biometria.setDiaCultivo(0);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaRepository).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve calcular FCA quando ração total é null")
    void deveCalcularFCAQuandoRacaoTotalEhNull() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(null);
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaRepository).save(any(Biometria.class));
    }

    @Test
    @DisplayName("Deve criar biometria sem peso total da amostra")
    void deveCriarBiometriaSemPesoTotalDaAmostra() {
        // Arrange
        request.setPesoTotalAmostra(null);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaMapper.toEntity(request, lote)).thenReturn(biometria);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.000"));
        when(biometriaRepository.save(any(Biometria.class))).thenReturn(biometria);
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        // Act
        BiometriaResponse resultado = biometriaService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(biometriaRepository).save(any(Biometria.class));
    }
}
