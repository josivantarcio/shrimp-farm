package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FertilizacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FertilizacaoResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.springframework.stereotype.Component;

@Component
public class FertilizacaoMapper {

    public Fertilizacao toEntity(FertilizacaoRequest request, Lote lote, Fornecedor fornecedor) {
        return Fertilizacao.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .produto(request.getProduto())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .finalidade(request.getFinalidade())
                .observacoes(request.getObservacoes())
                .build();
    }

    public FertilizacaoResponse toResponse(Fertilizacao fertilizacao) {
        return FertilizacaoResponse.builder()
                .id(fertilizacao.getId())
                .loteId(fertilizacao.getLote().getId())
                .loteCodigo(fertilizacao.getLote().getCodigo())
                .fornecedorId(fertilizacao.getFornecedor() != null ? fertilizacao.getFornecedor().getId() : null)
                .fornecedorNome(fertilizacao.getFornecedor() != null ? fertilizacao.getFornecedor().getNome() : null)
                .dataAplicacao(fertilizacao.getDataAplicacao())
                .produto(fertilizacao.getProduto())
                .quantidade(fertilizacao.getQuantidade())
                .unidade(fertilizacao.getUnidade())
                .custoUnitario(fertilizacao.getCustoUnitario())
                .custoTotal(fertilizacao.getCustoTotal())
                .finalidade(fertilizacao.getFinalidade())
                .observacoes(fertilizacao.getObservacoes())
                .dataCriacao(fertilizacao.getDataCriacao())
                .dataAtualizacao(fertilizacao.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Fertilizacao fertilizacao, FertilizacaoRequest request, Lote lote, Fornecedor fornecedor) {
        fertilizacao.setLote(lote);
        fertilizacao.setFornecedor(fornecedor);
        fertilizacao.setDataAplicacao(request.getDataAplicacao());
        fertilizacao.setProduto(request.getProduto());
        fertilizacao.setQuantidade(request.getQuantidade());
        fertilizacao.setUnidade(request.getUnidade());
        fertilizacao.setCustoUnitario(request.getCustoUnitario());
        fertilizacao.setFinalidade(request.getFinalidade());
        fertilizacao.setObservacoes(request.getObservacoes());
    }
}
