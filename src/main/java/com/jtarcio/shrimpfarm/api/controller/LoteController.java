package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.application.service.LoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @PostMapping
    public ResponseEntity<LoteResponse> criar(@Valid @RequestBody LoteRequest request) {
        LoteResponse response = loteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> buscarPorId(@PathVariable Long id) {
        LoteResponse response = loteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LoteResponse>> listarTodos() {
        List<LoteResponse> response = loteService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<LoteResponse>> listarAtivos() {
        List<LoteResponse> response = loteService.listarAtivos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viveiro/{viveiroId}")
    public ResponseEntity<List<LoteResponse>> listarPorViveiro(@PathVariable Long viveiroId) {
        List<LoteResponse> response = loteService.listarPorViveiro(viveiroId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<LoteResponse>> listarPaginado(Pageable pageable) {
        Page<LoteResponse> response = loteService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LoteRequest request) {
        LoteResponse response = loteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        loteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
