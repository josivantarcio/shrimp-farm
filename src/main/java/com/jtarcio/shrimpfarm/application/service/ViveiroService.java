package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.application.mapper.ViveiroMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViveiroService {

    private final ViveiroRepository viveiroRepository;
    private final FazendaRepository fazendaRepository;
    private final ViveiroMapper viveiroMapper;

    @Transactional
    public ViveiroResponse criar(ViveiroRequest request) {
        log.info("Criando novo viveiro: {} para fazenda ID: {}", request.getNome(), request.getFazendaId());

        Fazenda fazenda = fazendaRepository.findById(request.getFazendaId())
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", request.getFazendaId()));

        if (!fazenda.getAtiva()) {
            throw new BusinessException("Não é possível criar viveiro em fazenda inativa");
        }

        Viveiro viveiro = viveiroMapper.toEntity(request, fazenda);
        Viveiro viveiroSalvo = viveiroRepository.save(viveiro);

        log.info("Viveiro criado com sucesso. ID: {}", viveiroSalvo.getId());
        return viveiroMapper.toResponse(viveiroSalvo);
    }

    @Transactional(readOnly = true)
    public ViveiroResponse buscarPorId(Long id) {
        log.debug("Buscando viveiro por ID: {}", id);

        Viveiro viveiro = viveiroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", id));

        return viveiroMapper.toResponse(viveiro);
    }

    @Transactional(readOnly = true)
    public List<ViveiroResponse> listarTodos() {
        log.debug("Listando todos os viveiros");

        return viveiroRepository.findAll().stream()
                .map(viveiroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ViveiroResponse> listarPorFazenda(Long fazendaId) {
        log.debug("Listando viveiros da fazenda ID: {}", fazendaId);

        if (!fazendaRepository.existsById(fazendaId)) {
            throw new EntityNotFoundException("Fazenda", fazendaId);
        }

        return viveiroRepository.findByFazendaId(fazendaId).stream()
                .map(viveiroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ViveiroResponse> listarPorStatus(StatusViveiroEnum status) {
        log.debug("Listando viveiros com status: {}", status);

        return viveiroRepository.findByStatus(status).stream()
                .map(viveiroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ViveiroResponse> listarAtivosPorFazenda(Long fazendaId) {
        log.debug("Listando viveiros ativos da fazenda ID: {}", fazendaId);

        if (!fazendaRepository.existsById(fazendaId)) {
            throw new EntityNotFoundException("Fazenda", fazendaId);
        }

        return viveiroRepository.findByFazendaIdAndAtivoTrue(fazendaId).stream()
                .map(viveiroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ViveiroResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando viveiros paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return viveiroRepository.findAll(pageable)
                .map(viveiroMapper::toResponse);
    }

    @Transactional
    public ViveiroResponse atualizar(Long id, ViveiroRequest request) {
        log.info("Atualizando viveiro ID: {}", id);

        Viveiro viveiro = viveiroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", id));

        Fazenda fazenda = fazendaRepository.findById(request.getFazendaId())
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", request.getFazendaId()));

        viveiroMapper.updateEntity(viveiro, request, fazenda);
        Viveiro viveiroAtualizado = viveiroRepository.save(viveiro);

        log.info("Viveiro atualizado com sucesso. ID: {}", id);
        return viveiroMapper.toResponse(viveiroAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando viveiro ID: {}", id);

        Viveiro viveiro = viveiroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", id));

        if (!viveiro.getLotes().isEmpty()) {
            throw new BusinessException("Não é possível deletar viveiro com lotes cadastrados");
        }

        viveiroRepository.delete(viveiro);
        log.info("Viveiro deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando viveiro ID: {}", id);

        Viveiro viveiro = viveiroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", id));

        viveiro.setAtivo(false);
        viveiroRepository.save(viveiro);

        log.info("Viveiro inativado com sucesso. ID: {}", id);
    }

    @Transactional
    public void mudarStatus(Long id, StatusViveiroEnum novoStatus) {
        log.info("Mudando status do viveiro ID: {} para {}", id, novoStatus);

        Viveiro viveiro = viveiroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viveiro", id));

        viveiro.setStatus(novoStatus);
        viveiroRepository.save(viveiro);

        log.info("Status do viveiro atualizado com sucesso. ID: {}", id);
    }
}
