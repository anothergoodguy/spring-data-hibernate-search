package com.sample.shop.service.mapper;

import com.sample.shop.domain.Customer;
import com.sample.shop.domain.WishList;
import com.sample.shop.service.dto.CustomerDTO;
import com.sample.shop.service.dto.WishListDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WishList} and its DTO {@link WishListDTO}.
 */
@Mapper(componentModel = "spring")
public interface WishListMapper extends EntityMapper<WishListDTO, WishList> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    WishListDTO toDto(WishList s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
