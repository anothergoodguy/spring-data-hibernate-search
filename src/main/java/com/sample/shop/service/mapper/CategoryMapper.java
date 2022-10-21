package com.sample.shop.service.mapper;

import com.sample.shop.domain.Category;
import com.sample.shop.domain.Product;
import com.sample.shop.service.dto.CategoryDTO;
import com.sample.shop.service.dto.ProductDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "categoryId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productTitleSet")
    CategoryDTO toDto(Category s);

    @Mapping(target = "removeProduct", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

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
    default Set<ProductDTO> toDtoProductTitleSet(Set<Product> product) {
        return product.stream().map(this::toDtoProductTitle).collect(Collectors.toSet());
    }
}
