package com.sample.shop.service.projections.dto;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.AssociationInverseSide;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue;

import java.util.UUID;

/**
 * A Address.
 */
@ProjectionConstructor
public record Address(
    UUID id,
    String address1,
    String address2,
    String city,
    String postcode,
    String country,
    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    Customer customer
) {

}
