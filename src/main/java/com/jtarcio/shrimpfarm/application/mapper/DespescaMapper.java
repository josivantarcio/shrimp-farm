package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.springframework.stereotype.Component;

@Component
public class DespescaMapper {

    public Despesca toEntity(DespescaRequest request, Lote lote, Comprador comprador) {
        return Despesca.builder()
                .lote(lote)
                .comprador(comprador)
                .dataDespesca(request.getDataDespesca())
                .pesoTotal(request.getPesoTotal())
                .quantidadeDespescada(request.getQuantidadeDespescada())
                .pesoMedioFinal(request.getPesoMedioFinal())
                .precoVendaKg(request.getPrecoVendaKg())
                .custoDespesca(request.getCustoDespesca())
                .observacoes(request.getObservacoes())
                .build();
    }

    public DespescaResponse toResponse(Despesca despesca) {
        return DespescaResponse.builder()
                .id(despesca.getId())
                .loteId(despesca.getLote().getId())
                .loteCodigo(despesca.getLote().getCodigo())
                .compradorId(despesca.getComprador() != null ? despesca.getComprador().getId() : null)
                .compradorNome(despesca.getComprador() != null ? despesca.getComprador().getNome() : null)
                .dataDespesca(despesca.getDataDespesca())
                .pesoTotal(despesca.getPesoTotal())
                .quantidadeDespescada(despesca.getQuantidadeDespescada())
                .pesoMedioFinal(despesca.getPesoMedioFinal())
                .taxaSobrevivencia(despesca.getTaxaSobrevivencia())
                .precoVendaKg(despesca.getPrecoVendaKg())
                .receitaTotal(despesca.getReceitaTotal())
                .custoDespesca(despesca.getCustoDespesca())
                .observacoes(despesca.getObservacoes())
                .dataCriacao(despesca.getDataCriacao())
                .dataAtualizacao(despesca.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Despesca despesca, DespescaRequest request, Lote lote, Comprador comprador) {
        despesca.setLote(lote);
        despesca.setComprador(comprador);
        despesca.setDataDespesca(request.getDataDespesca());
        despesca.setPesoTotal(request.getPesoTotal());
        despesca.setQuantidadeDespescada(request.getQuantidadeDespescada());
        despesca.setPesoMedioFinal(request.getPesoMedioFinal());
        despesca.setPrecoVendaKg(request.getPrecoVendaKg());
        despesca.setCustoDespesca(request.getCustoDespesca());
        despesca.setObservacoes(request.getObservacoes());
    }
}
