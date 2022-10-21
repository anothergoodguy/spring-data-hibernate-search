package com.sample.shop.service.projections.mapper;

import com.sample.shop.service.projections.dto.Address;
import com.sample.shop.service.projections.dto.Customer;
import com.sample.shop.service.dto.AddressDTO;
import com.sample.shop.service.dto.CustomerDTO;
import com.sample.shop.service.mapper.EntityMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressProjectionMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    AddressDTO toDto(Address s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
