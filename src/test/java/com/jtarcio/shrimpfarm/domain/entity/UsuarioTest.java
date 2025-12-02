package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    @DisplayName("Deve criar um usuário com builder")
    void deveCriarUsuarioComBuilder() {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .senha("senha123")
                .nome("João Silva")
                .telefone("85999999999")
                .papel(RoleEnum.ADMIN)
                .ativo(true)
                .build();

        assertThat(usuario).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("teste@email.com");
        assertThat(usuario.getNome()).isEqualTo("João Silva");
        assertThat(usuario.getPapel()).isEqualTo(RoleEnum.ADMIN);
        assertThat(usuario.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve criar usuário com construtor padrão")
    void deveCriarUsuarioComConstrutorPadrao() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@fazenda.com");
        usuario.setSenha("admin123");
        usuario.setNome("Admin");
        usuario.setPapel(RoleEnum.GERENTE);

        assertThat(usuario.getEmail()).isEqualTo("admin@fazenda.com");
        assertThat(usuario.getPapel()).isEqualTo(RoleEnum.GERENTE);
    }

    @Test
    @DisplayName("Deve inicializar ativo como true por padrão")
    void deveInicializarAtivoComoTruePorPadrao() {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .senha("senha")
                .nome("Teste")
                .papel(RoleEnum.OPERACIONAL)
                .build();

        assertThat(usuario.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .senha("senha")
                .nome("Teste")
                .papel(RoleEnum.ADMIN)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        usuario.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(usuario.getDataCriacao()).isNotNull();
        assertThat(usuario.getDataAtualizacao()).isNotNull();
        assertThat(usuario.getDataCriacao()).isBetween(antes, depois);
        assertThat(usuario.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .senha("senha")
                .nome("Teste")
                .papel(RoleEnum.GERENTE)
                .build();

        usuario.onCreate();
        LocalDateTime dataOriginal = usuario.getDataAtualizacao();

        Thread.sleep(10); // Pequeno delay para garantir diferença de tempo

        usuario.onUpdate();

        assertThat(usuario.getDataAtualizacao()).isNotNull();
        assertThat(usuario.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve permitir diferentes papéis de usuário")
    void devePermitirDiferentesPapeis() {
        Usuario admin = Usuario.builder()
                .email("admin@email.com")
                .senha("senha")
                .nome("Admin")
                .papel(RoleEnum.ADMIN)
                .build();

        Usuario gerente = Usuario.builder()
                .email("gerente@email.com")
                .senha("senha")
                .nome("Gerente")
                .papel(RoleEnum.GERENTE)
                .build();

        Usuario operador = Usuario.builder()
                .email("operador@email.com")
                .senha("senha")
                .nome("Operador")
                .papel(RoleEnum.OPERACIONAL)
                .build();

        assertThat(admin.getPapel()).isEqualTo(RoleEnum.ADMIN);
        assertThat(gerente.getPapel()).isEqualTo(RoleEnum.GERENTE);
        assertThat(operador.getPapel()).isEqualTo(RoleEnum.OPERACIONAL);
    }

    @Test
    @DisplayName("Deve permitir desativar usuário")
    void devePermitirDesativarUsuario() {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .senha("senha")
                .nome("Teste")
                .papel(RoleEnum.OPERACIONAL)
                .ativo(true)
                .build();

        assertThat(usuario.getAtivo()).isTrue();

        usuario.setAtivo(false);

        assertThat(usuario.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve aceitar telefone opcional")
    void deveAceitarTelefoneOpcional() {
        Usuario usuarioComTelefone = Usuario.builder()
                .email("com@email.com")
                .senha("senha")
                .nome("Com Telefone")
                .telefone("85999999999")
                .papel(RoleEnum.OPERACIONAL)
                .build();

        Usuario usuarioSemTelefone = Usuario.builder()
                .email("sem@email.com")
                .senha("senha")
                .nome("Sem Telefone")
                .papel(RoleEnum.OPERACIONAL)
                .build();

        assertThat(usuarioComTelefone.getTelefone()).isEqualTo("85999999999");
        assertThat(usuarioSemTelefone.getTelefone()).isNull();
    }
}
