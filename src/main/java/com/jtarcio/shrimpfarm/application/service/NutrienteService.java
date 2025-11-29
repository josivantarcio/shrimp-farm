package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.NutrienteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.NutrienteResponse;
import com.jtarcio.shrimpfarm.application.mapper.NutrienteMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.NutrienteRepository;
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
public class NutrienteService {

    private final NutrienteRepository nutrienteRepository;
    private final LoteRepository loteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final NutrienteMapper nutrienteMapper;

    @Transactional
    public NutrienteResponse criar(NutrienteRequest request) {
        log.info("Registrando aplicação de nutriente no lote ID: {}", request.getLoteId());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        if (lote.getStatus() != StatusLoteEnum.ATIVO) {
            throw new BusinessException("Só é possível registrar nutrientes em lotes ativos");
        }

        if (request.getDataAplicacao().isBefore(lote.getDataPovoamento())) {
            throw new BusinessException("Data de aplicação não pode ser anterior ao povoamento");
        }

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        Nutriente nutriente = nutrienteMapper.toEntity(request, lote, fornecedor);
        Nutriente nutrienteSalvo = nutrienteRepository.save(nutriente);

        log.info("Nutriente registrado com sucesso. ID: {} - Produto: {}",
                nutrienteSalvo.getId(), nutrienteSalvo.getProduto());

        return nutrienteMapper.toResponse(nutrienteSalvo);
    }

    @Transactional(readOnly = true)
    public NutrienteResponse buscarPorId(Long id) {
        log.debug("Buscando nutriente por ID: {}", id);

        Nutriente nutriente = nutrienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nutriente", id));

        return nutrienteMapper.toResponse(nutriente);
    }

    @Transactional(readOnly = true)
    public List<NutrienteResponse> listarPorLote(Long loteId) {
        log.debug("Listando nutrientes do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        return nutrienteRepository.findByLoteIdOrderByDataAplicacaoAsc(loteId).stream()
                .map(nutrienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCustoTotalPorLote(Long loteId) {
        log.debug("Calculando custo total de nutrientes do lote ID: {}", loteId);

        BigDecimal total = nutrienteRepository.calcularCustoTotalNutrientesByLoteId(loteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Page<NutrienteResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando nutrientes paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return nutrienteRepository.findAll(pageable)
                .map(nutrienteMapper::toResponse);
    }

    @Transactional
    public NutrienteResponse atualizar(Long id, NutrienteRequest request) {
        log.info("Atualizando nutriente ID: {}", id);

        Nutriente nutriente = nutrienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nutriente", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        Fornecedor fornecedor = null;
        if (request.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.getFornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor", request.getFornecedorId()));
        }

        nutrienteMapper.updateEntity(nutriente, request, lote, fornecedor);
        Nutriente nutrienteAtualizado = nutrienteRepository.save(nutriente);

        log.info("Nutriente atualizado com sucesso. ID: {}", id);
        return nutrienteMapper.toResponse(nutrienteAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando nutriente ID: {}", id);

        Nutriente nutriente = nutrienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nutriente", id));

        nutrienteRepository.delete(nutriente);
        log.info("Nutriente deletado com sucesso. ID: {}", id);
    }
}
