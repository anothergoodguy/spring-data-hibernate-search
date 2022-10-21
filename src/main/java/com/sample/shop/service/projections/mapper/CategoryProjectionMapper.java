package com.sample.shop.service.projections.mapper;

import com.sample.shop.service.projections.dto.Category;
import com.sample.shop.service.projections.dto.Product;
import com.sample.shop.service.dto.CategoryDTO;
import com.sample.shop.service.dto.ProductDTO;
import com.sample.shop.service.mapper.EntityMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryProjectionMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "categoryId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productTitleSet")
    CategoryDTO toDto(Category s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("productTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ProductDTO toDtoProductTitle(Product product);

    @Named("productTitleSet")
    default Set<ProductDTO> toDtoProductTitleSet(List<Product> product) {
        return product.stream().map(this::toDtoProductTitle).collect(Collectors.toSet());
    }
}
