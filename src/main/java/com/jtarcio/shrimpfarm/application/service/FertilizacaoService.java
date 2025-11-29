package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FertilizacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FertilizacaoResponse;
import com.jtarcio.shrimpfarm.application.mapper.FertilizacaoMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FertilizacaoRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FertilizacaoService {

    private final FertilizacaoRepository fertilizacaoRepository;
    private final LoteRepository loteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final FertilizacaoMapper fertilizacaoMapper;

    @Transactional
    public FertilizacaoResponse criar(FertilizacaoRequest request) {
        log.info("Registrando fertilização no lote ID: {}", request.getLoteId());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        if (lote.getStatus() != StatusLoteEnum.ATIVO && lote.getStatus() != StatusLoteEnum.PLANEJADO) {
            throw new BusinessException("Só é possível registrar fertilização em lotes ativos ou planejados");
        }

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        Fertilizacao fertilizacao = fertilizacaoMapper.toEntity(request, lote, fornecedor);
        Fertilizacao fertilizacaoSalva = fertilizacaoRepository.save(fertilizacao);

        log.info("Fertilização registrada com sucesso. ID: {} - Produto: {}",
                fertilizacaoSalva.getId(), fertilizacaoSalva.getProduto());

        return fertilizacaoMapper.toResponse(fertilizacaoSalva);
    }

    @Transactional(readOnly = true)
    public FertilizacaoResponse buscarPorId(Long id) {
        log.debug("Buscando fertilização por ID: {}", id);

        Fertilizacao fertilizacao = fertilizacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fertilização", id));

        return fertilizacaoMapper.toResponse(fertilizacao);
    }

    @Transactional(readOnly = true)
    public List<FertilizacaoResponse> listarPorLote(Long loteId) {
        log.debug("Listando fertilizações do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        return fertilizacaoRepository.findByLoteIdOrderByDataAplicacaoAsc(loteId).stream()
                .map(fertilizacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCustoTotalPorLote(Long loteId) {
        log.debug("Calculando custo total de fertilização do lote ID: {}", loteId);

        BigDecimal total = fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(loteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Page<FertilizacaoResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando fertilizações paginadas: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return fertilizacaoRepository.findAll(pageable)
                .map(fertilizacaoMapper::toResponse);
    }

    @Transactional
    public FertilizacaoResponse atualizar(Long id, FertilizacaoRequest request) {
        log.info("Atualizando fertilização ID: {}", id);

        Fertilizacao fertilizacao = fertilizacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fertilização", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        fertilizacaoMapper.updateEntity(fertilizacao, request, lote, fornecedor);
        Fertilizacao fertilizacaoAtualizada = fertilizacaoRepository.save(fertilizacao);

        log.info("Fertilização atualizada com sucesso. ID: {}", id);
        return fertilizacaoMapper.toResponse(fertilizacaoAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando fertilização ID: {}", id);

        Fertilizacao fertilizacao = fertilizacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fertilização", id));

        fertilizacaoRepository.delete(fertilizacao);
        log.info("Fertilização deletada com sucesso. ID: {}", id);
    }
}
