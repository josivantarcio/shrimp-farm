package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.application.service.ViveiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/viveiros")
@RequiredArgsConstructor
public class ViveiroController {

    private final ViveiroService viveiroService;

    @PostMapping
    public ResponseEntity<ViveiroResponse> criar(@Valid @RequestBody ViveiroRequest request) {
        ViveiroResponse response = viveiroService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViveiroResponse> buscarPorId(@PathVariable Long id) {
        ViveiroResponse response = viveiroService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ViveiroResponse>> listarTodos() {
        List<ViveiroResponse> response = viveiroService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fazenda/{fazendaId}")
    public ResponseEntity<List<ViveiroResponse>> listarPorFazenda(@PathVariable Long fazendaId) {
        List<ViveiroResponse> response = viveiroService.listarPorFazenda(fazendaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ViveiroResponse>> listarPaginado(Pageable pageable) {
        Page<ViveiroResponse> response = viveiroService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViveiroResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ViveiroRequest request) {
        ViveiroResponse response = viveiroService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        viveiroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        viveiroService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
