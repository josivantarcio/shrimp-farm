package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Racao;
import org.springframework.stereotype.Component;

@Component
public class RacaoMapper {

    public Racao toEntity(RacaoRequest request, Lote lote, Fornecedor fornecedor) {
        return Racao.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .tipoRacao(request.getTipoRacao())
                .marca(request.getMarca())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .proteinaPercentual(request.getProteinaPercentual())
                .observacoes(request.getObservacoes())
                .build();
    }

    public RacaoResponse toResponse(Racao racao) {
        return RacaoResponse.builder()
                .id(racao.getId())
                .loteId(racao.getLote().getId())
                .loteCodigo(racao.getLote().getCodigo())
                .fornecedorId(racao.getFornecedor() != null ? racao.getFornecedor().getId() : null)
                .fornecedorNome(racao.getFornecedor() != null ? racao.getFornecedor().getNome() : null)
                .dataAplicacao(racao.getDataAplicacao())
                .tipoRacao(racao.getTipoRacao())
                .marca(racao.getMarca())
                .quantidade(racao.getQuantidade())
                .unidade(racao.getUnidade())
                .custoUnitario(racao.getCustoUnitario())
                .custoTotal(racao.getCustoTotal())
                .proteinaPercentual(racao.getProteinaPercentual())
                .observacoes(racao.getObservacoes())
                .dataCriacao(racao.getDataCriacao())
                .dataAtualizacao(racao.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Racao racao, RacaoRequest request, Lote lote, Fornecedor fornecedor) {
        racao.setLote(lote);
        racao.setFornecedor(fornecedor);
        racao.setDataAplicacao(request.getDataAplicacao());
        racao.setTipoRacao(request.getTipoRacao());
        racao.setMarca(request.getMarca());
        racao.setQuantidade(request.getQuantidade());
        racao.setUnidade(request.getUnidade());
        racao.setCustoUnitario(request.getCustoUnitario());
        racao.setProteinaPercentual(request.getProteinaPercentual());
        racao.setObservacoes(request.getObservacoes());
    }
}
