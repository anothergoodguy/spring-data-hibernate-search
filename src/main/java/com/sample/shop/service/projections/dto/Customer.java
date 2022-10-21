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
public record Customer(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String telephone,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    List<WishList> wishLists,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    List<Address> addresses
) {
}
