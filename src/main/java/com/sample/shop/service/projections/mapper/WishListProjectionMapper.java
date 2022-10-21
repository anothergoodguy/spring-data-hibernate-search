package com.sample.shop.service.projections.mapper;

import com.sample.shop.service.projections.dto.Customer;
import com.sample.shop.service.projections.dto.WishList;
import com.sample.shop.service.dto.CustomerDTO;
import com.sample.shop.service.dto.WishListDTO;
import com.sample.shop.service.mapper.EntityMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link WishList} and its DTO {@link WishListDTO}.
 */
@Mapper(componentModel = "spring")
public interface WishListProjectionMapper extends EntityMapper<WishListDTO, WishList> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    WishListDTO toDto(WishList s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
