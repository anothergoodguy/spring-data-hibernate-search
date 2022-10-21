package com.sample.shop.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sample.shop.domain.Product} entity. This class is used
 * in {@link com.sample.shop.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter title;

    private StringFilter keywords;

    private StringFilter description;

    private IntegerFilter rating;

    private LocalDateFilter dateAdded;

    private LocalDateFilter dateModified;

    private UUIDFilter wishListId;

    private UUIDFilter categoryId;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.keywords = other.keywords == null ? null : other.keywords.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.rating = other.rating == null ? null : other.rating.copy();
        this.dateAdded = other.dateAdded == null ? null : other.dateAdded.copy();
        this.dateModified = other.dateModified == null ? null : other.dateModified.copy();
        this.wishListId = other.wishListId == null ? null : other.wishListId.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
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

    public StringFilter getKeywords() {
        return keywords;
    }

    public StringFilter keywords() {
        if (keywords == null) {
            keywords = new StringFilter();
        }
        return keywords;
    }

    public void setKeywords(StringFilter keywords) {
        this.keywords = keywords;
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

    public IntegerFilter getRating() {
        return rating;
    }

    public IntegerFilter rating() {
        if (rating == null) {
            rating = new IntegerFilter();
        }
        return rating;
    }

    public void setRating(IntegerFilter rating) {
        this.rating = rating;
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

    public UUIDFilter getWishListId() {
        return wishListId;
    }

    public UUIDFilter wishListId() {
        if (wishListId == null) {
            wishListId = new UUIDFilter();
        }
        return wishListId;
    }

    public void setWishListId(UUIDFilter wishListId) {
        this.wishListId = wishListId;
    }

    public UUIDFilter getCategoryId() {
        return categoryId;
    }

    public UUIDFilter categoryId() {
        if (categoryId == null) {
            categoryId = new UUIDFilter();
        }
        return categoryId;
    }

    public void setCategoryId(UUIDFilter categoryId) {
        this.categoryId = categoryId;
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
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(keywords, that.keywords) &&
            Objects.equals(description, that.description) &&
            Objects.equals(rating, that.rating) &&
            Objects.equals(dateAdded, that.dateAdded) &&
            Objects.equals(dateModified, that.dateModified) &&
            Objects.equals(wishListId, that.wishListId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, keywords, description, rating, dateAdded, dateModified, wishListId, categoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (keywords != null ? "keywords=" + keywords + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (rating != null ? "rating=" + rating + ", " : "") +
            (dateAdded != null ? "dateAdded=" + dateAdded + ", " : "") +
            (dateModified != null ? "dateModified=" + dateModified + ", " : "") +
            (wishListId != null ? "wishListId=" + wishListId + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
