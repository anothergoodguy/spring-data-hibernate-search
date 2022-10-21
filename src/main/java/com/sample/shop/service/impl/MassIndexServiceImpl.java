/*
 *
 * Copyright (c) 2022. TinyMedic Pvt Ltd  - All Rights Reserved.
 * This file is part of the API module  of the project: TinyMedic-Platform.
 *
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Any resources of   TinyMedic-Platform can not be copied and/or distributed without the express permission of TinyMedic Pvt Ltd.
 *
 */

package com.sample.shop.service.impl;

import com.sample.shop.service.MassIndexService;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.core.TimeValue;
import org.hibernate.search.backend.elasticsearch.index.ElasticsearchIndexManager;
import org.hibernate.search.backend.elasticsearch.metamodel.ElasticsearchIndexDescriptor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.pojo.massindexing.MassIndexingMonitor;
import org.hibernate.search.mapper.pojo.massindexing.impl.PojoMassIndexingLoggingMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MassIndexServiceImpl implements MassIndexService {

    private final Logger log = LoggerFactory.getLogger(MassIndexServiceImpl.class);

    private final EntityManager entityManager;

    private final MassIndexingMonitor monitor;

    private final RestHighLevelClient mClient;

    public MassIndexServiceImpl(EntityManager entityManager, RestHighLevelClient client) {
        this.entityManager = entityManager;
        mClient = client;
        monitor = new PojoMassIndexingLoggingMonitor(1000);
    }

    @Async
    @Override
    public void reindexAll() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SearchSession searchSession = Search.session(entityManager);
        searchSession
            .massIndexer()
            .batchSizeToLoadObjects(500)
            .threadsToLoadObjects(2)
            .typesToIndexInParallel(1)
            .monitor(monitor)
            .start()
            .thenRun(() -> {
                stopWatch.stop();
                log.info("Mass indexing succeeded! in {}", stopWatch.formatTime());
            })
            .exceptionally(throwable -> {
                stopWatch.stop();
                log.error("Mass indexing failed! in {}", stopWatch.formatTime(), throwable);
                return null;
            });
    }
}
