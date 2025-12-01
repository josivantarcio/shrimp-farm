package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FornecedorMapperTest {

    private final FornecedorMapper mapper = new FornecedorMapper();

    private FornecedorRequest criarRequest() {
        return FornecedorRequest.builder()
                .nome("Fornecedor A")
                .cnpj("12.345.678/0001-90")
                .email("fornecedorA@teste.com")
                .telefone("(11) 99999-0000")
                .endereco("Rua 1, 100")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01000-000")
                .observacoes("Obs teste fornecedor")
                .build();
    }

    private Fornecedor criarEntity() {
        return Fornecedor.builder()
                .id(10L)
                .nome("Fornecedor A")
                .cnpj("12.345.678/0001-90")
                .email("fornecedorA@teste.com")
                .telefone("(11) 99999-0000")
                .endereco("Rua 1, 100")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01000-000")
                .observacoes("Obs teste fornecedor")
                .build();
    }

    @Test
    @DisplayName("Deve converter FornecedorRequest para Fornecedor")
    void deveConverterRequestParaEntity() {
        FornecedorRequest request = criarRequest();

        Fornecedor entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getCnpj()).isEqualTo(request.getCnpj());
        assertThat(entity.getEmail()).isEqualTo(request.getEmail());
        assertThat(entity.getTelefone()).isEqualTo(request.getTelefone());
        assertThat(entity.getEndereco()).isEqualTo(request.getEndereco());
        assertThat(entity.getCidade()).isEqualTo(request.getCidade());
        assertThat(entity.getEstado()).isEqualTo(request.getEstado());
        assertThat(entity.getCep()).isEqualTo(request.getCep());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Fornecedor existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Fornecedor entity = criarEntity();
        FornecedorRequest request = FornecedorRequest.builder()
                .nome("Fornecedor B")
                .cnpj("98.765.432/0001-10")
                .email("fornecedorB@teste.com")
                .telefone("(21) 98888-0000")
                .endereco("Avenida 2, 200")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .cep("20000-000")
                .observacoes("Obs atualizada fornecedor")
                .build();

        mapper.updateEntity(entity, request);

        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getCnpj()).isEqualTo(request.getCnpj());
        assertThat(entity.getEmail()).isEqualTo(request.getEmail());
        assertThat(entity.getTelefone()).isEqualTo(request.getTelefone());
        assertThat(entity.getEndereco()).isEqualTo(request.getEndereco());
        assertThat(entity.getCidade()).isEqualTo(request.getCidade());
        assertThat(entity.getEstado()).isEqualTo(request.getEstado());
        assertThat(entity.getCep()).isEqualTo(request.getCep());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Fornecedor para FornecedorResponse")
    void deveConverterEntityParaResponse() {
        Fornecedor entity = criarEntity();

        FornecedorResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getNome()).isEqualTo(entity.getNome());
        assertThat(response.getCnpj()).isEqualTo(entity.getCnpj());
        assertThat(response.getEmail()).isEqualTo(entity.getEmail());
        assertThat(response.getTelefone()).isEqualTo(entity.getTelefone());
        assertThat(response.getEndereco()).isEqualTo(entity.getEndereco());
        assertThat(response.getCidade()).isEqualTo(entity.getCidade());
        assertThat(response.getEstado()).isEqualTo(entity.getEstado());
        assertThat(response.getCep()).isEqualTo(entity.getCep());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
    }
}
