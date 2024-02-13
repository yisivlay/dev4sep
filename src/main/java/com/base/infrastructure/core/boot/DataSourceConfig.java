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
package com.base.infrastructure.core.boot;

import com.base.infrastructure.core.config.JdbcConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * {@code @author:} YISivlay
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Autowired
    JdbcConfig jdbcConfig;

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties(jdbcConfig.getDriverClassName(), jdbcConfig.getProtocol(), jdbcConfig.getSubProtocol(), jdbcConfig.getPort());
    }

    @Bean
    @Primary
    public DataSource tenantDataSourceJndi() {
        var p = getProperties();
        var ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
        log.info("Created new datasource: " + p.getUrl());
        return ds;
    }

    protected DataSourceProperties getProperties() {
        return dataSourceProperties();
    }
}
