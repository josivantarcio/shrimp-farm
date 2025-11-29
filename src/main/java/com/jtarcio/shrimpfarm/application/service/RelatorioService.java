package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
import com.jtarcio.shrimpfarm.application.dto.response.RelatorioCustoLoteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final LoteRepository loteRepository;
    private final ViveiroRepository viveiroRepository;
    private final BiometriaRepository biometriaRepository;
    private final RacaoRepository racaoRepository;
    private final NutrienteRepository nutrienteRepository;
    private final FertilizacaoRepository fertilizacaoRepository;
    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Retorna KPIs gerais para o Dashboard
     */
    @Transactional(readOnly = true)
    public DashboardKPIsResponse obterKPIsDashboard() {
        log.info("Gerando KPIs do Dashboard");

        List<Lote> lotesAtivos = loteRepository.findByStatus(StatusLoteEnum.ATIVO);

        if (lotesAtivos.isEmpty()) {
            return DashboardKPIsResponse.builder()
                    .totalLotesAtivos(0)
                    .totalViveirosOcupados(0)
                    .build();
        }

        // Total de viveiros ocupados
        long viveirosOcupados = viveiroRepository.countByStatus(StatusViveiroEnum.OCUPADO);

        // Dias médios de cultivo
        double diasMedios = lotesAtivos.stream()
                .mapToLong(lote -> ChronoUnit.DAYS.between(lote.getDataPovoamento(), LocalDate.now()))
                .average()
                .orElse(0.0);

        // Biomassa total atual e peso médio
        BigDecimal biomassaTotal = BigDecimal.ZERO;
        BigDecimal pesoMedioTotal = BigDecimal.ZERO;
        BigDecimal sobrevivenciaTotal = BigDecimal.ZERO;
        int lotesComBiometria = 0;

        for (Lote lote : lotesAtivos) {
            Biometria ultimaBiometria = biometriaRepository
                    .findUltimaBiometriaByLoteId(lote.getId())
                    .orElse(null);

            if (ultimaBiometria != null) {
                biomassaTotal = biomassaTotal.add(ultimaBiometria.getBiomassaEstimada());
                pesoMedioTotal = pesoMedioTotal.add(ultimaBiometria.getPesoMedio());

                if (ultimaBiometria.getSobrevivenciaEstimada() != null) {
                    sobrevivenciaTotal = sobrevivenciaTotal.add(ultimaBiometria.getSobrevivenciaEstimada());
                }

                lotesComBiometria++;
            }
        }

        BigDecimal pesoMedio = lotesComBiometria > 0
                ? pesoMedioTotal.divide(BigDecimal.valueOf(lotesComBiometria), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal sobrevivenciaMedia = lotesComBiometria > 0
                ? sobrevivenciaTotal.divide(BigDecimal.valueOf(lotesComBiometria), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Custo médio por kg (placeholder - pode calcular depois)
        BigDecimal custoMedio = BigDecimal.valueOf(11.50);
        BigDecimal lucroMedio = BigDecimal.valueOf(4.20);

        return DashboardKPIsResponse.builder()
                .totalLotesAtivos(lotesAtivos.size())
                .totalViveirosOcupados((int) viveirosOcupados)
                .diasMediosCultivo(BigDecimal.valueOf(diasMedios).setScale(0, RoundingMode.HALF_UP))
                .pesoMedioAtual(pesoMedio)
                .biomassaTotalAtual(biomassaTotal)
                .custoMedioPorKg(custoMedio)
                .lucroMedioPorKg(lucroMedio)
                .taxaSobrevivenciaMedia(sobrevivenciaMedia)
                .fcaMedia(BigDecimal.valueOf(1.25))
                .build();
    }

    /**
     * Gera relatório detalhado de custos de um lote
     */
    @Transactional(readOnly = true)
    public RelatorioCustoLoteResponse gerarRelatorioCustoLote(Long loteId) {
        log.info("Gerando relatório de custo do lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote", loteId));

        // Buscar última biometria
        Biometria ultimaBiometria = biometriaRepository
                .findUltimaBiometriaByLoteId(loteId)
                .orElse(null);

        // Calcular custos
        BigDecimal custoRacao = racaoRepository.calcularCustoTotalRacaoByLoteId(loteId);
        BigDecimal custoNutrientes = nutrienteRepository.calcularCustoTotalNutrientesByLoteId(loteId);
        BigDecimal custoFertilizacao = fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(loteId);
        BigDecimal custosVariaveis = custoVariavelRepository.calcularCustoTotalVariavelByLoteId(loteId);

        custoRacao = custoRacao != null ? custoRacao : BigDecimal.ZERO;
        custoNutrientes = custoNutrientes != null ? custoNutrientes : BigDecimal.ZERO;
        custoFertilizacao = custoFertilizacao != null ? custoFertilizacao : BigDecimal.ZERO;
        custosVariaveis = custosVariaveis != null ? custosVariaveis : BigDecimal.ZERO;

        BigDecimal custoTotal = custoRacao
                .add(custoNutrientes)
                .add(custoFertilizacao)
                .add(custosVariaveis);

        // Calcular indicadores
        BigDecimal biomassaAtual = ultimaBiometria != null ? ultimaBiometria.getBiomassaEstimada() : BigDecimal.ZERO;
        BigDecimal pesoMedioAtual = ultimaBiometria != null ? ultimaBiometria.getPesoMedio() : BigDecimal.ZERO;

        // Calcular quantidade estimada baseada na biomassa e peso médio
        Integer quantidadeEstimada = 0;
        if (ultimaBiometria != null && pesoMedioAtual.compareTo(BigDecimal.ZERO) > 0) {
            // biomassa em kg * 1000 / peso médio em gramas = quantidade
            quantidadeEstimada = biomassaAtual
                    .multiply(BigDecimal.valueOf(1000))
                    .divide(pesoMedioAtual, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        BigDecimal custoPorKg = biomassaAtual.compareTo(BigDecimal.ZERO) > 0
                ? custoTotal.divide(biomassaAtual, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Calcular FCA
        BigDecimal totalRacao = racaoRepository.calcularQuantidadeTotalRacaoByLoteId(loteId);
        totalRacao = totalRacao != null ? totalRacao : BigDecimal.ZERO;

        BigDecimal fca = biomassaAtual.compareTo(BigDecimal.ZERO) > 0
                ? totalRacao.divide(biomassaAtual, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal taxaSobrevivencia = ultimaBiometria != null && ultimaBiometria.getSobrevivenciaEstimada() != null
                ? ultimaBiometria.getSobrevivenciaEstimada()
                : BigDecimal.ZERO;

        // Dias de cultivo
        long diasCultivo = ChronoUnit.DAYS.between(lote.getDataPovoamento(), LocalDate.now());

        return RelatorioCustoLoteResponse.builder()
                .loteId(lote.getId())
                .loteCodigo(lote.getCodigo())
                .viveiroNome(lote.getViveiro().getNome())
                .diasCultivo((int) diasCultivo)
                .custoRacao(custoRacao)
                .custoNutrientes(custoNutrientes)
                .custoFertilizacao(custoFertilizacao)
                .custosVariaveis(custosVariaveis)
                .custoTotal(custoTotal)
                .biomassaAtual(biomassaAtual)
                .pesoMedioAtual(pesoMedioAtual)
                .quantidadeEstimada(quantidadeEstimada)
                .custoPorKg(custoPorKg)
                .fca(fca)
                .taxaSobrevivencia(taxaSobrevivencia)
                .build();
    }

    /**
     * Lista relatórios de custos de todos os lotes ativos
     */
    @Transactional(readOnly = true)
    public List<RelatorioCustoLoteResponse> listarRelatoriosLotesAtivos() {
        log.info("Listando relatórios de custos de lotes ativos");

        List<Lote> lotesAtivos = loteRepository.findByStatus(StatusLoteEnum.ATIVO);

        return lotesAtivos.stream()
                .map(lote -> gerarRelatorioCustoLote(lote.getId()))
                .collect(Collectors.toList());
    }
}
