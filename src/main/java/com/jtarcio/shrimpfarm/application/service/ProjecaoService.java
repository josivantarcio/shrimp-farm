package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjecaoService {

    private final LoteRepository loteRepository;
    private final BiometriaRepository biometriaRepository;

    // Constantes para projeções
    private static final BigDecimal PESO_IDEAL_DESPESCA = BigDecimal.valueOf(15.0); // 15g
    private static final BigDecimal SOBREVIVENCIA_PADRAO = BigDecimal.valueOf(0.80); // 80%
    private static final int DIAS_MINIMOS_CULTIVO = 90;
    private static final int DIAS_MAXIMOS_CULTIVO = 150;

    /**
     * Projeta o peso médio em uma data futura baseado nas biometrias
     */
    @Transactional(readOnly = true)
    public BigDecimal projetarPesoMedio(Long loteId, LocalDate dataProjecao) {
        log.info("Projetando peso médio do lote {} para data {}", loteId, dataProjecao);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        List<Biometria> biometrias = biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(loteId);

        if (biometrias.isEmpty()) {
            throw new BusinessException("Não há biometrias registradas para fazer projeção");
        }

        if (biometrias.size() < 2) {
            throw new BusinessException("É necessário pelo menos 2 biometrias para fazer projeção");
        }

        // Calcular GPD médio das últimas biometrias
        BigDecimal gpdMedio = calcularGPDMedio(biometrias);

        // Última biometria
        Biometria ultimaBiometria = biometrias.get(biometrias.size() - 1);

        // Dias entre última biometria e data de projeção
        long diasProjecao = ChronoUnit.DAYS.between(ultimaBiometria.getDataBiometria(), dataProjecao);

        if (diasProjecao < 0) {
            throw new BusinessException("Data de projeção não pode ser anterior à última biometria");
        }

        // Projeção: peso atual + (GPD médio * dias)
        BigDecimal pesoProjetado = ultimaBiometria.getPesoMedio()
                .add(gpdMedio.multiply(BigDecimal.valueOf(diasProjecao)));

        log.info("Peso projetado: {}g (GPD médio: {}g/dia)", pesoProjetado, gpdMedio);
        return pesoProjetado.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Sugere a melhor data para despesca baseado no peso ideal
     */
    @Transactional(readOnly = true)
    public Map<String, Object> sugerirDataDespesca(Long loteId) {
        log.info("Sugerindo data de despesca para lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        List<Biometria> biometrias = biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(loteId);

        if (biometrias.isEmpty() || biometrias.size() < 2) {
            throw new BusinessException("É necessário pelo menos 2 biometrias para sugerir data de despesca");
        }

        Map<String, Object> resultado = new HashMap<>();

        // Calcular GPD médio
        BigDecimal gpdMedio = calcularGPDMedio(biometrias);
        Biometria ultimaBiometria = biometrias.get(biometrias.size() - 1);

        // Calcular dias necessários para atingir peso ideal
        BigDecimal pesoFaltante = PESO_IDEAL_DESPESCA.subtract(ultimaBiometria.getPesoMedio());

        if (pesoFaltante.compareTo(BigDecimal.ZERO) <= 0) {
            resultado.put("status", "PRONTO_PARA_DESPESCA");
            resultado.put("mensagem", "Lote já atingiu o peso ideal de despesca");
            resultado.put("dataSugerida", LocalDate.now());
            resultado.put("pesoAtual", ultimaBiometria.getPesoMedio());
            resultado.put("pesoIdeal", PESO_IDEAL_DESPESCA);
            return resultado;
        }

        // Dias necessários = peso faltante / GPD médio
        long diasNecessarios = pesoFaltante.divide(gpdMedio, 0, RoundingMode.UP).longValue();
        LocalDate dataSugerida = ultimaBiometria.getDataBiometria().plusDays(diasNecessarios);

        // Verificar se está dentro do intervalo aceitável
        long diasTotaisCultivo = ChronoUnit.DAYS.between(lote.getDataPovoamento(), dataSugerida);

        String status;
        if (diasTotaisCultivo < DIAS_MINIMOS_CULTIVO) {
            status = "MUITO_CEDO";
            dataSugerida = lote.getDataPovoamento().plusDays(DIAS_MINIMOS_CULTIVO);
        } else if (diasTotaisCultivo > DIAS_MAXIMOS_CULTIVO) {
            status = "ATENCAO_PRAZO";
            dataSugerida = lote.getDataPovoamento().plusDays(DIAS_MAXIMOS_CULTIVO);
        } else {
            status = "IDEAL";
        }

        resultado.put("status", status);
        resultado.put("dataSugerida", dataSugerida);
        resultado.put("diasNecessarios", diasNecessarios);
        resultado.put("diasTotaisCultivo", diasTotaisCultivo);
        resultado.put("pesoAtual", ultimaBiometria.getPesoMedio());
        resultado.put("pesoIdeal", PESO_IDEAL_DESPESCA);
        resultado.put("gpdMedio", gpdMedio);

        log.info("Data sugerida: {} (status: {})", dataSugerida, status);
        return resultado;
    }

    /**
     * Projeta a biomassa total na despesca
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> projetarBiomassaDespesca(Long loteId, LocalDate dataDespesca) {
        log.info("Projetando biomassa na despesca do lote {} para {}", loteId, dataDespesca);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        Map<String, BigDecimal> resultado = new HashMap<>();

        // Projetar peso médio na data
        BigDecimal pesoMedioProjetado = projetarPesoMedio(loteId, dataDespesca);

        // Calcular quantidade estimada (quantidade inicial * sobrevivência)
        BigDecimal quantidadeEstimada = BigDecimal.valueOf(lote.getQuantidadePosLarvas())
                .multiply(SOBREVIVENCIA_PADRAO);

        // Biomassa = peso médio * quantidade / 1000 (em kg)
        BigDecimal biomassaProjetada = pesoMedioProjetado
                .multiply(quantidadeEstimada)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        resultado.put("pesoMedioProjetado", pesoMedioProjetado);
        resultado.put("quantidadeEstimada", quantidadeEstimada);
        resultado.put("biomassaProjetada", biomassaProjetada);
        resultado.put("sobrevivenciaEstimada", SOBREVIVENCIA_PADRAO.multiply(BigDecimal.valueOf(100)));

        log.info("Biomassa projetada: {} kg", biomassaProjetada);
        return resultado;
    }

    /**
     * Projeta a receita esperada na despesca
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> projetarReceitaDespesca(Long loteId, LocalDate dataDespesca, BigDecimal precoVendaKg) {
        log.info("Projetando receita da despesca do lote {} para {} com preço R$ {}/kg",
                loteId, dataDespesca, precoVendaKg);

        Map<String, BigDecimal> resultado = new HashMap<>();

        // Projetar biomassa
        Map<String, BigDecimal> projecaoBiomassa = projetarBiomassaDespesca(loteId, dataDespesca);
        BigDecimal biomassaProjetada = projecaoBiomassa.get("biomassaProjetada");

        // Receita = biomassa * preço por kg
        BigDecimal receitaProjetada = biomassaProjetada.multiply(precoVendaKg);

        resultado.put("biomassaProjetada", biomassaProjetada);
        resultado.put("precoVendaKg", precoVendaKg);
        resultado.put("receitaProjetada", receitaProjetada);
        resultado.putAll(projecaoBiomassa);

        log.info("Receita projetada: R$ {}", receitaProjetada);
        return resultado;
    }

    /**
     * Projeta o lucro esperado (receita - custos)
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> projetarLucroDespesca(Long loteId, LocalDate dataDespesca,
                                                         BigDecimal precoVendaKg,
                                                         CalculadoraCustoService calculadoraCustoService) {
        log.info("Projetando lucro da despesca do lote {}", loteId);

        Map<String, BigDecimal> resultado = new HashMap<>();

        // Projetar receita
        Map<String, BigDecimal> projecaoReceita = projetarReceitaDespesca(loteId, dataDespesca, precoVendaKg);
        BigDecimal receitaProjetada = projecaoReceita.get("receitaProjetada");

        // Calcular custos atuais
        Map<String, BigDecimal> custos = calculadoraCustoService.calcularCustosDoLote(loteId);
        BigDecimal custoTotal = custos.get("custoTotal");

        // Lucro projetado
        BigDecimal lucroProjetado = receitaProjetada.subtract(custoTotal);

        // ROI projetado
        BigDecimal roiProjetado = BigDecimal.ZERO;
        if (custoTotal.compareTo(BigDecimal.ZERO) > 0) {
            roiProjetado = lucroProjetado
                    .divide(custoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        resultado.put("receitaProjetada", receitaProjetada);
        resultado.put("custoTotal", custoTotal);
        resultado.put("lucroProjetado", lucroProjetado);
        resultado.put("roiProjetado", roiProjetado);
        resultado.putAll(projecaoReceita);

        log.info("Lucro projetado: R$ {} (ROI: {}%)", lucroProjetado, roiProjetado);
        return resultado;
    }

    // Método auxiliar privado
    private BigDecimal calcularGPDMedio(List<Biometria> biometrias) {
        if (biometrias.size() < 2) {
            return BigDecimal.ZERO;
        }

        // Pegar as últimas 3 biometrias ou todas se tiver menos
        int inicio = Math.max(0, biometrias.size() - 3);
        List<Biometria> ultimasBiometrias = biometrias.subList(inicio, biometrias.size());

        BigDecimal somaGPD = BigDecimal.ZERO;
        int contador = 0;

        for (Biometria biometria : ultimasBiometrias) {
            if (biometria.getGanhoPesoDiario() != null) {
                somaGPD = somaGPD.add(biometria.getGanhoPesoDiario());
                contador++;
            }
        }

        if (contador == 0) {
            return BigDecimal.ZERO;
        }

        return somaGPD.divide(BigDecimal.valueOf(contador), 4, RoundingMode.HALF_UP);
    }
}
