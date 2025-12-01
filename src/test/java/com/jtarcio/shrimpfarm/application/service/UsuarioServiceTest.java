package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.application.mapper.UsuarioMapper;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest request;
    private Usuario usuario;
    private UsuarioResponse response;

    @BeforeEach
    void setUp() {
        request = UsuarioRequest.builder()
                .nome("João da Silva")
                .email("joao@example.com")
                .senha("senha123")
                .papel(RoleEnum.ADMIN)
                .ativo(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome(request.getNome())
                .email(request.getEmail())
                .senha("senhaHash") // senha tratada no service
                .papel(request.getPapel())
                .ativo(request.getAtivo())
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .dataAtualizacao(LocalDateTime.now())
                .build();

        response = UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .papel(usuario.getPapel())
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .build();
    }

    @Test
    @DisplayName("listarTodos() deve retornar apenas usuários ativos mapeados")
    void listarTodosDeveRetornarAtivosMapeados() {
        when(usuarioRepository.findByAtivoTrue()).thenReturn(List.of(usuario));
        when(usuarioMapper.toResponseList(List.of(usuario))).thenReturn(List.of(response));

        List<UsuarioResponse> lista = usuarioService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
        verify(usuarioRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.buscarPorId(1L));
    }

    @Test
    @DisplayName("buscarPorId() deve retornar usuário mapeado quando encontrar")
    void buscarPorIdDeveRetornarResponse() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.buscarPorId(1L);

        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getEmail(), resultado.getEmail());
    }

    @Test
    @DisplayName("criarUsuario() deve lançar BusinessException se email já existir")
    void criarUsuarioDeveLancarBusinessQuandoEmailJaExiste() {
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class,
                () -> usuarioService.criar(request));
    }

    @Test
    @DisplayName("criarUsuario() deve criar usuário quando email for único")
    void criarUsuarioDeveCriarQuandoEmailUnico() {
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.criar(request);

        assertNotNull(resultado);
        assertEquals(response.getId(), resultado.getId());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("atualizar() deve lançar EntityNotFoundException se usuário não existir")
    void atualizarDeveLancarEntityNotFoundQuandoNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.atualizar(1L, request));
    }

    @Test
    @DisplayName("atualizar() deve lançar BusinessException se email já estiver em outro usuário")
    void atualizarDeveLancarBusinessQuandoEmailDeOutroUsuario() {
        Usuario outro = Usuario.builder()
                .id(2L)
                .nome("Outro")
                .email(request.getEmail())
                .papel(RoleEnum.OPERACIONAL)
                .ativo(true)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(outro));

        assertThrows(BusinessException.class,
                () -> usuarioService.atualizar(1L, request));
    }

    @Test
    @DisplayName("atualizar() deve atualizar dados quando válido")
    void atualizarDeveAtualizarQuandoValido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        doNothing().when(usuarioMapper).updateEntity(usuario, request);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse resultado = usuarioService.atualizar(1L, request);

        assertEquals(response.getId(), resultado.getId());
        verify(usuarioMapper, times(1)).updateEntity(usuario, request);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("desativar() deve marcar usuário como inativo")
    void desativarDeveMarcarComoInativo() {
        usuario.setAtivo(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.desativar(1L);

        assertFalse(usuario.getAtivo());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("inativar() deve marcar usuário como inativo (alias)")
    void inativarDeveMarcarComoInativo() {
        usuario.setAtivo(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.inativar(1L);

        assertFalse(usuario.getAtivo());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("deletar() deve remover usuário existente")
    void deletarDeveRemoverUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deletar(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("deletar() deve lançar EntityNotFoundException se usuário não existir")
    void deletarDeveLancarEntityNotFoundQuandoNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.deletar(1L));
    }
}
