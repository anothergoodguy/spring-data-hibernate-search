package com.sample.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A Address.
 */
@Entity
@Audited
@Table(name = "address")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed(index = "address")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "address-read", createIndex = false)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36)
    @KeywordField(projectable = Projectable.YES, searchable = Searchable.YES, sortable = Sortable.YES)
    private UUID id;

    @Column(name = "address_1")
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String address1;

    @Column(name = "address_2")
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String address2;

    @Column(name = "city")
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String city;

    @NotNull
    @Size(max = 10)
    @Column(name = "postcode", length = 10, nullable = false)
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String postcode;

    @NotNull
    @Size(max = 2)
    @Column(name = "country", length = 2, nullable = false)
    @FullTextField(projectable = Projectable.YES, searchable = Searchable.YES, analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String country;

    @IndexedEmbedded(includeDepth = 1, structure = ObjectStructure.NESTED)
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

    public Address id(UUID id) {
        this.setId(id);
        return this;
    }

    public String getAddress1() {
        return this.address1;
    }

    public Address address1(String address1) {
        this.setAddress1(address1);
        return this;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public Address address2(String address2) {
        this.setAddress2(address2);
        return this;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return this.city;
    }

    public Address city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public Address postcode(String postcode) {
        this.setPostcode(postcode);
        return this;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return this.country;
    }

    public Address country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        return id != null && id.equals(((Address) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Address{" +
            "id=" + getId() +
            ", address1='" + getAddress1() + "'" +
            ", address2='" + getAddress2() + "'" +
            ", city='" + getCity() + "'" +
            ", postcode='" + getPostcode() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
