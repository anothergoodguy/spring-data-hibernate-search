package com.sample.shop.config;

import java.net.URISyntaxException;
import java.nio.file.Path;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmMappingConfigurationContext;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmSearchMappingConfigurer;
import org.hibernate.search.util.common.jar.impl.JandexUtils;
import org.springframework.stereotype.Component;

@Component("searchMappingConfigurer")
public class HibernateSearchMappingConfigurer implements HibernateOrmSearchMappingConfigurer {

    @Override
    public void configure(HibernateOrmMappingConfigurationContext context) {
        // Workaround for https://hibernate.atlassian.net/browse/HSEARCH-4724
        // => Hibernate Search doesn't seem to find the Jandex index in the fat JAR.
        try {
            var classesUri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            var ssp = classesUri.getSchemeSpecificPart();
            var jarpath = Path.of(ssp.substring(ssp.indexOf(":") + 1, ssp.indexOf("!")));
            context.annotationMapping().add(JandexUtils.readIndex(jarpath).get());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
