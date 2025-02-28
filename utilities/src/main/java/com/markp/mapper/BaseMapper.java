package com.markp.mapper;

import com.markp.dto.BaseDto;
import com.markp.model.BaseEntity;
import org.mapstruct.MappingTarget;

public class BaseMapper {

    public static void mapToBaseDto(BaseEntity entity, BaseDto dto) {
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
    }

    public static void mapToBaseEntity(BaseDto dto, BaseEntity entity) {
        entity.setId(dto.getId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    public static void updateEntityFromDto(BaseDto dto, @MappingTarget BaseEntity entity) {
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }
}