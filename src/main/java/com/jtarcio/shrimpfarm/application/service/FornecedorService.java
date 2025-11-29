package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.application.mapper.FornecedorMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
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
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        log.info("Criando novo fornecedor: {}", request.getNome());

        // Validar CNPJ único (se fornecido)
        if (request.getCnpj() != null && !request.getCnpj().isEmpty()) {
            if (fornecedorRepository.findByCnpj(request.getCnpj()).isPresent()) {
                throw new BusinessException("Já existe um fornecedor com o CNPJ: " + request.getCnpj());
            }
        }

        Fornecedor fornecedor = fornecedorMapper.toEntity(request);
        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);

        log.info("Fornecedor criado com sucesso. ID: {}", fornecedorSalvo.getId());
        return fornecedorMapper.toResponse(fornecedorSalvo);
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscarPorId(Long id) {
        log.debug("Buscando fornecedor por ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor", id));

        return fornecedorMapper.toResponse(fornecedor);
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listarTodos() {
        log.debug("Listando todos os fornecedores");

        return fornecedorRepository.findAll().stream()
                .map(fornecedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listarAtivos() {
        log.debug("Listando fornecedores ativos");

        return fornecedorRepository.findByAtivoTrue().stream()
                .map(fornecedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FornecedorResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando fornecedores paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return fornecedorRepository.findAll(pageable)
                .map(fornecedorMapper::toResponse);
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        log.info("Atualizando fornecedor ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor", id));

        // Validar CNPJ único (exceto o próprio)
        if (request.getCnpj() != null && !request.getCnpj().isEmpty()) {
            fornecedorRepository.findByCnpj(request.getCnpj())
                    .ifPresent(f -> {
                        if (!f.getId().equals(id)) {
                            throw new BusinessException("Já existe outro fornecedor com o CNPJ: " + request.getCnpj());
                        }
                    });
        }

        fornecedorMapper.updateEntity(fornecedor, request);
        Fornecedor fornecedorAtualizado = fornecedorRepository.save(fornecedor);

        log.info("Fornecedor atualizado com sucesso. ID: {}", id);
        return fornecedorMapper.toResponse(fornecedorAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando fornecedor ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor", id));

        fornecedorRepository.delete(fornecedor);
        log.info("Fornecedor deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando fornecedor ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor", id));

        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);

        log.info("Fornecedor inativado com sucesso. ID: {}", id);
    }
}
