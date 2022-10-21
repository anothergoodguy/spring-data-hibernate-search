package com.sample.shop.service.projections.dto;

import com.sample.shop.domain.enumeration.CategoryStatus;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ProjectionConstructor
public record Category(

    UUID id,
    String description,
    Integer sortOrder,
    LocalDate dateAdded,
    LocalDate dateModified,
    CategoryStatus status,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    Category parent,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    List<Product> products
) {}
