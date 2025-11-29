package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.application.service.FazendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fazendas")
@RequiredArgsConstructor
public class FazendaController {

    private final FazendaService fazendaService;

    @PostMapping
    public ResponseEntity<FazendaResponse> criar(@Valid @RequestBody FazendaRequest request) {
        FazendaResponse response = fazendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FazendaResponse> buscarPorId(@PathVariable Long id) {
        FazendaResponse response = fazendaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FazendaResponse>> listarTodas() {
        List<FazendaResponse> response = fazendaService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<FazendaResponse>> listarAtivas() {
        List<FazendaResponse> response = fazendaService.listarAtivas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<FazendaResponse>> listarPaginado(Pageable pageable) {
        Page<FazendaResponse> response = fazendaService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FazendaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FazendaRequest request) {
        FazendaResponse response = fazendaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fazendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        fazendaService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
