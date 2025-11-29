package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import org.springframework.stereotype.Component;

@Component
public class ViveiroMapper {

    public Viveiro toEntity(ViveiroRequest request, Fazenda fazenda) {
        return Viveiro.builder()
                .fazenda(fazenda)
                .codigo(request.getCodigo())
                .nome(request.getNome())
                .area(request.getArea())
                .profundidadeMedia(request.getProfundidadeMedia())
                .volume(request.getVolume())
                .status(request.getStatus())
                .observacoes(request.getObservacoes())
                .ativo(request.getAtivo())
                .build();
    }

    public ViveiroResponse toResponse(Viveiro viveiro) {
        return ViveiroResponse.builder()
                .id(viveiro.getId())
                .fazendaId(viveiro.getFazenda().getId())
                .fazendaNome(viveiro.getFazenda().getNome())
                .codigo(viveiro.getCodigo())
                .nome(viveiro.getNome())
                .area(viveiro.getArea())
                .profundidadeMedia(viveiro.getProfundidadeMedia())
                .volume(viveiro.getVolume())
                .status(viveiro.getStatus())
                .observacoes(viveiro.getObservacoes())
                .ativo(viveiro.getAtivo())
                .quantidadeLotes(viveiro.getLotes().size())
                .dataCriacao(viveiro.getDataCriacao())
                .dataAtualizacao(viveiro.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Viveiro viveiro, ViveiroRequest request, Fazenda fazenda) {
        viveiro.setFazenda(fazenda);
        viveiro.setCodigo(request.getCodigo());
        viveiro.setNome(request.getNome());
        viveiro.setArea(request.getArea());
        viveiro.setProfundidadeMedia(request.getProfundidadeMedia());
        viveiro.setVolume(request.getVolume());
        viveiro.setStatus(request.getStatus());
        viveiro.setObservacoes(request.getObservacoes());
        viveiro.setAtivo(request.getAtivo());
    }
}
