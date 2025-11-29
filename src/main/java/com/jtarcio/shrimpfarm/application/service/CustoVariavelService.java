package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.CustoVariavelRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CustoVariavelResponse;
import com.jtarcio.shrimpfarm.application.mapper.CustoVariavelMapper;
import com.jtarcio.shrimpfarm.domain.entity.CustoVariavel;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CustoVariavelRepository;
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
public class CustoVariavelService {

    private final CustoVariavelRepository custoVariavelRepository;
    private final LoteRepository loteRepository;
    private final CustoVariavelMapper custoVariavelMapper;

    @Transactional
    public CustoVariavelResponse criar(CustoVariavelRequest request) {
        log.info("Registrando custo variável no lote ID: {} - Categoria: {}",
                request.getLoteId(), request.getCategoria());

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        CustoVariavel custoVariavel = custoVariavelMapper.toEntity(request, lote);
        CustoVariavel custoVariavelSalvo = custoVariavelRepository.save(custoVariavel);

        log.info("Custo variável registrado com sucesso. ID: {} - Valor: R$ {}",
                custoVariavelSalvo.getId(), custoVariavelSalvo.getValor());

        return custoVariavelMapper.toResponse(custoVariavelSalvo);
    }

    @Transactional(readOnly = true)
    public CustoVariavelResponse buscarPorId(Long id) {
        log.debug("Buscando custo variável por ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo Variável", id));

        return custoVariavelMapper.toResponse(custoVariavel);
    }

    @Transactional(readOnly = true)
    public List<CustoVariavelResponse> listarPorLote(Long loteId) {
        log.debug("Listando custos variáveis do lote ID: {}", loteId);

        if (!loteRepository.existsById(loteId)) {
            throw new EntityNotFoundException("Lote", loteId);
        }

        return custoVariavelRepository.findByLoteIdOrderByDataLancamentoAsc(loteId).stream()
                .map(custoVariavelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustoVariavelResponse> listarPorCategoria(CategoriaGastoEnum categoria) {
        log.debug("Listando custos variáveis por categoria: {}", categoria);

        return custoVariavelRepository.findByCategoria(categoria).stream()
                .map(custoVariavelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorLote(Long loteId) {
        log.debug("Calculando total de custos variáveis do lote ID: {}", loteId);

        BigDecimal total = custoVariavelRepository.calcularCustoTotalVariavelByLoteId(loteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Page<CustoVariavelResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando custos variáveis paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return custoVariavelRepository.findAll(pageable)
                .map(custoVariavelMapper::toResponse);
    }

    @Transactional
    public CustoVariavelResponse atualizar(Long id, CustoVariavelRequest request) {
        log.info("Atualizando custo variável ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo Variável", id));

        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote", request.getLoteId()));

        custoVariavelMapper.updateEntity(custoVariavel, request, lote);
        CustoVariavel custoVariavelAtualizado = custoVariavelRepository.save(custoVariavel);

        log.info("Custo variável atualizado com sucesso. ID: {}", id);
        return custoVariavelMapper.toResponse(custoVariavelAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando custo variável ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo Variável", id));

        custoVariavelRepository.delete(custoVariavel);
        log.info("Custo variável deletado com sucesso. ID: {}", id);
    }
}
