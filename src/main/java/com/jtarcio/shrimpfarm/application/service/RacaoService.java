package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.application.mapper.RacaoMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Racao;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.RacaoRepository;
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
public class RacaoService {

    private final RacaoRepository racaoRepository;
    private final LoteRepository loteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final RacaoMapper racaoMapper;

    @Transactional
    public RacaoResponse criar(RacaoRequest request) {
        log.info("Registrando aplicação de ração no lote ID: {}", request.getLoteId());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        if (lote.getStatus() != StatusLoteEnum.ATIVO) {
            throw new BusinessException("Só é possível registrar ração em lotes ativos");
        }

        if (request.getDataAplicacao().isBefore(lote.getDataPovoamento())) {
            throw new BusinessException("Data de aplicação não pode ser anterior ao povoamento");
        }

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        Racao racao = racaoMapper.toEntity(request, lote, fornecedor);
        Racao racaoSalva = racaoRepository.save(racao);

        log.info("Ração registrada com sucesso. ID: {} - Quantidade: {}kg",
                racaoSalva.getId(), racaoSalva.getQuantidade());

        return racaoMapper.toResponse(racaoSalva);
    }

    @Transactional(readOnly = true)
    public RacaoResponse buscarPorId(Long id) {
        log.debug("Buscando ração por ID: {}", id);

        Racao racao = racaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ração", id));

        return racaoMapper.toResponse(racao);
    }

    @Transactional(readOnly = true)
    public List<RacaoResponse> listarPorLote(Long loteId) {
        log.debug("Listando rações do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        return racaoRepository.findByLoteIdOrderByDataAplicacaoAsc(loteId).stream()
                .map(racaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorLote(Long loteId) {
        log.debug("Calculando total de ração do lote ID: {}", loteId);

        BigDecimal total = racaoRepository.calcularQuantidadeTotalRacaoByLoteId(loteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Page<RacaoResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando rações paginadas: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return racaoRepository.findAll(pageable)
                .map(racaoMapper::toResponse);
    }

    @Transactional
    public RacaoResponse atualizar(Long id, RacaoRequest request) {
        log.info("Atualizando ração ID: {}", id);

        Racao racao = racaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ração", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        racaoMapper.updateEntity(racao, request, lote, fornecedor);
        Racao racaoAtualizada = racaoRepository.save(racao);

        log.info("Ração atualizada com sucesso. ID: {}", id);
        return racaoMapper.toResponse(racaoAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando ração ID: {}", id);

        Racao racao = racaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ração", id));

        racaoRepository.delete(racao);
        log.info("Ração deletada com sucesso. ID: {}", id);
    }
}
