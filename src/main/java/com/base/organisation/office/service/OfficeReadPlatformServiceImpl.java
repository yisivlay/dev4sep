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
package com.base.organisation.office.service;

import com.base.infrastructure.core.boot.datasource.RoutingDataSource;
import com.base.infrastructure.core.security.service.PlatformSecurityContext;
import com.base.organisation.office.data.OfficeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * {@code @author:} YISivlay
 */
@Service
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
                                         final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<OfficeData> getAll(boolean includeAllOffices) {

        final String h = this.context.authenticatedUser().getOffice().getHierarchy();
        String hierarchy = null;
        if (includeAllOffices) {
            hierarchy = ".%";
        } else {
            hierarchy = h + "%";
        }
        String sql = "SELECT * FROM offices o where o.hierarchy like ? ";
        return this.jdbcTemplate.query(sql, (rs, rowNum) -> {

            final Long id = rs.getLong("id");
            final Long parentId = rs.getLong("parentId");
            final String parentName = rs.getString("parentName");
            final String name = rs.getString("name");
            final String nameDecorated = rs.getString("nameDecorated");
            final String hch = rs.getString("hierarchy");
            final String externalId = rs.getString("externalId");
            final LocalDate openingDate = rs.getDate("openingDate").toLocalDate();

            return OfficeData.builder()
                    .id(id)
                    .parentId(parentId)
                    .parentName(parentName)
                    .name(name)
                    .nameDecorated(nameDecorated)
                    .hierarchy(hch)
                    .externalId(externalId)
                    .openingDate(openingDate)
                    .build();
        }, hierarchy);
    }
}
