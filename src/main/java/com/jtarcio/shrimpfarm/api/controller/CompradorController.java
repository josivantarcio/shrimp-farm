package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.application.service.CompradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compradores")
@RequiredArgsConstructor
public class CompradorController {

    private final CompradorService compradorService;

    @PostMapping
    public ResponseEntity<CompradorResponse> criar(@Valid @RequestBody CompradorRequest request) {
        CompradorResponse response = compradorService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompradorResponse> buscarPorId(@PathVariable Long id) {
        CompradorResponse response = compradorService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CompradorResponse>> listarTodos() {
        List<CompradorResponse> response = compradorService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<CompradorResponse>> listarAtivos() {
        List<CompradorResponse> response = compradorService.listarAtivos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<CompradorResponse>> listarPaginado(Pageable pageable) {
        Page<CompradorResponse> response = compradorService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CompradorRequest request) {
        CompradorResponse response = compradorService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        compradorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        compradorService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
