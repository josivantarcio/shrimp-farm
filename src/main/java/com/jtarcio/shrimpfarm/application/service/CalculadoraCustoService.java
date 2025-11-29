package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculadoraCustoService {

    private final LoteRepository loteRepository;
    private final RacaoRepository racaoRepository;
    private final NutrienteRepository nutrienteRepository;
    private final FertilizacaoRepository fertilizacaoRepository;
    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Calcula todos os custos de um lote
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calcularCustosDoLote(Long loteId) {
        log.info("Calculando custos do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        Map<String, BigDecimal> custos = new HashMap<>();

        // 1. Custo de pós-larvas
        BigDecimal custoPosLarvas = lote.getCustoPosLarvas() != null
                ? lote.getCustoPosLarvas()
                : BigDecimal.ZERO;
        custos.put("custoPosLarvas", custoPosLarvas);

        // 2. Custo de ração
        BigDecimal custoRacao = racaoRepository.calcularCustoTotalRacaoByLoteId(loteId);
        custoRacao = custoRacao != null ? custoRacao : BigDecimal.ZERO;
        custos.put("custoRacao", custoRacao);

        // 3. Custo de nutrientes (probióticos, vitaminas, etc)
        BigDecimal custoNutrientes = nutrienteRepository.calcularCustoTotalNutrientesByLoteId(loteId);
        custoNutrientes = custoNutrientes != null ? custoNutrientes : BigDecimal.ZERO;
        custos.put("custoNutrientes", custoNutrientes);

        // 4. Custo de fertilização
        BigDecimal custoFertilizacao = fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(loteId);
        custoFertilizacao = custoFertilizacao != null ? custoFertilizacao : BigDecimal.ZERO;
        custos.put("custoFertilizacao", custoFertilizacao);

        // 5. Custos variáveis (energia, mão de obra, etc)
        BigDecimal custoVariavel = custoVariavelRepository.calcularCustoTotalVariavelByLoteId(loteId);
        custoVariavel = custoVariavel != null ? custoVariavel : BigDecimal.ZERO;
        custos.put("custoVariavel", custoVariavel);

        // 6. Custo total
        BigDecimal custoTotal = custoPosLarvas
                .add(custoRacao)
                .add(custoNutrientes)
                .add(custoFertilizacao)
                .add(custoVariavel);
        custos.put("custoTotal", custoTotal);

        log.info("Custos calculados - Total: R$ {}", custoTotal);
        return custos;
    }

    /**
     * Calcula o custo por kg produzido (se houver despesca)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularCustoPorKg(Long loteId) {
        log.info("Calculando custo por kg do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        if (lote.getDespesca() == null) {
            log.warn("Lote {} ainda não tem despesca registrada", loteId);
            return BigDecimal.ZERO;
        }

        Map<String, BigDecimal> custos = calcularCustosDoLote(loteId);
        BigDecimal custoTotal = custos.get("custoTotal");
        BigDecimal pesoTotalDespesca = lote.getDespesca().getPesoTotal();

        if (pesoTotalDespesca.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal custoPorKg = custoTotal.divide(pesoTotalDespesca, 2, RoundingMode.HALF_UP);

        log.info("Custo por kg: R$ {}/kg", custoPorKg);
        return custoPorKg;
    }

    /**
     * Calcula o custo por camarão
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularCustoPorCamarao(Long loteId) {
        log.info("Calculando custo por camarão do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        if (lote.getDespesca() == null) {
            log.warn("Lote {} ainda não tem despesca registrada", loteId);
            return BigDecimal.ZERO;
        }

        Map<String, BigDecimal> custos = calcularCustosDoLote(loteId);
        BigDecimal custoTotal = custos.get("custoTotal");
        Integer quantidadeDespescada = lote.getDespesca().getQuantidadeDespescada();

        if (quantidadeDespescada == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal custoPorCamarao = custoTotal.divide(
                BigDecimal.valueOf(quantidadeDespescada),
                4,
                RoundingMode.HALF_UP
        );

        log.info("Custo por camarão: R$ {}", custoPorCamarao);
        return custoPorCamarao;
    }

    /**
     * Calcula o percentual de cada custo em relação ao total
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calcularPercentualCustos(Long loteId) {
        log.info("Calculando percentual de custos do lote ID: {}", loteId);

        Map<String, BigDecimal> custos = calcularCustosDoLote(loteId);
        Map<String, BigDecimal> percentuais = new HashMap<>();

        BigDecimal custoTotal = custos.get("custoTotal");

        if (custoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return percentuais;
        }

        custos.forEach((chave, valor) -> {
            if (!chave.equals("custoTotal")) {
                BigDecimal percentual = valor
                        .divide(custoTotal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                percentuais.put(chave + "Percentual", percentual);
            }
        });

        log.info("Percentuais calculados com sucesso");
        return percentuais;
    }

    /**
     * Calcula o custo médio diário do lote
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularCustoMedioDiario(Long loteId) {
        log.info("Calculando custo médio diário do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        Integer diasCultivo = lote.getDiasCultivo();

        if (diasCultivo == null || diasCultivo == 0) {
            return BigDecimal.ZERO;
        }

        Map<String, BigDecimal> custos = calcularCustosDoLote(loteId);
        BigDecimal custoTotal = custos.get("custoTotal");

        BigDecimal custoMedioDiario = custoTotal.divide(
                BigDecimal.valueOf(diasCultivo),
                2,
                RoundingMode.HALF_UP
        );

        log.info("Custo médio diário: R$ {}/dia", custoMedioDiario);
        return custoMedioDiario;
    }

    /**
     * Calcula a relação custo/receita (ROI) se houver despesca
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calcularROI(Long loteId) {
        log.info("Calculando ROI do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        Map<String, BigDecimal> resultado = new HashMap<>();

        if (lote.getDespesca() == null) {
            log.warn("Lote {} ainda não tem despesca registrada", loteId);
            return resultado;
        }

        Map<String, BigDecimal> custos = calcularCustosDoLote(loteId);
        BigDecimal custoTotal = custos.get("custoTotal");
        BigDecimal receitaTotal = lote.getDespesca().getReceitaTotal();

        if (receitaTotal == null) {
            receitaTotal = BigDecimal.ZERO;
        }

        // Lucro
        BigDecimal lucro = receitaTotal.subtract(custoTotal);
        resultado.put("custoTotal", custoTotal);
        resultado.put("receitaTotal", receitaTotal);
        resultado.put("lucro", lucro);

        // ROI (%)
        if (custoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal roi = lucro
                    .divide(custoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            resultado.put("roiPercentual", roi);
        }

        // Margem de lucro (%)
        if (receitaTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margemLucro = lucro
                    .divide(receitaTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            resultado.put("margemLucro", margemLucro);
        }

        log.info("ROI calculado - Lucro: R$ {}, ROI: {}%",
                lucro, resultado.get("roiPercentual"));

        return resultado;
    }
}
