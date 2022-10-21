package com.sample.shop.service.mapper;

import com.sample.shop.domain.Address;
import com.sample.shop.domain.Customer;
import com.sample.shop.service.dto.AddressDTO;
import com.sample.shop.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    AddressDTO toDto(Address s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
