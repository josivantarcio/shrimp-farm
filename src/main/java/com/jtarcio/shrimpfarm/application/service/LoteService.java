package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.application.mapper.LoteMapper;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoteService {

    private final LoteRepository loteRepository;
    private final ViveiroRepository viveiroRepository;
    private final LoteMapper loteMapper;

    @Transactional
    public LoteResponse criar(LoteRequest request) {
        log.info("Criando novo lote: {} no viveiro ID: {}", request.getCodigo(), request.getViveiroId());

        // Validar viveiro
        Viveiro viveiro = viveiroRepository.findById(request.getViveiroId())
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", request.getViveiroId()));

        if (!viveiro.getAtivo()) {
            throw new BusinessException("Não é possível criar lote em viveiro inativo");
        }

        // Verificar se código já existe
        if (loteRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new BusinessException("Já existe um lote com o código: " + request.getCodigo());
        }

        // Validar datas
        if (request.getDataDespesca() != null &&
                request.getDataDespesca().isBefore(request.getDataPovoamento())) {
            throw new BusinessException("Data de despesca não pode ser anterior à data de povoamento");
        }

        Lote lote = loteMapper.toEntity(request, viveiro);
        Lote loteSalvo = loteRepository.save(lote);

        // Atualizar status do viveiro se o lote for ativo
        if (loteSalvo.getStatus() == StatusLoteEnum.ATIVO) {
            viveiro.setStatus(StatusViveiroEnum.OCUPADO);
            viveiroRepository.save(viveiro);
        }

        log.info("Lote criado com sucesso. ID: {}", loteSalvo.getId());
        return loteMapper.toResponse(loteSalvo);
    }

    @Transactional(readOnly = true)
    public LoteResponse buscarPorId(Long id) {
        log.debug("Buscando lote por ID: {}", id);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        return loteMapper.toResponse(lote);
    }

    @Transactional(readOnly = true)
    public LoteResponse buscarPorCodigo(String codigo) {
        log.debug("Buscando lote por código: {}", codigo);

        Lote lote = loteRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Lote com código " + codigo + " não encontrado"));

        return loteMapper.toResponse(lote);
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarTodos() {
        log.debug("Listando todos os lotes");

        return loteRepository.findAll().stream()
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorViveiro(Long viveiroId) {
        log.debug("Listando lotes do viveiro ID: {}", viveiroId);

        if (!viveiroRepository.existsById(viveiroId)) {
            throw new EntityNotFoundException("Viveiro", viveiroId);
        }

        return loteRepository.findByViveiroId(viveiroId).stream()
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorFazenda(Long fazendaId) {
        log.debug("Listando lotes da fazenda ID: {}", fazendaId);

        return loteRepository.findByFazendaId(fazendaId).stream()
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorStatus(StatusLoteEnum status) {
        log.debug("Listando lotes com status: {}", status);

        return loteRepository.findByStatus(status).stream()
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarAtivos() {
        log.debug("Listando lotes ativos");

        return loteRepository.findLotesAtivos().stream()
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LoteResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando lotes paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return loteRepository.findAll(pageable)
                .map(loteMapper::toResponse);
    }

    @Transactional
    public LoteResponse atualizar(Long id, LoteRequest request) {
        log.info("Atualizando lote ID: {}", id);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        Viveiro viveiro = viveiroRepository.findById(request.getViveiroId())
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", request.getViveiroId()));

        loteMapper.updateEntity(lote, request, viveiro);
        Lote loteAtualizado = loteRepository.save(lote);

        log.info("Lote atualizado com sucesso. ID: {}", id);
        return loteMapper.toResponse(loteAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando lote ID: {}", id);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        loteRepository.delete(lote);
        log.info("Lote deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public LoteResponse iniciarCultivo(Long id) {
        log.info("Iniciando cultivo do lote ID: {}", id);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        if (lote.getStatus() != StatusLoteEnum.PLANEJADO) {
            throw new BusinessException("Só é possível iniciar cultivo de lotes planejados");
        }

        lote.setStatus(StatusLoteEnum.ATIVO);
        lote.getViveiro().setStatus(StatusViveiroEnum.OCUPADO);

        Lote loteAtualizado = loteRepository.save(lote);

        log.info("Cultivo iniciado com sucesso. Lote ID: {}", id);
        return loteMapper.toResponse(loteAtualizado);
    }

    @Transactional
    public LoteResponse finalizarCultivo(Long id, LocalDate dataDespesca) {
        log.info("Finalizando cultivo do lote ID: {} em {}", id, dataDespesca);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        if (lote.getStatus() != StatusLoteEnum.ATIVO) {
            throw new BusinessException("Só é possível finalizar lotes ativos");
        }

        if (dataDespesca.isBefore(lote.getDataPovoamento())) {
            throw new BusinessException("Data de despesca não pode ser anterior à data de povoamento");
        }

        lote.setDataDespesca(dataDespesca);
        lote.setStatus(StatusLoteEnum.FINALIZADO);
        lote.getViveiro().setStatus(StatusViveiroEnum.DISPONIVEL);

        Lote loteAtualizado = loteRepository.save(lote);

        log.info("Cultivo finalizado com sucesso. Lote ID: {}", id);
        return loteMapper.toResponse(loteAtualizado);
    }

    @Transactional
    public LoteResponse cancelarLote(Long id) {
        log.info("Cancelando lote ID: {}", id);

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote", id));

        if (lote.getStatus() == StatusLoteEnum.FINALIZADO) {
            throw new BusinessException("Não é possível cancelar lote já finalizado");
        }

        lote.setStatus(StatusLoteEnum.CANCELADO);
        lote.getViveiro().setStatus(StatusViveiroEnum.DISPONIVEL);

        Lote loteAtualizado = loteRepository.save(lote);

        log.info("Lote cancelado com sucesso. ID: {}", id);
        return loteMapper.toResponse(loteAtualizado);
    }
}
