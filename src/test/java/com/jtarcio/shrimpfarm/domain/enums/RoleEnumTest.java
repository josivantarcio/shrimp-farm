package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        RoleEnum[] valores = RoleEnum.values();

        assertThat(valores).hasSize(4);
        assertThat(valores).containsExactlyInAnyOrder(
                RoleEnum.ADMIN,
                RoleEnum.GERENTE,
                RoleEnum.OPERACIONAL,
                RoleEnum.VISUALIZADOR
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada role")
    void deveRetornarCodigoCorreto() {
        assertThat(RoleEnum.ADMIN.getCodigo()).isEqualTo(0);
        assertThat(RoleEnum.GERENTE.getCodigo()).isEqualTo(1);
        assertThat(RoleEnum.OPERACIONAL.getCodigo()).isEqualTo(2);
        assertThat(RoleEnum.VISUALIZADOR.getCodigo()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar authority correto para cada role")
    void deveRetornarAuthorityCorreto() {
        assertThat(RoleEnum.ADMIN.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(RoleEnum.GERENTE.getAuthority()).isEqualTo("ROLE_GERENTE");
        assertThat(RoleEnum.OPERACIONAL.getAuthority()).isEqualTo("ROLE_OPERACIONAL");
        assertThat(RoleEnum.VISUALIZADOR.getAuthority()).isEqualTo("ROLE_VISUALIZADOR");
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada role")
    void deveRetornarDescricaoCorreta() {
        assertThat(RoleEnum.ADMIN.getDescricao()).isEqualTo("Administrador do sistema");
        assertThat(RoleEnum.GERENTE.getDescricao()).isEqualTo("Gerente da fazenda");
        assertThat(RoleEnum.OPERACIONAL.getDescricao()).isEqualTo("Operador de campo");
        assertThat(RoleEnum.VISUALIZADOR.getDescricao()).isEqualTo("Apenas visualização");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(RoleEnum.fromCodigo(0)).isEqualTo(RoleEnum.ADMIN);
        assertThat(RoleEnum.fromCodigo(1)).isEqualTo(RoleEnum.GERENTE);
        assertThat(RoleEnum.fromCodigo(2)).isEqualTo(RoleEnum.OPERACIONAL);
        assertThat(RoleEnum.fromCodigo(3)).isEqualTo(RoleEnum.VISUALIZADOR);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> RoleEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de role inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> RoleEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de role inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        RoleEnum role = RoleEnum.valueOf("GERENTE");

        assertThat(role).isEqualTo(RoleEnum.GERENTE);
        assertThat(role.getAuthority()).isEqualTo("ROLE_GERENTE");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(RoleEnum.ADMIN.ordinal()).isEqualTo(0);
        assertThat(RoleEnum.VISUALIZADOR.ordinal()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve implementar interface GrantedAuthority")
    void deveImplementarGrantedAuthority() {
        assertThat(RoleEnum.ADMIN)
                .isInstanceOf(org.springframework.security.core.GrantedAuthority.class);
    }

    @Test
    @DisplayName("Deve retornar authority através do método da interface GrantedAuthority")
    void deveRetornarAuthorityAtravesInterfaceGrantedAuthority() {
        org.springframework.security.core.GrantedAuthority authority = RoleEnum.GERENTE;

        assertThat(authority.getAuthority()).isEqualTo("ROLE_GERENTE");
    }
}
