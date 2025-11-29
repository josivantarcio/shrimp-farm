package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.NutrienteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.NutrienteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import org.springframework.stereotype.Component;

@Component
public class NutrienteMapper {

    public Nutriente toEntity(NutrienteRequest request, Lote lote, Fornecedor fornecedor) {
        return Nutriente.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .tipoNutriente(request.getTipoNutriente())
                .produto(request.getProduto())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .observacoes(request.getObservacoes())
                .build();
    }

    public NutrienteResponse toResponse(Nutriente nutriente) {
        return NutrienteResponse.builder()
                .id(nutriente.getId())
                .loteId(nutriente.getLote().getId())
                .loteCodigo(nutriente.getLote().getCodigo())
                .fornecedorId(nutriente.getFornecedor() != null ? nutriente.getFornecedor().getId() : null)
                .fornecedorNome(nutriente.getFornecedor() != null ? nutriente.getFornecedor().getNome() : null)
                .dataAplicacao(nutriente.getDataAplicacao())
                .tipoNutriente(nutriente.getTipoNutriente())
                .produto(nutriente.getProduto())
                .quantidade(nutriente.getQuantidade())
                .unidade(nutriente.getUnidade())
                .custoUnitario(nutriente.getCustoUnitario())
                .custoTotal(nutriente.getCustoTotal())
                .observacoes(nutriente.getObservacoes())
                .dataCriacao(nutriente.getDataCriacao())
                .dataAtualizacao(nutriente.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Nutriente nutriente, NutrienteRequest request, Lote lote, Fornecedor fornecedor) {
        nutriente.setLote(lote);
        nutriente.setFornecedor(fornecedor);
        nutriente.setDataAplicacao(request.getDataAplicacao());
        nutriente.setTipoNutriente(request.getTipoNutriente());
        nutriente.setProduto(request.getProduto());
        nutriente.setQuantidade(request.getQuantidade());
        nutriente.setUnidade(request.getUnidade());
        nutriente.setCustoUnitario(request.getCustoUnitario());
        nutriente.setObservacoes(request.getObservacoes());
    }
}
