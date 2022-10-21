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

package com.sample.shop.service;

import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;

public interface MassIndexService {
    @Async
    void reindexAll();
}
