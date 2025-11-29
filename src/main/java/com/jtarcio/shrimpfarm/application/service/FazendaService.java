package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.application.mapper.FazendaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
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
public class FazendaService {

    private final FazendaRepository fazendaRepository;
    private final FazendaMapper fazendaMapper;

    @Transactional
    public FazendaResponse criar(FazendaRequest request) {
        log.info("Criando nova fazenda: {}", request.getNome());

        Fazenda fazenda = fazendaMapper.toEntity(request);
        Fazenda fazendaSalva = fazendaRepository.save(fazenda);

        log.info("Fazenda criada com sucesso. ID: {}", fazendaSalva.getId());
        return fazendaMapper.toResponse(fazendaSalva);
    }

    @Transactional(readOnly = true)
    public FazendaResponse buscarPorId(Long id) {
        log.debug("Buscando fazenda por ID: {}", id);

        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", id));

        return fazendaMapper.toResponse(fazenda);
    }

    @Transactional(readOnly = true)
    public List<FazendaResponse> listarTodas() {
        log.debug("Listando todas as fazendas");

        return fazendaRepository.findAll().stream()
                .map(fazendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FazendaResponse> listarAtivas() {
        log.debug("Listando fazendas ativas");

        return fazendaRepository.findByAtivaTrue().stream()
                .map(fazendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FazendaResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando fazendas paginadas: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return fazendaRepository.findAll(pageable)
                .map(fazendaMapper::toResponse);
    }

    @Transactional
    public FazendaResponse atualizar(Long id, FazendaRequest request) {
        log.info("Atualizando fazenda ID: {}", id);

        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", id));

        fazendaMapper.updateEntity(fazenda, request);
        Fazenda fazendaAtualizada = fazendaRepository.save(fazenda);

        log.info("Fazenda atualizada com sucesso. ID: {}", id);
        return fazendaMapper.toResponse(fazendaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando fazenda ID: {}", id);

        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", id));

        fazendaRepository.delete(fazenda);
        log.info("Fazenda deletada com sucesso. ID: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando fazenda ID: {}", id);

        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", id));

        fazenda.setAtiva(false);
        fazendaRepository.save(fazenda);

        log.info("Fazenda inativada com sucesso. ID: {}", id);
    }

    @Transactional
    public void ativar(Long id) {
        log.info("Ativando fazenda ID: {}", id);

        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fazenda", id));

        fazenda.setAtiva(true);
        fazendaRepository.save(fazenda);

        log.info("Fazenda ativada com sucesso. ID: {}", id);
    }
}
