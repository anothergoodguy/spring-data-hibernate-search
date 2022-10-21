package com.sample.shop.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sample.shop.domain.WishList} entity. This class is used
 * in {@link com.sample.shop.web.rest.WishListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /wish-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter title;

    private BooleanFilter restricted;

    private UUIDFilter productId;

    private UUIDFilter customerId;

    private Boolean distinct;

    public WishListCriteria() {}

    public WishListCriteria(WishListCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.restricted = other.restricted == null ? null : other.restricted.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WishListCriteria copy() {
        return new WishListCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public UUIDFilter id() {
        if (id == null) {
            id = new UUIDFilter();
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public BooleanFilter getRestricted() {
        return restricted;
    }

    public BooleanFilter restricted() {
        if (restricted == null) {
            restricted = new BooleanFilter();
        }
        return restricted;
    }

    public void setRestricted(BooleanFilter restricted) {
        this.restricted = restricted;
    }

    public UUIDFilter getProductId() {
        return productId;
    }

    public UUIDFilter productId() {
        if (productId == null) {
            productId = new UUIDFilter();
        }
        return productId;
    }

    public void setProductId(UUIDFilter productId) {
        this.productId = productId;
    }

    public UUIDFilter getCustomerId() {
        return customerId;
    }

    public UUIDFilter customerId() {
        if (customerId == null) {
            customerId = new UUIDFilter();
        }
        return customerId;
    }

    public void setCustomerId(UUIDFilter customerId) {
        this.customerId = customerId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WishListCriteria that = (WishListCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(restricted, that.restricted) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, restricted, productId, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishListCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (restricted != null ? "restricted=" + restricted + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (customerId != null ? "customerId=" + customerId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
