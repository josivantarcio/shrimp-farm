package com.jtarcio.shrimpfarm.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dinheiro {

    private BigDecimal valor;
    private String moeda = "BRL";

    public Dinheiro(BigDecimal valor) {
        this.valor = valor != null ? valor.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.moeda = "BRL";
    }

    public Dinheiro(double valor) {
        this(BigDecimal.valueOf(valor));
    }

    // Operações matemáticas
    public Dinheiro somar(Dinheiro outro) {
        if (!this.moeda.equals(outro.moeda)) {
            throw new IllegalArgumentException("Não é possível somar valores de moedas diferentes");
        }
        return new Dinheiro(this.valor.add(outro.valor), this.moeda);
    }

    public Dinheiro subtrair(Dinheiro outro) {
        if (!this.moeda.equals(outro.moeda)) {
            throw new IllegalArgumentException("Não é possível subtrair valores de moedas diferentes");
        }
        return new Dinheiro(this.valor.subtract(outro.valor), this.moeda);
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(this.valor.multiply(fator), this.moeda);
    }

    public Dinheiro dividir(BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divisão por zero");
        }
        return new Dinheiro(this.valor.divide(divisor, 2, RoundingMode.HALF_UP), this.moeda);
    }

    // Comparações
    public boolean isMaiorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public boolean isMenorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) < 0;
    }

    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositivo() {
        return this.valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativo() {
        return this.valor.compareTo(BigDecimal.ZERO) < 0;
    }

    // Formatação
    public String formatarMoeda() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(valor);
    }

    @Override
    public String toString() {
        return formatarMoeda();
    }
}
