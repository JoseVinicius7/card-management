package com.ms.cardmanagement.repository;


import com.ms.cardmanagement.domain.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
