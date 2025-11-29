package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class BiometriaMapper {

    public Biometria toEntity(BiometriaRequest request, Lote lote) {
        Integer diaCultivo = (int) ChronoUnit.DAYS.between(
                lote.getDataPovoamento(),
                request.getDataBiometria()
        );

        return Biometria.builder()
                .lote(lote)
                .dataBiometria(request.getDataBiometria())
                .diaCultivo(diaCultivo)
                .pesoMedio(request.getPesoMedio())
                .quantidadeAmostrada(request.getQuantidadeAmostrada())
                .pesoTotalAmostra(request.getPesoTotalAmostra())
                .observacoes(request.getObservacoes())
                .build();
    }

    public BiometriaResponse toResponse(Biometria biometria) {
        return BiometriaResponse.builder()
                .id(biometria.getId())
                .loteId(biometria.getLote().getId())
                .loteCodigo(biometria.getLote().getCodigo())
                .dataBiometria(biometria.getDataBiometria())
                .diaCultivo(biometria.getDiaCultivo())
                .pesoMedio(biometria.getPesoMedio())
                .quantidadeAmostrada(biometria.getQuantidadeAmostrada())
                .pesoTotalAmostra(biometria.getPesoTotalAmostra())
                .ganhoPesoDiario(biometria.getGanhoPesoDiario())
                .biomassaEstimada(biometria.getBiomassaEstimada())
                .sobrevivenciaEstimada(biometria.getSobrevivenciaEstimada())
                .fatorConversaoAlimentar(biometria.getFatorConversaoAlimentar())
                .observacoes(biometria.getObservacoes())
                .dataCriacao(biometria.getDataCriacao())
                .dataAtualizacao(biometria.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Biometria biometria, BiometriaRequest request, Lote lote) {
        Integer diaCultivo = (int) ChronoUnit.DAYS.between(
                lote.getDataPovoamento(),
                request.getDataBiometria()
        );

        biometria.setLote(lote);
        biometria.setDataBiometria(request.getDataBiometria());
        biometria.setDiaCultivo(diaCultivo);
        biometria.setPesoMedio(request.getPesoMedio());
        biometria.setQuantidadeAmostrada(request.getQuantidadeAmostrada());
        biometria.setPesoTotalAmostra(request.getPesoTotalAmostra());
        biometria.setObservacoes(request.getObservacoes());
    }
}
