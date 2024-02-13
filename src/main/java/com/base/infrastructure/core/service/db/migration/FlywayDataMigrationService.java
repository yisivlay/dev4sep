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

import com.base.infrastructure.core.config.JdbcConfig;
import com.base.infrastructure.core.tenant.PlatformTenant;
import com.base.infrastructure.core.tenant.PlatformTenantConnection;
import com.base.infrastructure.core.tenant.TenantDetailsService;
import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.FlywayException;
import com.googlecode.flyway.core.util.jdbc.DriverDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * {@code @author:} YISivlay
 */
@Service
public class FlywayDataMigrationService {

    protected final DataSource dataSource;
    protected final FlywayDatasourceService flywayDatasourceService;
    private final TenantDetailsService tenantDetailsService;
    private final JdbcConfig jdbcConfig;

    @Autowired
    public FlywayDataMigrationService(final @Qualifier("tenantDataSourceJndi") DataSource dataSource,
                                      final FlywayDatasourceService flywayDatasourceService,
                                      final TenantDetailsService tenantDetailsService,
                                      final JdbcConfig jdbcConfig) {
        this.dataSource = dataSource;
        this.flywayDatasourceService = flywayDatasourceService;
        this.tenantDetailsService = tenantDetailsService;
        this.jdbcConfig = jdbcConfig;
    }

    @PostConstruct
    public void migration() {
        tenantDatabase();
        final List<PlatformTenant> tenants = this.tenantDetailsService.findAllTenants();
        for (final PlatformTenant tenant : tenants) {
            final PlatformTenantConnection connection = tenant.connection();
            if (connection.isAutoUpdateEnabled()) {
                final Flyway flyway = new Flyway();
                String connectionProtocol = jdbcConfig.constructProtocol(connection.getSchemaServer(), connection.getSchemaServerPort(), connection.getSchemaName());
                DriverDataSource source = new DriverDataSource(jdbcConfig.getDriverClassName(), connectionProtocol, connection.getSchemaUsername(), connection.getSchemaPassword());
                flyway.setDataSource(source);
                flyway.setLocations("db/migrations/core_db");
                flyway.setOutOfOrder(true);
                flyway.setValidateOnMigrate(false);
                try {
                    flyway.migrate();
                } catch (FlywayException e) {
                    String betterMessage = e.getMessage() + "; for tenant database url: " + connectionProtocol + ", username: " + connection.getSchemaUsername();
                    throw new FlywayException(betterMessage, e.getCause());
                }
            }
        }
    }

    private void tenantDatabase() {
        final Flyway flyway = new Flyway();

        flyway.setDataSource(dataSource);
        flyway.setLocations("db/migrations/tenant_db");
        flyway.setOutOfOrder(true);
        flyway.setValidateOnMigrate(false);
        flyway.migrate();

        flywayDatasourceService.updateSchemaVersion();
    }
}
