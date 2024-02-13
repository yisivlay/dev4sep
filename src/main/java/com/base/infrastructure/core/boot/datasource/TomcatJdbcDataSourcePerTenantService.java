/**
 * Copyright 2024 DEV4Sep
 *
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
package com.base.infrastructure.core.boot.datasource;

import com.base.infrastructure.core.config.JdbcConfig;
import com.base.infrastructure.core.tenant.PlatformTenantConnection;
import com.base.infrastructure.core.utils.ThreadLocalContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:} YISivlay
 */
@Slf4j
@Service
public class TomcatJdbcDataSourcePerTenantService implements RoutingDataSourceService {

    private final Map<Long, DataSource> tenantToDataSourceMap = new HashMap<>(1);
    private final DataSource tenantDataSource;

    @Autowired
    private JdbcConfig jdbcConfig;

    @Autowired
    public TomcatJdbcDataSourcePerTenantService(final @Qualifier("tenantDataSourceJndi") DataSource tenantDataSource) {
        this.tenantDataSource = tenantDataSource;
    }

    @Override
    public DataSource retrieveDataSource() {

        // default to tenant database datasource
        var tenantDataSource = this.tenantDataSource;
        final var tenant = ThreadLocalContextUtil.getTenant();
        if (tenant != null) {
            final var tenantConnection = tenant.connection();
            synchronized (this.tenantToDataSourceMap) {
                if (this.tenantToDataSourceMap.containsKey(tenantConnection.getConnectionId())) {
                    tenantDataSource = this.tenantToDataSourceMap.get(tenantConnection.getConnectionId());
                } else {
                    tenantDataSource = createNewDataSourceFor(tenantConnection);
                    this.tenantToDataSourceMap.put(tenantConnection.getConnectionId(), tenantDataSource);
                }
            }
        }

        return tenantDataSource;
    }

    private DataSource createNewDataSourceFor(final PlatformTenantConnection tenantConnectionObj) {
        var jdbcUrl = this.jdbcConfig.constructProtocol(tenantConnectionObj.getSchemaServer(), tenantConnectionObj.getSchemaServerPort(), tenantConnectionObj.getSchemaName());
        final var poolConfiguration = new PoolProperties();
        poolConfiguration.setDriverClassName(this.jdbcConfig.getDriverClassName());
        poolConfiguration.setName(tenantConnectionObj.getSchemaName() + "_pool");
        poolConfiguration.setUrl(jdbcUrl);
        poolConfiguration.setUsername(tenantConnectionObj.getSchemaUsername());
        poolConfiguration.setPassword(tenantConnectionObj.getSchemaPassword());
        poolConfiguration.setInitialSize(tenantConnectionObj.getInitialSize());
        poolConfiguration.setTestOnBorrow(tenantConnectionObj.isTestOnBorrow());
        poolConfiguration.setValidationQuery("SELECT 1");
        poolConfiguration.setValidationInterval(tenantConnectionObj.getValidationInterval());
        poolConfiguration.setRemoveAbandoned(tenantConnectionObj.isRemoveAbandoned());
        poolConfiguration.setRemoveAbandonedTimeout(tenantConnectionObj.getRemoveAbandonedTimeout());
        poolConfiguration.setLogAbandoned(tenantConnectionObj.isLogAbandoned());
        poolConfiguration.setAbandonWhenPercentageFull(tenantConnectionObj.getAbandonWhenPercentageFull());

        poolConfiguration.setMaxActive(tenantConnectionObj.getMaxActive());
        poolConfiguration.setMinIdle(tenantConnectionObj.getMinIdle());
        poolConfiguration.setMaxIdle(tenantConnectionObj.getMaxIdle());
        poolConfiguration.setSuspectTimeout(tenantConnectionObj.getSuspectTimeout());
        poolConfiguration.setTimeBetweenEvictionRunsMillis(tenantConnectionObj.getTimeBetweenEvictionRunsMillis());
        poolConfiguration.setMinEvictableIdleTimeMillis(tenantConnectionObj.getMinEvictableIdleTimeMillis());

        poolConfiguration.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport");

        return new org.apache.tomcat.jdbc.pool.DataSource(poolConfiguration);
    }

}
