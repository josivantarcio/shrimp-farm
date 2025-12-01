package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.application.mapper.ViveiroMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ViveiroService")
class ViveiroServiceTest {

    @Mock
    private ViveiroRepository viveiroRepository;

    @Mock
    private FazendaRepository fazendaRepository;

    @Mock
    private ViveiroMapper viveiroMapper;

    @InjectMocks
    private ViveiroService viveiroService;

    private Fazenda fazendaAtiva;
    private Fazenda fazendaInativa;
    private ViveiroRequest request;
    private Viveiro viveiro;
    private ViveiroResponse response;

    @BeforeEach
    void setUp() {
        fazendaAtiva = Fazenda.builder()
                .id(1L)
                .nome("Fazenda Atlântico")
                .ativa(true)
                .build();

        fazendaInativa = Fazenda.builder()
                .id(2L)
                .nome("Fazenda Desativada")
                .ativa(false)
                .build();

        request = ViveiroRequest.builder()
                .fazendaId(1L)
                .codigo("V01")
                .nome("V-01")
                .area(new BigDecimal("1000.00"))
                .profundidadeMedia(new BigDecimal("1.20"))
                .volume(new BigDecimal("1200.00"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Viveiro de teste")
                .ativo(true)
                .build();

        viveiro = Viveiro.builder()
                .id(10L)
                .codigo(request.getCodigo())
                .nome(request.getNome())
                .fazenda(fazendaAtiva)
                .area(request.getArea())
                .profundidadeMedia(request.getProfundidadeMedia())
                .volume(request.getVolume())
                .status(request.getStatus())
                .observacoes(request.getObservacoes())
                .ativo(request.getAtivo())
                .lotes(List.of())
                .build();

        response = ViveiroResponse.builder()
                .id(10L)
                .fazendaId(fazendaAtiva.getId())
                .codigo(viveiro.getCodigo())
                .nome(viveiro.getNome())
                .area(viveiro.getArea())
                .profundidadeMedia(viveiro.getProfundidadeMedia())
                .volume(viveiro.getVolume())
                .status(viveiro.getStatus())
                .ativo(viveiro.getAtivo())
                .build();
    }

    @Test
    @DisplayName("criarViveiro() deve lançar EntityNotFoundException se fazenda não existir")
    void criarViveiroDeveLancarEntityNotFoundQuandoFazendaNaoExiste() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> viveiroService.criar(request));
    }

    @Test
    @DisplayName("criarViveiro() deve lançar BusinessException se fazenda estiver inativa")
    void criarViveiroDeveLancarBusinessQuandoFazendaInativa() {
        request.setFazendaId(2L);
        when(fazendaRepository.findById(2L)).thenReturn(Optional.of(fazendaInativa));

        assertThrows(BusinessException.class,
                () -> viveiroService.criar(request));
    }

    @Test
    @DisplayName("criarViveiro() deve criar viveiro quando fazenda estiver ativa")
    void criarViveiroDeveCriarQuandoFazendaAtiva() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazendaAtiva));
        when(viveiroMapper.toEntity(request, fazendaAtiva)).thenReturn(viveiro);
        when(viveiroRepository.save(viveiro)).thenReturn(viveiro);
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        ViveiroResponse resultado = viveiroService.criar(request);

        assertNotNull(resultado);
        assertEquals(response.getId(), resultado.getId());
        verify(viveiroRepository, times(1)).save(viveiro);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(viveiroRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> viveiroService.buscarPorId(10L));
    }

    @Test
    @DisplayName("buscarPorId() deve retornar response quando encontrar")
    void buscarPorIdDeveRetornarResponseQuandoEncontrar() {
        when(viveiroRepository.findById(10L)).thenReturn(Optional.of(viveiro));
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        ViveiroResponse resultado = viveiroService.buscarPorId(10L);

        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("listarTodos() deve retornar todos os viveiros mapeados")
    void listarTodosDeveRetornarListaMapeada() {
        when(viveiroRepository.findAll()).thenReturn(List.of(viveiro));
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        List<ViveiroResponse> lista = viveiroService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
    }

    @Test
    @DisplayName("listarPorFazenda() deve lançar EntityNotFoundException se fazenda não existir")
    void listarPorFazendaDeveLancarEntityNotFoundQuandoFazendaNaoExiste() {
        when(fazendaRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> viveiroService.listarPorFazenda(1L));
    }

    @Test
    @DisplayName("listarPorFazenda() deve retornar viveiros da fazenda quando existir")
    void listarPorFazendaDeveRetornarLista() {
        when(fazendaRepository.existsById(1L)).thenReturn(true);
        when(viveiroRepository.findByFazendaId(1L)).thenReturn(List.of(viveiro));
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        List<ViveiroResponse> lista = viveiroService.listarPorFazenda(1L);

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
    }

    @Test
    @DisplayName("listarPorStatus() deve retornar viveiros com status informado")
    void listarPorStatusDeveRetornarPorStatus() {
        when(viveiroRepository.findByStatus(StatusViveiroEnum.DISPONIVEL))
                .thenReturn(List.of(viveiro));
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        List<ViveiroResponse> lista =
                viveiroService.listarPorStatus(StatusViveiroEnum.DISPONIVEL);

        assertEquals(1, lista.size());
        assertEquals(StatusViveiroEnum.DISPONIVEL, lista.get(0).getStatus());
    }

    @Test
    @DisplayName("listarAtivosPorFazenda() deve lançar EntityNotFoundException se fazenda não existir")
    void listarAtivosPorFazendaDeveLancarEntityNotFoundQuandoFazendaNaoExiste() {
        when(fazendaRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> viveiroService.listarAtivosPorFazenda(1L));
    }

    @Test
    @DisplayName("listarAtivosPorFazenda() deve retornar viveiros ativos da fazenda")
    void listarAtivosPorFazendaDeveRetornarLista() {
        when(fazendaRepository.existsById(1L)).thenReturn(true);
        when(viveiroRepository.findByFazendaIdAndAtivoTrue(1L))
                .thenReturn(List.of(viveiro));
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        List<ViveiroResponse> lista = viveiroService.listarAtivosPorFazenda(1L);

        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getAtivo());
    }

    @Test
    @DisplayName("listarPaginado() deve retornar página mapeada")
    void listarPaginadoDeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Viveiro> pageEntidades = new PageImpl<>(List.of(viveiro), pageable, 1);

        when(viveiroRepository.findAll(pageable)).thenReturn(pageEntidades);
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        Page<ViveiroResponse> pagina = viveiroService.listarPaginado(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals(response.getId(), pagina.getContent().get(0).getId());
    }

    @Test
    @DisplayName("atualizar() deve atualizar viveiro e retornar response")
    void atualizarDeveAtualizarEMapear() {
        request.setFazendaId(1L);
        when(viveiroRepository.findById(10L)).thenReturn(Optional.of(viveiro));
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazendaAtiva));

        doAnswer(invocation -> {
            Viveiro v = invocation.getArgument(0);
            ViveiroRequest req = invocation.getArgument(1);
            Fazenda faz = invocation.getArgument(2);
            v.setNome(req.getNome());
            v.setCodigo(req.getCodigo());
            v.setArea(req.getArea());
            v.setProfundidadeMedia(req.getProfundidadeMedia());
            v.setVolume(req.getVolume());
            v.setStatus(req.getStatus());
            v.setObservacoes(req.getObservacoes());
            v.setAtivo(req.getAtivo());
            v.setFazenda(faz);
            return null;
        }).when(viveiroMapper).updateEntity(any(Viveiro.class), any(ViveiroRequest.class), any(Fazenda.class));

        when(viveiroRepository.save(viveiro)).thenReturn(viveiro);
        when(viveiroMapper.toResponse(viveiro)).thenReturn(response);

        ViveiroResponse resultado = viveiroService.atualizar(10L, request);

        assertEquals(response.getId(), resultado.getId());
        verify(viveiroMapper, times(1)).updateEntity(eq(viveiro), eq(request), eq(fazendaAtiva));
        verify(viveiroRepository, times(1)).save(viveiro);
    }

    @Test
    @DisplayName("deletar() deve lançar BusinessException quando viveiro tiver lotes")
    void deletarDeveLancarBusinessQuandoViveiroTemLotes() {
        Viveiro comLotes = Viveiro.builder()
                .id(20L)
                .nome("V-02")
                .lotes(List.of(Lote.builder().id(1L).build()))
                .build();

        when(viveiroRepository.findById(20L)).thenReturn(Optional.of(comLotes));

        assertThrows(BusinessException.class,
                () -> viveiroService.deletar(20L));

        verify(viveiroRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deletar() deve excluir viveiro quando não tiver lotes")
    void deletarDeveExcluirQuandoSemLotes() {
        viveiro.setLotes(List.of());
        when(viveiroRepository.findById(10L)).thenReturn(Optional.of(viveiro));

        viveiroService.deletar(10L);

        verify(viveiroRepository, times(1)).delete(viveiro);
    }

    @Test
    @DisplayName("inativar() deve marcar viveiro como inativo")
    void inativarDeveMarcarComoInativo() {
        viveiro.setAtivo(true);
        when(viveiroRepository.findById(10L)).thenReturn(Optional.of(viveiro));
        when(viveiroRepository.save(viveiro)).thenReturn(viveiro);

        viveiroService.inativar(10L);

        assertFalse(viveiro.getAtivo());
        verify(viveiroRepository, times(1)).save(viveiro);
    }

    @Test
    @DisplayName("mudarStatus() deve alterar status do viveiro")
    void mudarStatusDeveAlterarStatus() {
        viveiro.setStatus(StatusViveiroEnum.DISPONIVEL);
        when(viveiroRepository.findById(10L)).thenReturn(Optional.of(viveiro));
        when(viveiroRepository.save(viveiro)).thenReturn(viveiro);

        viveiroService.mudarStatus(10L, StatusViveiroEnum.OCUPADO);

        assertEquals(StatusViveiroEnum.OCUPADO, viveiro.getStatus());
        verify(viveiroRepository, times(1)).save(viveiro);
    }
}
