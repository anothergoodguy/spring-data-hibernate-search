package com.sample.shop.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.sample.shop.domain.WishList;
import com.sample.shop.repository.WishListRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link WishList} entity.
 */
public interface WishListSearchRepository extends ElasticsearchRepository<WishList, UUID>, WishListSearchRepositoryInternal {}

interface WishListSearchRepositoryInternal {
    Stream<WishList> search(String query);

    Stream<WishList> search(Query query);

    void index(WishList entity);
}

class WishListSearchRepositoryInternalImpl implements WishListSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WishListRepository repository;

    WishListSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WishListRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<WishList> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<WishList> search(Query query) {
        return elasticsearchTemplate.search(query, WishList.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(WishList entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
