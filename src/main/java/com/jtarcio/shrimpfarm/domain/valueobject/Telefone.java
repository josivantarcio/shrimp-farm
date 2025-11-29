package com.jtarcio.shrimpfarm.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {

    private String numero;

    // Validação simples de telefone brasileiro
    public boolean isValido() {
        if (numero == null) return false;
        String limpo = numero.replaceAll("[^0-9]", "");
        // Aceita: (DD) 9XXXX-XXXX (11 dígitos) ou (DD) XXXX-XXXX (10 dígitos)
        return limpo.length() == 10 || limpo.length() == 11;
    }

    // Formatar para exibição: (84) 99999-9999
    public String formatar() {
        if (!isValido()) return numero;

        String limpo = numero.replaceAll("[^0-9]", "");

        if (limpo.length() == 11) {
            // (XX) 9XXXX-XXXX
            return String.format("(%s) %s-%s",
                    limpo.substring(0, 2),
                    limpo.substring(2, 7),
                    limpo.substring(7));
        } else if (limpo.length() == 10) {
            // (XX) XXXX-XXXX
            return String.format("(%s) %s-%s",
                    limpo.substring(0, 2),
                    limpo.substring(2, 6),
                    limpo.substring(6));
        }

        return numero;
    }

    @Override
    public String toString() {
        return formatar();
    }
}
