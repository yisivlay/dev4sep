/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.infrastructure.core.config.jpa;

import com.base.infrastructure.core.boot.datasource.RoutingDataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * {@code @author:} YISivlay
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.base.**.domain"})
@EnableConfigurationProperties(JpaProperties.class)
public class JpaConfig extends JpaBaseConfiguration {

    private final Collection<EntityManagerFactoryCustomizer> emFactoryCustomizers;

    @Autowired
    public JpaConfig(RoutingDataSource dataSource,
                     JpaProperties properties,
                     ObjectProvider<JtaTransactionManager> jtaTransactionManager,
                     Collection<EntityManagerFactoryCustomizer> emFactoryCustomizers) {
        super(dataSource, properties, jtaTransactionManager);
        this.emFactoryCustomizers = emFactoryCustomizers;
    }

    @Override
    @Bean
    @Primary
    @DependsOn("flywayDataMigrationService")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
        var vendorProperties = getVendorProperties();
        var packagesToScan = getPackagesToScan();
        return factoryBuilder
                .dataSource(getDataSource())
                .properties(vendorProperties)
                .persistenceUnit("jpa-pu")
                .packages(packagesToScan)
                .jta(false).build();
    }

    protected String[] getPackagesToScan() {
        var packagesToScan = new HashSet<>();
        packagesToScan.add("com.base");
        emFactoryCustomizers.forEach(c -> packagesToScan.addAll(c.additionalPackagesToScan()));
        return packagesToScan.toArray(String[]::new);
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return adapter;
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        Map<String, Object> vendorProperties = new HashMap<>();
        vendorProperties.put(PersistenceUnitProperties.WEAVING, "static");
        vendorProperties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, "true");
        vendorProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        emFactoryCustomizers.forEach(c -> vendorProperties.putAll(c.additionalVendorProperties()));
        return vendorProperties;
    }

    @Bean
    public TransactionTemplate txTemplate(PlatformTransactionManager transactionManager) {
        var tt = new TransactionTemplate();
        tt.setTransactionManager(transactionManager);
        return tt;
    }
}
