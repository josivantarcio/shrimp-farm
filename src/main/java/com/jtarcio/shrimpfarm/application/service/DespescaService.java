package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.application.mapper.DespescaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CompradorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.DespescaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class DespescaService {

    private final DespescaRepository despescaRepository;
    private final LoteRepository loteRepository;
    private final CompradorRepository compradorRepository;
    private final DespescaMapper despescaMapper;

    @Transactional
    public DespescaResponse criar(DespescaRequest request) {
        log.info("Registrando despesca do lote ID: {}", request.getLoteId());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        // Validações
        if (lote.getStatus() != StatusLoteEnum.ATIVO) {
            throw new BusinessException("Só é possível registrar despesca em lotes ativos");
        }

        if (lote.getDespesca() != null) {
            throw new BusinessException("Lote já possui despesca registrada");
        }

        if (request.getDataDespesca().isBefore(lote.getDataPovoamento())) {
            throw new BusinessException("Data de despesca não pode ser anterior ao povoamento");
        }

        Comprador comprador = null;
        if (request.getCompradorId() != null) {
            comprador = compradorRepository.findById(request.getCompradorId())
                    .orElseThrow(() -> new EntityNotFoundException("Comprador", request.getCompradorId()));
        }

        Despesca despesca = despescaMapper.toEntity(request, lote, comprador);

        // Calcular taxa de sobrevivência
        BigDecimal taxaSobrevivencia = BigDecimal.valueOf(request.getQuantidadeDespescada())
                .divide(BigDecimal.valueOf(lote.getQuantidadePosLarvas()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        despesca.setTaxaSobrevivencia(taxaSobrevivencia);

        // Calcular receita total se houver preço
        if (request.getPrecoVendaKg() != null) {
            BigDecimal receita = request.getPesoTotal().multiply(request.getPrecoVendaKg());
            despesca.setReceitaTotal(receita);
        }

        Despesca despescaSalva = despescaRepository.save(despesca);

        // Atualizar status do lote e viveiro
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        lote.setDataDespesca(request.getDataDespesca());
        lote.getViveiro().setStatus(StatusViveiroEnum.DISPONIVEL);
        loteRepository.save(lote);

        log.info("Despesca registrada com sucesso. Lote ID: {} - Peso total: {}kg - Taxa sobrevivência: {}%",
                lote.getId(), despescaSalva.getPesoTotal(), taxaSobrevivencia);

        return despescaMapper.toResponse(despescaSalva);
    }

    @Transactional(readOnly = true)
    public DespescaResponse buscarPorId(Long id) {
        log.debug("Buscando despesca por ID: {}", id);

        Despesca despesca = despescaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesca", id));

        return despescaMapper.toResponse(despesca);
    }

    @Transactional(readOnly = true)
    public DespescaResponse buscarPorLote(Long loteId) {
        log.debug("Buscando despesca do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        Despesca despesca = despescaRepository.findByLoteId(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Despesca não encontrada para o lote ID: " + loteId));

        return despescaMapper.toResponse(despesca);
    }

    @Transactional
    public DespescaResponse atualizar(Long id, DespescaRequest request) {
        log.info("Atualizando despesca ID: {}", id);

        Despesca despesca = despescaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesca", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        Comprador comprador = null;
        if (request.getCompradorId() != null) {
            comprador = compradorRepository.findById(request.getCompradorId())
                    .orElseThrow(() -> new EntityNotFoundException("Comprador", request.getCompradorId()));
        }

        despescaMapper.updateEntity(despesca, request, lote, comprador);

        // Recalcular taxa de sobrevivência
        BigDecimal taxaSobrevivencia = BigDecimal.valueOf(request.getQuantidadeDespescada())
                .divide(BigDecimal.valueOf(lote.getQuantidadePosLarvas()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        despesca.setTaxaSobrevivencia(taxaSobrevivencia);

        // Recalcular receita total
        if (request.getPrecoVendaKg() != null) {
            BigDecimal receita = request.getPesoTotal().multiply(request.getPrecoVendaKg());
            despesca.setReceitaTotal(receita);
        }

        Despesca despescaAtualizada = despescaRepository.save(despesca);

        log.info("Despesca atualizada com sucesso. ID: {}", id);
        return despescaMapper.toResponse(despescaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando despesca ID: {}", id);

        Despesca despesca = despescaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesca", id));

        // Voltar status do lote para ativo
        Lote lote = despesca.getLote();
        lote.setStatus(StatusLoteEnum.ATIVO);
        lote.setDataDespesca(null);
        lote.getViveiro().setStatus(StatusViveiroEnum.OCUPADO);
        loteRepository.save(lote);

        despescaRepository.delete(despesca);
        log.info("Despesca deletada com sucesso. ID: {}", id);
    }
}
