package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
import com.jtarcio.shrimpfarm.application.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final RelatorioService relatorioService;

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKPIsResponse> obterKPIs() {
        DashboardKPIsResponse response = relatorioService.obterKPIsDashboard();
        return ResponseEntity.ok(response);
    }
}
