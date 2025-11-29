package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.application.service.RacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/racoes")
@RequiredArgsConstructor
public class RacaoController {

    private final RacaoService racaoService;

    @PostMapping
    public ResponseEntity<RacaoResponse> criar(@Valid @RequestBody RacaoRequest request) {
        RacaoResponse response = racaoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RacaoResponse> buscarPorId(@PathVariable Long id) {
        RacaoResponse response = racaoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<RacaoResponse>> listarPorLote(@PathVariable Long loteId) {
        List<RacaoResponse> response = racaoService.listarPorLote(loteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lote/{loteId}/total")
    public ResponseEntity<BigDecimal> calcularTotalPorLote(@PathVariable Long loteId) {
        BigDecimal total = racaoService.calcularTotalPorLote(loteId);
        return ResponseEntity.ok(total);
    }

    @GetMapping
    public ResponseEntity<Page<RacaoResponse>> listarPaginado(Pageable pageable) {
        Page<RacaoResponse> response = racaoService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RacaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RacaoRequest request) {
        RacaoResponse response = racaoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        racaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
