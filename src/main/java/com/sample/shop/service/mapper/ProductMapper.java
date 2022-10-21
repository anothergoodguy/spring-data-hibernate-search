package com.sample.shop.service.mapper;

import com.sample.shop.domain.Product;
import com.sample.shop.domain.WishList;
import com.sample.shop.service.dto.ProductDTO;
import com.sample.shop.service.dto.WishListDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "wishList", source = "wishList", qualifiedByName = "wishListId")
    ProductDTO toDto(Product s);

    @Named("wishListId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WishListDTO toDtoWishListId(WishList wishList);
}
