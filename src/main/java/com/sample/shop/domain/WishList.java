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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A WishList.
 */
@Entity
@Audited
@Table(name = "wish_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed(index = "wishlist")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "wishlist-read", createIndex = false)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishList implements Serializable {

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

    @Column(name = "restricted")
    @GenericField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private Boolean restricted;

    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @NotAudited
    @OneToMany(mappedBy = "wishList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    //@org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = {"wishList", "categories"}, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JsonIgnoreProperties(value = {"wishLists", "addresses"}, allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WishList id(UUID id) {
        this.setId(id);
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public WishList title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRestricted() {
        return this.restricted;
    }

    public WishList restricted(Boolean restricted) {
        this.setRestricted(restricted);
        return this;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setWishList(null));
        }
        if (products != null) {
            products.forEach(i -> i.setWishList(this));
        }
        this.products = products;
    }

    public WishList products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public WishList addProduct(Product product) {
        this.products.add(product);
        product.setWishList(this);
        return this;
    }

    public WishList removeProduct(Product product) {
        this.products.remove(product);
        product.setWishList(null);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public WishList customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WishList)) {
            return false;
        }
        return id != null && id.equals(((WishList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishList{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", restricted='" + getRestricted() + "'" +
            "}";
    }
}
