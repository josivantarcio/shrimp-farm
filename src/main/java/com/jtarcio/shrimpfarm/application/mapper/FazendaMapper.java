package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import org.springframework.stereotype.Component;

@Component
public class FazendaMapper {

    public Fazenda toEntity(FazendaRequest request) {
        return Fazenda.builder()
                .nome(request.getNome())
                .proprietario(request.getProprietario())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .areaTotal(request.getAreaTotal())
                .areaUtil(request.getAreaUtil())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .observacoes(request.getObservacoes())
                .ativa(request.getAtiva())
                .build();
    }

    public FazendaResponse toResponse(Fazenda fazenda) {
        return FazendaResponse.builder()
                .id(fazenda.getId())
                .nome(fazenda.getNome())
                .proprietario(fazenda.getProprietario())
                .endereco(fazenda.getEndereco())
                .cidade(fazenda.getCidade())
                .estado(fazenda.getEstado())
                .cep(fazenda.getCep())
                .areaTotal(fazenda.getAreaTotal())
                .areaUtil(fazenda.getAreaUtil())
                .telefone(fazenda.getTelefone())
                .email(fazenda.getEmail())
                .observacoes(fazenda.getObservacoes())
                .ativa(fazenda.getAtiva())
                .quantidadeViveiros(fazenda.getViveiros().size())
                .dataCriacao(fazenda.getDataCriacao())
                .dataAtualizacao(fazenda.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Fazenda fazenda, FazendaRequest request) {
        fazenda.setNome(request.getNome());
        fazenda.setProprietario(request.getProprietario());
        fazenda.setEndereco(request.getEndereco());
        fazenda.setCidade(request.getCidade());
        fazenda.setEstado(request.getEstado());
        fazenda.setCep(request.getCep());
        fazenda.setAreaTotal(request.getAreaTotal());
        fazenda.setAreaUtil(request.getAreaUtil());
        fazenda.setTelefone(request.getTelefone());
        fazenda.setEmail(request.getEmail());
        fazenda.setObservacoes(request.getObservacoes());
        fazenda.setAtiva(request.getAtiva());
    }
}
