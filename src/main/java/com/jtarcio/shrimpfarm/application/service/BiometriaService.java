package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.application.mapper.BiometriaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.RacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiometriaService {

    private final BiometriaRepository biometriaRepository;
    private final LoteRepository loteRepository;
    private final RacaoRepository racaoRepository;
    private final BiometriaMapper biometriaMapper;

    @Transactional
    public BiometriaResponse criar(BiometriaRequest request) {
        log.info("Criando nova biometria para lote ID: {} em {}",
                request.getLoteId(), request.getDataBiometria());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        // Validações
        validarBiometria(request, lote);

        Biometria biometria = biometriaMapper.toEntity(request, lote);

        // Calcular indicadores
        calcularIndicadores(biometria, lote);

        Biometria biometriaSalva = biometriaRepository.save(biometria);

        log.info("Biometria criada com sucesso. ID: {} - Peso médio: {}g",
                biometriaSalva.getId(), biometriaSalva.getPesoMedio());

        return biometriaMapper.toResponse(biometriaSalva);
    }

    @Transactional(readOnly = true)
    public BiometriaResponse buscarPorId(Long id) {
        log.debug("Buscando biometria por ID: {}", id);

        Biometria biometria = biometriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Biometria", id));

        return biometriaMapper.toResponse(biometria);
    }

    @Transactional(readOnly = true)
    public List<BiometriaResponse> listarPorLote(Long loteId) {
        log.debug("Listando biometrias do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        return biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(loteId).stream()
                .map(biometriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BiometriaResponse buscarUltimaBiometriaDoLote(Long loteId) {
        log.debug("Buscando última biometria do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        Biometria biometria = biometriaRepository.findUltimaBiometriaByLoteId(loteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nenhuma biometria encontrada para o lote ID: " + loteId));

        return biometriaMapper.toResponse(biometria);
    }

    @Transactional(readOnly = true)
    public Page<BiometriaResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando biometrias paginadas: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return biometriaRepository.findAll(pageable)
                .map(biometriaMapper::toResponse);
    }

    @Transactional
    public BiometriaResponse atualizar(Long id, BiometriaRequest request) {
        log.info("Atualizando biometria ID: {}", id);

        Biometria biometria = biometriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Biometria", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        validarBiometria(request, lote);

        biometriaMapper.updateEntity(biometria, request, lote);
        calcularIndicadores(biometria, lote);

        Biometria biometriaAtualizada = biometriaRepository.save(biometria);

        log.info("Biometria atualizada com sucesso. ID: {}", id);
        return biometriaMapper.toResponse(biometriaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando biometria ID: {}", id);

        Biometria biometria = biometriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Biometria", id));

        biometriaRepository.delete(biometria);
        log.info("Biometria deletada com sucesso. ID: {}", id);
    }

    // Métodos privados auxiliares

    private void validarBiometria(BiometriaRequest request, Lote lote) {
        // Validar se lote está ativo
        if (lote.getStatus() != StatusLoteEnum.ATIVO && lote.getStatus() != StatusLoteEnum.PLANEJADO) {
            throw new BusinessException("Só é possível registrar biometria em lotes ativos ou planejados");
        }

        // Validar se data é posterior ao povoamento
        if (request.getDataBiometria().isBefore(lote.getDataPovoamento())) {
            throw new BusinessException("Data da biometria não pode ser anterior à data de povoamento");
        }

        // Validar se peso total da amostra confere
        if (request.getPesoTotalAmostra() != null) {
            BigDecimal pesoCalculado = request.getPesoMedio()
                    .multiply(BigDecimal.valueOf(request.getQuantidadeAmostrada()));
            BigDecimal diferenca = pesoCalculado.subtract(request.getPesoTotalAmostra()).abs();
            BigDecimal margemErro = pesoCalculado.multiply(BigDecimal.valueOf(0.05)); // 5% de margem

            if (diferenca.compareTo(margemErro) > 0) {
                log.warn("Divergência no peso total da amostra. Calculado: {}, Informado: {}",
                        pesoCalculado, request.getPesoTotalAmostra());
            }
        }
    }

    private void calcularIndicadores(Biometria biometria, Lote lote) {
        // 1. Calcular GPD (Ganho de Peso Diário)
        if (biometria.getDiaCultivo() > 0) {
            BigDecimal gpd = biometria.getPesoMedio()
                    .divide(BigDecimal.valueOf(biometria.getDiaCultivo()), 4, RoundingMode.HALF_UP);
            biometria.setGanhoPesoDiario(gpd);
        }

        // 2. Estimar biomassa (peso médio * quantidade estimada de camarões / 1000)
        // Assumindo sobrevivência de 80% como padrão
        BigDecimal sobrevivenciaEstimada = BigDecimal.valueOf(0.80);
        BigDecimal quantidadeEstimada = BigDecimal.valueOf(lote.getQuantidadePosLarvas())
                .multiply(sobrevivenciaEstimada);
        BigDecimal biomassa = biometria.getPesoMedio()
                .multiply(quantidadeEstimada)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP); // em kg
        biometria.setBiomassaEstimada(biomassa);
        biometria.setSobrevivenciaEstimada(sobrevivenciaEstimada.multiply(BigDecimal.valueOf(100)));

        // 3. Calcular FCA (Fator de Conversão Alimentar)
        BigDecimal racaoTotal = racaoRepository.calcularQuantidadeTotalRacaoByLoteId(lote.getId());
        if (racaoTotal != null && biomassa.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal fca = racaoTotal.divide(biomassa, 3, RoundingMode.HALF_UP);
            biometria.setFatorConversaoAlimentar(fca);
        }
    }
}
