package com.ms.cardmanagement.mapper;

import com.ms.cardmanagement.domain.CartaoEntity;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartaoMapper {

    CartaoResponse toResponse(CartaoEntity entity);

    CartaoCriadoEvent toCriadoEvent(CartaoEntity entity);

    CartaoAtivadoEvent toAtivadoEvent(CartaoEntity entity);

}
