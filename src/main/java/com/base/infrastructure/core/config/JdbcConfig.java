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
package com.base.infrastructure.core.config;

import com.base.infrastructure.core.boot.datasource.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author YISivlay
 */
@Service
public class JdbcConfig {

    @Autowired
    ApplicationContext context;
    private String driverClassName;
    private String protocol;
    private String subProtocol;
    private Integer port;

    @PostConstruct
    protected void init() {
        Environment environment = context.getEnvironment();
        driverClassName = environment.getProperty("dev4sep.datasource.driver-class-name");
        protocol = environment.getProperty("dev4sep.datasource.protocol");
        subProtocol = environment.getProperty("dev4sep.datasource.sub_protocol");
        port = Integer.valueOf(Objects.requireNonNull(environment.getProperty("dev4sep.datasource.port")));
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final RoutingDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getSubProtocol() {
        return this.subProtocol;
    }

    public Integer getPort() {
        return this.port;
    }

    public String constructProtocol(String schemaServer, String schemaServerPort, String schemaName) {
        return protocol + ":" + subProtocol + "://" + schemaServer + ':' + schemaServerPort + '/' + schemaName;
    }
}
