package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum RoleEnum implements GrantedAuthority {
    ADMIN(0, "ROLE_ADMIN", "Administrador do sistema"),
    GERENTE(1, "ROLE_GERENTE", "Gerente da fazenda"),
    OPERACIONAL(2, "ROLE_OPERACIONAL", "Operador de campo"),
    VISUALIZADOR(3, "ROLE_VISUALIZADOR", "Apenas visualização");

    private final Integer codigo;
    private final String authority;
    private final String descricao;

    RoleEnum(Integer codigo, String authority, String descricao) {
        this.codigo = codigo;
        this.authority = authority;
        this.descricao = descricao;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public static RoleEnum fromCodigo(Integer codigo) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getCodigo().equals(codigo)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("Código de role inválido: " + codigo);
    }
}
