package com.ms.cardmanagement.repository;


import com.ms.cardmanagement.domain.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {
}
