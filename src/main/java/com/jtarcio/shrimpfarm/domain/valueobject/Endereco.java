package com.jtarcio.shrimpfarm.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado; // UF (2 caracteres)
    private String cep;

    // Método para validar CEP (formato: 12345-678 ou 12345678)
    public boolean isCepValido() {
        if (cep == null) return false;
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        return cepLimpo.length() == 8;
    }

    // Método para formatar endereço completo
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null) sb.append(logradouro);
        if (numero != null) sb.append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) sb.append(" - ").append(complemento);
        if (bairro != null) sb.append(" - ").append(bairro);
        if (cidade != null && estado != null) sb.append(" - ").append(cidade).append("/").append(estado);
        if (cep != null) sb.append(" - CEP: ").append(cep);
        return sb.toString();
    }
}
