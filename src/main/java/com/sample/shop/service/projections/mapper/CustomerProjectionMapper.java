package com.sample.shop.service.projections.mapper;

import com.sample.shop.service.projections.dto.Customer;
import com.sample.shop.service.dto.CustomerDTO;
import com.sample.shop.service.mapper.EntityMapper;

import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerProjectionMapper extends EntityMapper<CustomerDTO, Customer> {}
