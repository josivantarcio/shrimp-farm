package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.application.service.DespescaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/despescas")
@RequiredArgsConstructor
public class DespescaController {

    private final DespescaService despescaService;

    @PostMapping
    public ResponseEntity<DespescaResponse> criar(@Valid @RequestBody DespescaRequest request) {
        DespescaResponse response = despescaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespescaResponse> buscarPorId(@PathVariable Long id) {
        DespescaResponse response = despescaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lote/{loteId}")
    public ResponseEntity<DespescaResponse> buscarPorLote(@PathVariable Long loteId) {
        DespescaResponse response = despescaService.buscarPorLote(loteId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespescaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DespescaRequest request) {
        DespescaResponse response = despescaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        despescaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
