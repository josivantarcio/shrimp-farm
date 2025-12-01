package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.application.mapper.LoteMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do LoteService")
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ViveiroRepository viveiroRepository;

    @Mock
    private LoteMapper loteMapper;

    @InjectMocks
    private LoteService loteService;

    private LoteRequest request;
    private Viveiro viveiro;
    private Lote lote;
    private LoteResponse response;

    @BeforeEach
    void setUp() {
        Fazenda fazenda = Fazenda.builder()
                .id(1L)
                .nome("Fazenda Teste")
                .build();

        viveiro = Viveiro.builder()
                .id(1L)
                .fazenda(fazenda)
                .codigo("V01")
                .nome("Viveiro 01")
                .area(new BigDecimal("1.50"))
                .profundidadeMedia(new BigDecimal("1.20"))
                .volume(new BigDecimal("18000"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .ativo(true)
                .build();

        request = LoteRequest.builder()
                .viveiroId(1L)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 10))
                .quantidadePosLarvas(100_000)
                .custoPosLarvas(new BigDecimal("1500.00"))
                .densidadeInicial(new BigDecimal("50.00"))
                .observacoes("Lote de teste")
                .build();

        lote = Lote.builder()
                .id(10L)
                .viveiro(viveiro)
                .codigo(request.getCodigo())
                .dataPovoamento(request.getDataPovoamento())
                .quantidadePosLarvas(request.getQuantidadePosLarvas())
                .custoPosLarvas(request.getCustoPosLarvas())
                .densidadeInicial(request.getDensidadeInicial())
                .status(StatusLoteEnum.ATIVO)
                .observacoes(request.getObservacoes())
                .build();

        response = LoteResponse.builder()
                .id(lote.getId())
                .viveiroId(viveiro.getId())
                .codigo(lote.getCodigo())
                .dataPovoamento(lote.getDataPovoamento())
                .quantidadePosLarvas(lote.getQuantidadePosLarvas())
                .custoPosLarvas(lote.getCustoPosLarvas())
                .densidadeInicial(lote.getDensidadeInicial())
                .status(lote.getStatus())
                .observacoes(lote.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve criar lote com sucesso e marcar viveiro como OCUPADO quando lote ATIVO")
    void criarDeveCriarLoteComSucesso() {
        when(viveiroRepository.findById(1L)).thenReturn(Optional.of(viveiro));
        when(loteRepository.findByCodigo(request.getCodigo())).thenReturn(Optional.empty());
        when(loteMapper.toEntity(request, viveiro)).thenReturn(lote);
        when(loteRepository.save(any(Lote.class))).thenReturn(lote);
        when(loteMapper.toResponse(lote)).thenReturn(response);

        LoteResponse resultado = loteService.criar(request);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("LOTE01_2025", resultado.getCodigo());
        assertEquals(1L, resultado.getViveiroId());
        assertEquals(StatusLoteEnum.ATIVO, resultado.getStatus());

        assertEquals(StatusViveiroEnum.OCUPADO, viveiro.getStatus());

        verify(viveiroRepository).findById(1L);
        verify(loteRepository).findByCodigo("LOTE01_2025");
        verify(loteRepository).save(any(Lote.class));
        verify(viveiroRepository).save(viveiro);
        verify(loteMapper).toEntity(request, viveiro);
        verify(loteMapper).toResponse(lote);
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando viveiro não existe")
    void criarDeveLancarEntityNotFoundQuandoViveiroNaoExiste() {
        when(viveiroRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> loteService.criar(request));

        verify(loteRepository, never()).findByCodigo(anyString());
        verify(loteRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando viveiro está inativo")
    void criarDeveLancarBusinessQuandoViveiroInativo() {
        viveiro.setAtivo(false);
        when(viveiroRepository.findById(1L)).thenReturn(Optional.of(viveiro));

        assertThrows(BusinessException.class, () -> loteService.criar(request));

        verify(loteRepository, never()).findByCodigo(anyString());
        verify(loteRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar LoteResponse quando encontrar lote")
    void buscarPorIdDeveRetornarLote() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(loteMapper.toResponse(lote)).thenReturn(response);

        LoteResponse resultado = loteService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(loteRepository).findById(10L);
        verify(loteMapper).toResponse(lote);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando lote não existe")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> loteService.buscarPorId(10L));
    }

    @Test
    @DisplayName("listarTodos() deve retornar lista mapeada")
    void listarTodosDeveRetornarLista() {
        when(loteRepository.findAll()).thenReturn(List.of(lote));
        when(loteMapper.toResponse(lote)).thenReturn(response);

        List<LoteResponse> resultados = loteService.listarTodos();

        assertEquals(1, resultados.size());
        verify(loteRepository).findAll();
        verify(loteMapper).toResponse(lote);
    }

    @Test
    @DisplayName("listarPaginado() deve retornar página mapeada")
    void listarPaginadoDeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lote> pageEntidade = new PageImpl<>(List.of(lote), pageable, 1);

        when(loteRepository.findAll(pageable)).thenReturn(pageEntidade);
        when(loteMapper.toResponse(lote)).thenReturn(response);

        Page<LoteResponse> pagina = loteService.listarPaginado(pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(loteRepository).findAll(pageable);
        verify(loteMapper).toResponse(lote);
    }

    @Test
    @DisplayName("iniciarCultivo() deve ativar lote PLANEJADO e marcar viveiro como OCUPADO")
    void iniciarCultivoDeveAtivarLotePlanejado() {
        lote.setStatus(StatusLoteEnum.PLANEJADO);
        viveiro.setStatus(StatusViveiroEnum.DISPONIVEL);

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(loteRepository.save(lote)).thenReturn(lote);
        when(loteMapper.toResponse(lote)).thenReturn(response);

        LoteResponse resultado = loteService.iniciarCultivo(10L);

        assertEquals(StatusLoteEnum.ATIVO, lote.getStatus());
        assertEquals(StatusViveiroEnum.OCUPADO, viveiro.getStatus());
        assertEquals(StatusLoteEnum.ATIVO, resultado.getStatus());

        verify(loteRepository).findById(10L);
        verify(loteRepository).save(lote);
        verify(loteMapper).toResponse(lote);
    }


    @Test
    @DisplayName("iniciarCultivo() deve lançar BusinessException se lote não está PLANEJADO")
    void iniciarCultivoDeveLancarBusinessQuandoNaoPlanejado() {
        lote.setStatus(StatusLoteEnum.ATIVO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        assertThrows(BusinessException.class, () -> loteService.iniciarCultivo(10L));

        verify(loteRepository, never()).save(any());
    }

    @Test
    @DisplayName("finalizarCultivo() deve finalizar lote ATIVO, definir dataDespesca e liberar viveiro")
    void finalizarCultivoDeveFinalizarLoteAtivo() {
        LocalDate dataPovoamento = LocalDate.of(2025, 1, 1);
        LocalDate dataDespesca = LocalDate.of(2025, 2, 1);

        lote.setStatus(StatusLoteEnum.ATIVO);
        lote.setDataPovoamento(dataPovoamento);
        viveiro.setStatus(StatusViveiroEnum.OCUPADO);

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loteMapper.toResponse(any(Lote.class))).thenAnswer(invocation -> {
            Lote l = invocation.getArgument(0);
            return LoteResponse.builder()
                    .id(l.getId())
                    .viveiroId(l.getViveiro().getId())
                    .codigo(l.getCodigo())
                    .dataPovoamento(l.getDataPovoamento())
                    .quantidadePosLarvas(l.getQuantidadePosLarvas())
                    .custoPosLarvas(l.getCustoPosLarvas())
                    .densidadeInicial(l.getDensidadeInicial())
                    .status(l.getStatus())
                    .observacoes(l.getObservacoes())
                    .build();
        });

        LoteResponse resultado = loteService.finalizarCultivo(10L, dataDespesca);

        assertEquals(StatusLoteEnum.FINALIZADO, lote.getStatus());
        assertEquals(dataDespesca, lote.getDataDespesca());
        assertEquals(StatusViveiroEnum.DISPONIVEL, viveiro.getStatus());
        assertEquals(StatusLoteEnum.FINALIZADO, resultado.getStatus());

        verify(loteRepository).findById(10L);
        verify(loteRepository).save(any(Lote.class));
        verify(loteMapper).toResponse(any(Lote.class));
    }


    @Test
    @DisplayName("finalizarCultivo() deve lançar BusinessException se lote não está ATIVO")
    void finalizarCultivoDeveLancarBusinessQuandoNaoAtivo() {
        lote.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        assertThrows(BusinessException.class,
                () -> loteService.finalizarCultivo(10L, LocalDate.now()));

        verify(loteRepository, never()).save(any());
    }

    @Test
    @DisplayName("finalizarCultivo() deve lançar BusinessException se dataDespesca for antes de dataPovoamento")
    void finalizarCultivoDeveLancarBusinessQuandoDataInvalida() {
        lote.setStatus(StatusLoteEnum.ATIVO);
        lote.setDataPovoamento(LocalDate.of(2025, 2, 1)); // depois
        LocalDate dataDespesca = LocalDate.of(2025, 1, 1); // antes

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        assertThrows(BusinessException.class,
                () -> loteService.finalizarCultivo(10L, dataDespesca));

        verify(loteRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelarLote() deve cancelar lote não finalizado e liberar viveiro")
    void cancelarLoteDeveCancelarQuandoNaoFinalizado() {
        lote.setStatus(StatusLoteEnum.ATIVO);
        viveiro.setStatus(StatusViveiroEnum.OCUPADO);

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loteMapper.toResponse(any(Lote.class))).thenAnswer(invocation -> {
            Lote l = invocation.getArgument(0);
            return LoteResponse.builder()
                    .id(l.getId())
                    .viveiroId(l.getViveiro().getId())
                    .codigo(l.getCodigo())
                    .dataPovoamento(l.getDataPovoamento())
                    .quantidadePosLarvas(l.getQuantidadePosLarvas())
                    .custoPosLarvas(l.getCustoPosLarvas())
                    .densidadeInicial(l.getDensidadeInicial())
                    .status(l.getStatus())
                    .observacoes(l.getObservacoes())
                    .build();
        });

        LoteResponse resultado = loteService.cancelarLote(10L);

        assertEquals(StatusLoteEnum.CANCELADO, lote.getStatus());
        assertEquals(StatusViveiroEnum.DISPONIVEL, viveiro.getStatus());
        assertEquals(StatusLoteEnum.CANCELADO, resultado.getStatus());

        verify(loteRepository).findById(10L);
        verify(loteRepository).save(any(Lote.class));
        verify(loteMapper).toResponse(any(Lote.class));
    }

    @Test
    @DisplayName("cancelarLote() deve lançar BusinessException quando lote já está FINALIZADO")
    void cancelarLoteDeveLancarBusinessQuandoFinalizado() {
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        assertThrows(BusinessException.class, () -> loteService.cancelarLote(10L));

        verify(loteRepository, never()).save(any());
    }

}
