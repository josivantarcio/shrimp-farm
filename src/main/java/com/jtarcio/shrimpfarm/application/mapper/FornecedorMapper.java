package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper {

    public Fornecedor toEntity(FornecedorRequest request) {
        return Fornecedor.builder()
                .nome(request.getNome())
                .cnpj(request.getCnpj())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .contato(request.getContato())
                .observacoes(request.getObservacoes())
                .ativo(request.getAtivo())
                .build();
    }

    public FornecedorResponse toResponse(Fornecedor fornecedor) {
        return FornecedorResponse.builder()
                .id(fornecedor.getId())
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .endereco(fornecedor.getEndereco())
                .cidade(fornecedor.getCidade())
                .estado(fornecedor.getEstado())
                .cep(fornecedor.getCep())
                .contato(fornecedor.getContato())
                .observacoes(fornecedor.getObservacoes())
                .ativo(fornecedor.getAtivo())
                .dataCriacao(fornecedor.getDataCriacao())
                .dataAtualizacao(fornecedor.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Fornecedor fornecedor, FornecedorRequest request) {
        fornecedor.setNome(request.getNome());
        fornecedor.setCnpj(request.getCnpj());
        fornecedor.setTelefone(request.getTelefone());
        fornecedor.setEmail(request.getEmail());
        fornecedor.setEndereco(request.getEndereco());
        fornecedor.setCidade(request.getCidade());
        fornecedor.setEstado(request.getEstado());
        fornecedor.setCep(request.getCep());
        fornecedor.setContato(request.getContato());
        fornecedor.setObservacoes(request.getObservacoes());
        fornecedor.setAtivo(request.getAtivo());
    }
}
