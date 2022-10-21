package com.sample.shop.service.projections.dto;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@ProjectionConstructor
public record WishList(
    UUID id,
    String title,
    Boolean restricted,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    List<Product> products,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    Customer customer
) {}
