package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.application.service.BiometriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/biometrias")
@RequiredArgsConstructor
public class BiometriaController {

    private final BiometriaService biometriaService;

    @PostMapping
    public ResponseEntity<BiometriaResponse> criar(@Valid @RequestBody BiometriaRequest request) {
        BiometriaResponse response = biometriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BiometriaResponse> buscarPorId(@PathVariable Long id) {
        BiometriaResponse response = biometriaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<BiometriaResponse>> listarPorLote(@PathVariable Long loteId) {
        List<BiometriaResponse> response = biometriaService.listarPorLote(loteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lote/{loteId}/ultima")
    public ResponseEntity<BiometriaResponse> buscarUltimaBiometriaDoLote(@PathVariable Long loteId) {
        BiometriaResponse response = biometriaService.buscarUltimaBiometriaDoLote(loteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BiometriaResponse>> listarPaginado(Pageable pageable) {
        Page<BiometriaResponse> response = biometriaService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BiometriaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BiometriaRequest request) {
        BiometriaResponse response = biometriaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        biometriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
