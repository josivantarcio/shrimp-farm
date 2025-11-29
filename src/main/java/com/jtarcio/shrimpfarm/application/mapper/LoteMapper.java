package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import org.springframework.stereotype.Component;

@Component
public class LoteMapper {

    public Lote toEntity(LoteRequest request, Viveiro viveiro) {
        return Lote.builder()
                .viveiro(viveiro)
                .codigo(request.getCodigo())
                .dataPovoamento(request.getDataPovoamento())
                .dataDespesca(request.getDataDespesca())
                .quantidadePosLarvas(request.getQuantidadePosLarvas())
                .custoPosLarvas(request.getCustoPosLarvas())
                .densidadeInicial(request.getDensidadeInicial())
                .status(request.getStatus())
                .observacoes(request.getObservacoes())
                .build();
    }

    public LoteResponse toResponse(Lote lote) {
        return LoteResponse.builder()
                .id(lote.getId())
                .viveiroId(lote.getViveiro().getId())
                .viveiroCodigo(lote.getViveiro().getCodigo())
                .viveiroNome(lote.getViveiro().getNome())
                .fazendaId(lote.getViveiro().getFazenda().getId())
                .fazendaNome(lote.getViveiro().getFazenda().getNome())
                .codigo(lote.getCodigo())
                .dataPovoamento(lote.getDataPovoamento())
                .dataDespesca(lote.getDataDespesca())
                .quantidadePosLarvas(lote.getQuantidadePosLarvas())
                .custoPosLarvas(lote.getCustoPosLarvas())
                .densidadeInicial(lote.getDensidadeInicial())
                .status(lote.getStatus())
                .diasCultivo(lote.getDiasCultivo())
                .observacoes(lote.getObservacoes())
                .quantidadeBiometrias(lote.getBiometrias().size())
                .dataCriacao(lote.getDataCriacao())
                .dataAtualizacao(lote.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Lote lote, LoteRequest request, Viveiro viveiro) {
        lote.setViveiro(viveiro);
        lote.setCodigo(request.getCodigo());
        lote.setDataPovoamento(request.getDataPovoamento());
        lote.setDataDespesca(request.getDataDespesca());
        lote.setQuantidadePosLarvas(request.getQuantidadePosLarvas());
        lote.setCustoPosLarvas(request.getCustoPosLarvas());
        lote.setDensidadeInicial(request.getDensidadeInicial());
        lote.setStatus(request.getStatus());
        lote.setObservacoes(request.getObservacoes());
    }
}
