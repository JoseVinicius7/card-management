package com.ms.cardmanagement.service;

import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.dto.CartaoResponse;

import java.util.List;

public interface CartaoService {
    List<CartaoResponse> criarCartoes(CriarCartaoRequest request);
}
