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
package com.base.infrastructure.core.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@code @author:} YISivlay
 */
@Service
public class JdbcTenantDetailsService implements TenantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTenantDetailsService(final @Qualifier("tenantDataSourceJndi") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PlatformTenant loadTenantById(String tenantIdentifier) {
        try {
            final TenantMapper rm = new TenantMapper();
            final String sql = "SELECT  " + rm.schema() + " WHERE t.identifier LIKE ?";

            return this.jdbcTemplate.queryForObject(sql, rm, tenantIdentifier);
        } catch (final EmptyResultDataAccessException e) {
            throw new RuntimeException("The tenant identifier: " + tenantIdentifier + " is not valid.");
        }
    }

    @Override
    public List<PlatformTenant> findAllTenants() {

        final TenantMapper rm = new TenantMapper();
        final String sql = "SELECT  " + rm.schema();

        return this.jdbcTemplate.query(sql, rm, new Object[]{});

    }
}
