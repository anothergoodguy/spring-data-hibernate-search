package com.sample.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A Product.
 */
@Entity
@Audited
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed(index = "product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product-read", createIndex = false)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36)
    @KeywordField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private UUID id;

    @NotNull
    @Column(name = "title", nullable = false)
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String title;

    @Column(name = "keywords")
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String keywords;

    @Column(name = "description")
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String description;

    @Column(name = "rating")
    @GenericField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private Integer rating;

    @Column(name = "date_added")
    @GenericField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private LocalDate dateAdded;

    @Column(name = "date_modified")
    @GenericField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private LocalDate dateModified;

    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JsonIgnoreProperties(value = { "products", "customer" }, allowSetters = true)
    private WishList wishList;

    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @NotAudited
    @ManyToMany(mappedBy = "products")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    //@org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = {"parent", "products"}, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product id(UUID id) {
        this.setId(id);
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Product title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Product keywords(String keywords) {
        this.setKeywords(keywords);
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Product rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public Product dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Product dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public WishList getWishList() {
        return this.wishList;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public Product wishList(WishList wishList) {
        this.setWishList(wishList);
        return this;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        if (this.categories != null) {
            this.categories.forEach(i -> i.removeProduct(this));
        }
        if (categories != null) {
            categories.forEach(i -> i.addProduct(this));
        }
        this.categories = categories;
    }

    public Product categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Product addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
        return this;
    }

    public Product removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", keywords='" + getKeywords() + "'" +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
