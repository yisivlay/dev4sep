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
package com.base.infrastructure.core.service.db.migration;

import com.base.infrastructure.core.boot.DataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author YISivlay
 */
@Slf4j
@Service
public class FlywayDatasourceService {

    private final JdbcTemplate jdbcTemplate;
    @Value("${dev4sep.tenant.db.update.schema.version}")
    private boolean enabled;
    @Autowired
    private DataSourceProperties dsp;

    @Autowired
    public FlywayDatasourceService(@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void updateSchemaVersion() {
        if (!enabled) {
            log.info("No schema_server_port UPDATE made to tenant_server_connections table of the dev4sep-tenants schema, because properties 'dev4sep.tenant.db.fix-up=false'");
            return;
        }
        if (dsp == null) {
            log.debug("No schema_server_port UPDATE made to tenant_server_connections table of the dev4sep-tenants schema (because our own Spring Boot DataSourceConfiguration is used in a traditional WAR)");
            return;
        }
        int r = jdbcTemplate.update("UPDATE tenant_server_connections SET schema_server = ?, " +
                        "schema_server_port = ?, schema_username = ?, schema_password = ?",
                dsp.getHost(), dsp.getPort(), dsp.getUsername(), dsp.getPassword());
        if (r == 0)
            log.warn("UPDATE tenant_server_connections SET ... did not update ANY rows - something is probably wrong");
        else
            log.info("Updated " + r + " rows in the tenant_server_connections table of the dev4sep-tenants schema to the real current host: " + dsp.getHost() + ", port: " + dsp.getPort());

    }
}
