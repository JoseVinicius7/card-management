package com.ms.cardmanagement.controller;

import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.service.CartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<List<CartaoResponse>> criarCartoes(@RequestBody CriarCartaoRequest request) {
        List<CartaoResponse> cartoesCriados = cartaoService.criarCartoes(request);
        return ResponseEntity.ok(cartoesCriados);
    }

    @PostMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarCartao(@PathVariable Long id) {
        cartaoService.ativarCartao(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCartao(@PathVariable Long id) {
        cartaoService.cancelarCartao(id);
        return ResponseEntity.ok().build();
    }
}
