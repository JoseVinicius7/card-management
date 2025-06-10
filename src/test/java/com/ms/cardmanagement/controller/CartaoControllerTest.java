package com.ms.cardmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.service.CartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartaoController.class)
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoService cartaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartaoResponse cartaoResponse;

    @BeforeEach
    void setup() {
        cartaoResponse = new CartaoResponse(
                1L,
                "12345678900",
                "Nome Impresso",
                "Produto",
                "Subproduto",
                TipoCartao.FISICO,
                SituacaoCartao.ATIVO,
                LocalDateTime.now()
        );
    }

    @Test
    void deveCriarCartoes() throws Exception {
        CriarCartaoRequest request = new CriarCartaoRequest("12345678900", "Nome Impresso", "Produto", "Subproduto");

        Mockito.when(cartaoService.criarCartoes(any())).thenReturn(List.of(cartaoResponse));

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cpf").value("12345678900"));
    }

    @Test
    void deveAtivarCartao() throws Exception {
        Mockito.when(cartaoService.ativarCartao(1L)).thenReturn(cartaoResponse);

        mockMvc.perform(post("/cartoes/1/ativar"))
                .andExpect(status().isOk());
    }

    @Test
    void deveCancelarCartao() throws Exception {
        mockMvc.perform(post("/cartoes/1/cancelar"))
                .andExpect(status().isOk());

        Mockito.verify(cartaoService).cancelarCartao(1L);
    }
}