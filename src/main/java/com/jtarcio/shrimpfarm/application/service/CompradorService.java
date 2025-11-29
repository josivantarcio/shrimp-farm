package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.application.mapper.CompradorMapper;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CompradorRepository;
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
public class CompradorService {

    private final CompradorRepository compradorRepository;
    private final CompradorMapper compradorMapper;

    @Transactional
    public CompradorResponse criar(CompradorRequest request) {
        log.info("Criando novo comprador: {}", request.getNome());

        // Validar CNPJ/CPF único
        if (compradorRepository.findByCnpj(request.getCnpj()).isPresent()) {
            throw new BusinessException("Já existe um comprador com o CNPJ/CPF: " + request.getCnpj());
        }

        Comprador comprador = compradorMapper.toEntity(request);
        Comprador compradorSalvo = compradorRepository.save(comprador);

        log.info("Comprador criado com sucesso. ID: {}", compradorSalvo.getId());
        return compradorMapper.toResponse(compradorSalvo);
    }

    @Transactional(readOnly = true)
    public CompradorResponse buscarPorId(Long id) {
        log.debug("Buscando comprador por ID: {}", id);

        Comprador comprador = compradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprador", id));

        return compradorMapper.toResponse(comprador);
    }

    @Transactional(readOnly = true)
    public List<CompradorResponse> listarTodos() {
        log.debug("Listando todos os compradores");

        return compradorRepository.findAll().stream()
                .map(compradorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompradorResponse> listarAtivos() {
        log.debug("Listando compradores ativos");

        return compradorRepository.findByAtivoTrue().stream()
                .map(compradorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CompradorResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando compradores paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return compradorRepository.findAll(pageable)
                .map(compradorMapper::toResponse);
    }

    @Transactional
    public CompradorResponse atualizar(Long id, CompradorRequest request) {
        log.info("Atualizando comprador ID: {}", id);

        Comprador comprador = compradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprador", id));

        // Validar CNPJ/CPF único (exceto o próprio)
        compradorRepository.findByCnpj(request.getCnpj())
                .ifPresent(c -> {
                    if (!c.getId().equals(id)) {
                        throw new BusinessException("Já existe outro comprador com o CNPJ/CPF: " + request.getCnpj());
                    }
                });

        compradorMapper.updateEntity(comprador, request);
        Comprador compradorAtualizado = compradorRepository.save(comprador);

        log.info("Comprador atualizado com sucesso. ID: {}", id);
        return compradorMapper.toResponse(compradorAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando comprador ID: {}", id);

        Comprador comprador = compradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprador", id));

        compradorRepository.delete(comprador);
        log.info("Comprador deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando comprador ID: {}", id);

        Comprador comprador = compradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprador", id));

        comprador.setAtivo(false);
        compradorRepository.save(comprador);

        log.info("Comprador inativado com sucesso. ID: {}", id);
    }
}
