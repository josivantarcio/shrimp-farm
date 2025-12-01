package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.application.mapper.FazendaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do FazendaService")
class FazendaServiceTest {

    @Mock
    private FazendaRepository fazendaRepository;

    @Mock
    private FazendaMapper fazendaMapper;

    @InjectMocks
    private FazendaService fazendaService;

    private FazendaRequest request;
    private Fazenda fazenda;
    private FazendaResponse response;

    @BeforeEach
    void setUp() {
        request = FazendaRequest.builder()
                .nome("Fazenda Atlântico")
                .proprietario("João Silva")
                .endereco("Rodovia X, km 10")
                .cidade("Aracati")
                .estado("CE")
                .cep("62800-000")
                .areaTotal(new BigDecimal("100.0"))
                .areaUtil(new BigDecimal("80.0"))
                .telefone("8599999-0000")
                .email("contato@fazenda.com")
                .observacoes("Fazenda de teste")
                .ativa(true)
                .build();

        fazenda = Fazenda.builder()
                .id(1L)
                .nome(request.getNome())
                .proprietario(request.getProprietario())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .areaTotal(request.getAreaTotal())
                .areaUtil(request.getAreaUtil())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .observacoes(request.getObservacoes())
                .ativa(request.getAtiva())
                .build();

        response = FazendaResponse.builder()
                .id(1L)
                .nome(fazenda.getNome())
                .proprietario(fazenda.getProprietario())
                .cidade(fazenda.getCidade())
                .estado(fazenda.getEstado())
                .ativa(fazenda.getAtiva())
                .build();
    }

    @Test
    @DisplayName("criarFazenda() deve salvar e retornar resposta mapeada")
    void criarFazendaDeveSalvarERetornarResponse() {
        when(fazendaMapper.toEntity(request)).thenReturn(fazenda);
        when(fazendaRepository.save(fazenda)).thenReturn(fazenda);
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        FazendaResponse resultado = fazendaService.criar(request);

        assertNotNull(resultado);
        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getNome(), resultado.getNome());
        verify(fazendaRepository, times(1)).save(fazenda);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fazendaService.buscarPorId(1L));
    }

    @Test
    @DisplayName("buscarPorId() deve retornar resposta quando encontrar")
    void buscarPorIdDeveRetornarResponseQuandoEncontrar() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazenda));
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        FazendaResponse resultado = fazendaService.buscarPorId(1L);

        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("listarTodas() deve mapear todas as entidades para response")
    void listarTodasDeveRetornarListaMapeada() {
        when(fazendaRepository.findAll()).thenReturn(List.of(fazenda));
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        List<FazendaResponse> lista = fazendaService.listarTodas();

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
    }

    @Test
    @DisplayName("listarAtivas() deve chamar repository.findByAtivaTrue()")
    void listarAtivasDeveUsarFindByAtivaTrue() {
        when(fazendaRepository.findByAtivaTrue()).thenReturn(List.of(fazenda));
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        List<FazendaResponse> lista = fazendaService.listarAtivas();

        assertEquals(1, lista.size());
        verify(fazendaRepository, times(1)).findByAtivaTrue();
    }

    @Test
    @DisplayName("listarPaginado() deve retornar página mapeada")
    void listarPaginadoDeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Fazenda> pageEntidades = new PageImpl<>(List.of(fazenda), pageable, 1);

        when(fazendaRepository.findAll(pageable)).thenReturn(pageEntidades);
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        Page<FazendaResponse> pagina = fazendaService.listarPaginado(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals(response.getId(), pagina.getContent().get(0).getId());
    }

    @Test
    @DisplayName("atualizar() deve aplicar mapper.updateEntity e salvar")
    void atualizarDeveAtualizarEMapear() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazenda));
        // updateEntity é void: apenas garantir que é chamado
        doAnswer(invocation -> {
            Fazenda f = invocation.getArgument(0);
            FazendaRequest req = invocation.getArgument(1);
            f.setNome(req.getNome());
            return null;
        }).when(fazendaMapper).updateEntity(any(Fazenda.class), any(FazendaRequest.class));

        when(fazendaRepository.save(fazenda)).thenReturn(fazenda);
        when(fazendaMapper.toResponse(fazenda)).thenReturn(response);

        FazendaResponse resultado = fazendaService.atualizar(1L, request);

        assertEquals(response.getId(), resultado.getId());
        verify(fazendaMapper, times(1)).updateEntity(eq(fazenda), eq(request));
        verify(fazendaRepository, times(1)).save(fazenda);
    }

    @Test
    @DisplayName("deletar() deve excluir fazenda existente")
    void deletarDeveExcluirFazenda() {
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazenda));

        fazendaService.deletar(1L);

        verify(fazendaRepository, times(1)).delete(fazenda);
    }

    @Test
    @DisplayName("inativar() deve marcar fazenda como inativa")
    void inativarDeveMarcarComoInativa() {
        fazenda.setAtiva(true);
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazenda));
        when(fazendaRepository.save(fazenda)).thenReturn(fazenda);

        fazendaService.inativar(1L);

        assertFalse(fazenda.getAtiva());
        verify(fazendaRepository, times(1)).save(fazenda);
    }

    @Test
    @DisplayName("ativar() deve marcar fazenda como ativa")
    void ativarDeveMarcarComoAtiva() {
        fazenda.setAtiva(false);
        when(fazendaRepository.findById(1L)).thenReturn(Optional.of(fazenda));
        when(fazendaRepository.save(fazenda)).thenReturn(fazenda);

        fazendaService.ativar(1L);

        assertTrue(fazenda.getAtiva());
        verify(fazendaRepository, times(1)).save(fazenda);
    }
}
