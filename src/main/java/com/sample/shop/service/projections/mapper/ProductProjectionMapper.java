package com.sample.shop.service.projections.mapper;

import com.sample.shop.service.projections.dto.Product;
import com.sample.shop.service.projections.dto.WishList;
import com.sample.shop.service.dto.ProductDTO;
import com.sample.shop.service.dto.WishListDTO;
import com.sample.shop.service.mapper.EntityMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductProjectionMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "wishList", source = "wishList", qualifiedByName = "wishListId")
    ProductDTO toDto(Product s);

    @Named("wishListId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WishListDTO toDtoWishListId(WishList wishList);
}
