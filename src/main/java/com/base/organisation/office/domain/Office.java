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
package com.base.organisation.office.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Table(name = "offices", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "name"),
        @UniqueConstraint(columnNames = "external_id", name = "external_id")
})
public class Office extends AbstractPersistable<Long> {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private final List<Office> children = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Office parent;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "opening_date")
    private Date openingDate;

    @Column(name = "hierarchy", length = 100)
    private String hierarchy;

    @Column(name = "external_id", length = 100)
    private String externalId;

    protected Office() {
    }
}
