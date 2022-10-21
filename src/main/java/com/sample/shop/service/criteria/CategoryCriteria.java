package com.sample.shop.service.criteria;

import com.sample.shop.domain.enumeration.CategoryStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sample.shop.domain.Category} entity. This class is used
 * in {@link com.sample.shop.web.rest.CategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CategoryStatus
     */
    public static class CategoryStatusFilter extends Filter<CategoryStatus> {

        public CategoryStatusFilter() {}

        public CategoryStatusFilter(CategoryStatusFilter filter) {
            super(filter);
        }

        @Override
        public CategoryStatusFilter copy() {
            return new CategoryStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter description;

    private IntegerFilter sortOrder;

    private LocalDateFilter dateAdded;

    private LocalDateFilter dateModified;

    private CategoryStatusFilter status;

    private UUIDFilter parentId;

    private UUIDFilter productId;

    private Boolean distinct;

    public CategoryCriteria() {}

    public CategoryCriteria(CategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.sortOrder = other.sortOrder == null ? null : other.sortOrder.copy();
        this.dateAdded = other.dateAdded == null ? null : other.dateAdded.copy();
        this.dateModified = other.dateModified == null ? null : other.dateModified.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CategoryCriteria copy() {
        return new CategoryCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getSortOrder() {
        return sortOrder;
    }

    public IntegerFilter sortOrder() {
        if (sortOrder == null) {
            sortOrder = new IntegerFilter();
        }
        return sortOrder;
    }

    public void setSortOrder(IntegerFilter sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateFilter getDateAdded() {
        return dateAdded;
    }

    public LocalDateFilter dateAdded() {
        if (dateAdded == null) {
            dateAdded = new LocalDateFilter();
        }
        return dateAdded;
    }

    public void setDateAdded(LocalDateFilter dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDateFilter getDateModified() {
        return dateModified;
    }

    public LocalDateFilter dateModified() {
        if (dateModified == null) {
            dateModified = new LocalDateFilter();
        }
        return dateModified;
    }

    public void setDateModified(LocalDateFilter dateModified) {
        this.dateModified = dateModified;
    }

    public CategoryStatusFilter getStatus() {
        return status;
    }

    public CategoryStatusFilter status() {
        if (status == null) {
            status = new CategoryStatusFilter();
        }
        return status;
    }

    public void setStatus(CategoryStatusFilter status) {
        this.status = status;
    }

    public UUIDFilter getParentId() {
        return parentId;
    }

    public UUIDFilter parentId() {
        if (parentId == null) {
            parentId = new UUIDFilter();
        }
        return parentId;
    }

    public void setParentId(UUIDFilter parentId) {
        this.parentId = parentId;
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
        final CategoryCriteria that = (CategoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(dateAdded, that.dateAdded) &&
            Objects.equals(dateModified, that.dateModified) &&
            Objects.equals(status, that.status) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, sortOrder, dateAdded, dateModified, status, parentId, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (sortOrder != null ? "sortOrder=" + sortOrder + ", " : "") +
            (dateAdded != null ? "dateAdded=" + dateAdded + ", " : "") +
            (dateModified != null ? "dateModified=" + dateModified + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (parentId != null ? "parentId=" + parentId + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
