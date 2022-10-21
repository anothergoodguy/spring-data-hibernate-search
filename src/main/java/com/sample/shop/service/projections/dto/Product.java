package com.sample.shop.service.projections.dto;

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
public record Product(
    UUID id,
    String title,
    String keywords,
    String description,
    Integer rating,
    LocalDate dateAdded,
    LocalDate dateModified,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    WishList wishList,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    List<Category> categories
) {
}
