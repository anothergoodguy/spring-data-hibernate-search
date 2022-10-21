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

package com.sample.shop.web.rest;



import com.sample.shop.security.AuthoritiesConstants;
import com.sample.shop.security.SecurityUtils;
import com.sample.shop.service.MassIndexService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing Elasticsearch index.
 */
@RestController
@RequestMapping("/api")
@Tag(name = " Elastic Search Mass Indexing API", description = "Rest API to Initiate Elastic Search Mass Indexing")
public class MassIndexResource {

    private final Logger log = LoggerFactory.getLogger(MassIndexResource.class);

    private final MassIndexService elasticsearchIndexService;

    public MassIndexResource(MassIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    /**
     * POST /elasticsearch/index -> Reindex all Elasticsearch documents
     */
    @PostMapping("/mass/index")
    @Operation(summary = "reIndex All Entries")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "OK",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Void.class)))
            ),
            @ApiResponse(responseCode = "404", description = "The resource not found"),
        }
    )
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> reindexAll() throws URISyntaxException {
        log.info("REST request to reindex Elasticsearch by user : {}", SecurityUtils.getCurrentUserLogin());
        elasticsearchIndexService.reindexAll();
        return ResponseEntity.accepted().headers(HeaderUtil.createAlert("elasticsearch.reindex.accepted", null,"asdf")).build();
    }


}
