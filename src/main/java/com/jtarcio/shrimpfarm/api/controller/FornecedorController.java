package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.application.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
        FornecedorResponse response = fornecedorService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
        FornecedorResponse response = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listarTodos() {
        List<FornecedorResponse> response = fornecedorService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<FornecedorResponse>> listarAtivos() {
        List<FornecedorResponse> response = fornecedorService.listarAtivos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<FornecedorResponse>> listarPaginado(Pageable pageable) {
        Page<FornecedorResponse> response = fornecedorService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequest request) {
        FornecedorResponse response = fornecedorService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        fornecedorService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
