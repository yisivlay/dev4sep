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
package com.base.infrastructure.core.security.service;

import com.base.infrastructure.core.tenant.PlatformTenant;
import com.base.infrastructure.core.tenant.PlatformTenantConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBC implementation of {@link BasicAuthTenantDetailsService} for loading a tenants details by a
 * <code>tenantIdentifier</code>.
 * <p>
 * @author YISivlay
 */
@Service
public class BasicAuthTenantDetailsServiceJdbc implements BasicAuthTenantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BasicAuthTenantDetailsServiceJdbc(@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PlatformTenant loadTenantById(String tenantIdentifier, boolean isReport) {
        try {
            final var rm = new TenantMapper(isReport);
            final var sql = "SELECT  " + rm.schema() + " WHERE t.identifier = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, tenantIdentifier);
        } catch (final EmptyResultDataAccessException e) {
            throw new RuntimeException("The tenant identifier: " + tenantIdentifier + " is not valid.", e);
        }
    }

    public static final class TenantMapper implements RowMapper<PlatformTenant> {

        private final boolean isReport;

        private final StringBuilder sqlBuilder = new StringBuilder("t.id, ts.id as connectionId , ")
                .append(" t.timezone_id as timezoneId , t.name,t.identifier, ts.schema_name as schemaName, ts.schema_server as schemaServer,")
                .append(" ts.schema_server_port as schemaServerPort, ts.auto_update as autoUpdate,")
                .append(" ts.schema_username as schemaUsername, ts.schema_password as schemaPassword , ts.pool_initial_size as initialSize,")
                .append(" ts.pool_validation_interval as validationInterval, ts.pool_remove_abandoned as removeAbandoned, ts.pool_remove_abandoned_timeout as removeAbandonedTimeout,")
                .append(" ts.pool_log_abandoned as logAbandoned, ts.pool_abandon_when_percentage_full as abandonedWhenPercentageFull, ts.pool_test_on_borrow as testOnBorrow,")
                .append(" ts.pool_max_active as poolMaxActive, ts.pool_min_idle as poolMinIdle, ts.pool_max_idle as poolMaxIdle,")
                .append(" ts.pool_suspect_timeout as poolSuspectTimeout, ts.pool_time_between_eviction_runs_millis as poolTimeBetweenEvictionRunsMillis,")
                .append(" ts.pool_min_evictable_idle_time_millis as poolMinEvictableIdleTimeMillis,")
                .append(" ts.deadlock_max_retries as maxRetriesOnDeadlock,")
                .append(" ts.deadlock_max_retry_interval as maxIntervalBetweenRetries ")
                .append(" FROM tenants t LEFT JOIN tenant_server_connections ts ");

        public TenantMapper(boolean isReport) {
            this.isReport = isReport;
        }

        public String schema() {
            if (this.isReport) {
                this.sqlBuilder.append(" ON t.report_Id = ts.id");
            } else {
                this.sqlBuilder.append(" ON t.oltp_Id = ts.id");
            }
            return this.sqlBuilder.toString();
        }

        @Override
        public PlatformTenant mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final var id = rs.getLong("id");
            final var tenantIdentifier = rs.getString("identifier");
            final var name = rs.getString("name");
            final var timezoneId = rs.getString("timezoneId");
            final var connection = getDBConnection(rs);

            return new PlatformTenant(id, tenantIdentifier, name, timezoneId, connection);
        }

        private PlatformTenantConnection getDBConnection(ResultSet rs) throws SQLException {

            final var connectionId = rs.getLong("connectionId");
            final var schemaName = rs.getString("schemaName");
            final var schemaServer = rs.getString("schemaServer");
            final var schemaServerPort = rs.getString("schemaServerPort");
            final var schemaUsername = rs.getString("schemaUsername");
            final var schemaPassword = rs.getString("schemaPassword");
            final var autoUpdateEnabled = rs.getBoolean("autoUpdate");
            final var initialSize = rs.getInt("initialSize");
            final var testOnBorrow = rs.getBoolean("testOnBorrow");
            final var validationInterval = rs.getLong("validationInterval");
            final var removeAbandoned = rs.getBoolean("removeAbandoned");
            final var removeAbandonedTimeout = rs.getInt("removeAbandonedTimeout");
            final var logAbandoned = rs.getBoolean("logAbandoned");
            final var abandonWhenPercentageFull = rs.getInt("abandonedWhenPercentageFull");
            final var maxActive = rs.getInt("poolMaxActive");
            final var minIdle = rs.getInt("poolMinIdle");
            final var maxIdle = rs.getInt("poolMaxIdle");
            final var suspectTimeout = rs.getInt("poolSuspectTimeout");
            final var timeBetweenEvictionRunsMillis = rs.getInt("poolTimeBetweenEvictionRunsMillis");
            final var minEvictableIdleTimeMillis = rs.getInt("poolMinEvictableIdleTimeMillis");
            var maxRetriesOnDeadlock = rs.getInt("maxRetriesOnDeadlock");
            var maxIntervalBetweenRetries = rs.getInt("maxIntervalBetweenRetries");

            maxRetriesOnDeadlock = bindValueInMinMaxRange(maxRetriesOnDeadlock, 0);
            maxIntervalBetweenRetries = bindValueInMinMaxRange(maxIntervalBetweenRetries, 1);

            return new PlatformTenantConnection(
                    connectionId,
                    schemaName,
                    schemaServer,
                    schemaServerPort,
                    schemaUsername,
                    schemaPassword,
                    autoUpdateEnabled,
                    initialSize,
                    validationInterval,
                    removeAbandoned,
                    removeAbandonedTimeout,
                    logAbandoned,
                    abandonWhenPercentageFull,
                    maxActive,
                    minIdle,
                    maxIdle,
                    suspectTimeout,
                    timeBetweenEvictionRunsMillis,
                    minEvictableIdleTimeMillis,
                    maxRetriesOnDeadlock,
                    maxIntervalBetweenRetries,
                    testOnBorrow
            );

        }

        private int bindValueInMinMaxRange(final int value, int min) {
            if (value < min) {
                return min;
            } else if (value > 15) {
                return 15;
            }
            return value;
        }
    }
}
