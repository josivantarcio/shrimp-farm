package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import org.springframework.stereotype.Component;

@Component
public class CompradorMapper {

    public Comprador toEntity(CompradorRequest request) {
        return Comprador.builder()
                .nome(request.getNome())
                .cnpj(request.getCnpj())
                .contato(request.getContato())
                .endereco(request.getEndereco())
                .ativo(request.getAtivo())
                .build();
    }

    public CompradorResponse toResponse(Comprador comprador) {
        return CompradorResponse.builder()
                .id(comprador.getId())
                .nome(comprador.getNome())
                .cnpj(comprador.getCnpj())
                .contato(comprador.getContato())
                .endereco(comprador.getEndereco())
                .ativo(comprador.getAtivo())
                .dataCriacao(comprador.getDataCriacao())
                .dataAtualizacao(comprador.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Comprador comprador, CompradorRequest request) {
        comprador.setNome(request.getNome());
        comprador.setCnpj(request.getCnpj());
        comprador.setContato(request.getContato());
        comprador.setEndereco(request.getEndereco());
        comprador.setAtivo(request.getAtivo());
    }
}
