package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.CustoVariavelRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CustoVariavelResponse;
import com.jtarcio.shrimpfarm.domain.entity.CustoVariavel;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.springframework.stereotype.Component;

@Component
public class CustoVariavelMapper {

    public CustoVariavel toEntity(CustoVariavelRequest request, Lote lote) {
        return CustoVariavel.builder()
                .lote(lote)
                .dataLancamento(request.getDataLancamento())
                .categoria(request.getCategoria())
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .observacoes(request.getObservacoes())
                .build();
    }

    public CustoVariavelResponse toResponse(CustoVariavel custoVariavel) {
        return CustoVariavelResponse.builder()
                .id(custoVariavel.getId())
                .loteId(custoVariavel.getLote().getId())
                .loteCodigo(custoVariavel.getLote().getCodigo())
                .dataLancamento(custoVariavel.getDataLancamento())
                .categoria(custoVariavel.getCategoria())
                .descricao(custoVariavel.getDescricao())
                .valor(custoVariavel.getValor())
                .observacoes(custoVariavel.getObservacoes())
                .dataCriacao(custoVariavel.getDataCriacao())
                .dataAtualizacao(custoVariavel.getDataAtualizacao())
                .build();
    }

    public void updateEntity(CustoVariavel custoVariavel, CustoVariavelRequest request, Lote lote) {
        custoVariavel.setLote(lote);
        custoVariavel.setDataLancamento(request.getDataLancamento());
        custoVariavel.setCategoria(request.getCategoria());
        custoVariavel.setDescricao(request.getDescricao());
        custoVariavel.setValor(request.getValor());
        custoVariavel.setObservacoes(request.getObservacoes());
    }
}
