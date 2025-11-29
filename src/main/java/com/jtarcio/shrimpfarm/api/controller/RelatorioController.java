package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
import com.jtarcio.shrimpfarm.application.dto.response.RelatorioCustoLoteResponse;
import com.jtarcio.shrimpfarm.application.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardKPIsResponse> obterDashboardKPIs() {
        DashboardKPIsResponse response = relatorioService.obterKPIsDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lotes/{loteId}/custos")
    public ResponseEntity<RelatorioCustoLoteResponse> relatorioCustoLote(@PathVariable Long loteId) {
        RelatorioCustoLoteResponse response = relatorioService.gerarRelatorioCustoLote(loteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lotes/ativos/custos")
    public ResponseEntity<List<RelatorioCustoLoteResponse>> relatoriosLotesAtivos() {
        List<RelatorioCustoLoteResponse> response = relatorioService.listarRelatoriosLotesAtivos();
        return ResponseEntity.ok(response);
    }
}
