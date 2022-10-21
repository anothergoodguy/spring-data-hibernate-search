package com.sample.shop.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.sample.shop.domain.WishList} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WishListDTO implements Serializable {

    private UUID id;

    @NotNull
    private String title;

    private Boolean restricted;

    private CustomerDTO customer;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WishListDTO)) {
            return false;
        }

        WishListDTO wishListDTO = (WishListDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, wishListDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WishListDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", restricted='" + getRestricted() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
