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
package com.base.administration.role.domain;

import com.base.administration.permission.domain.Permission;
import com.base.administration.role.data.RoleData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "name")})
public class Role extends AbstractPersistable<Long> {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private final Set<Permission> permissions = new HashSet<>();

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "is_disabled")
    private Boolean isDisabled;

    @Column(name = "is_for_sign_up_process")
    private Boolean isForSignupProcess;

    protected Role() {
    }

    public RoleData toData() {
        return RoleData.builder()
                .id(getId())
                .name(this.name)
                .description(this.description)
                .disabled(this.isDisabled)
                .build();
    }

    public boolean hasPermissionTo(final String permissionCode) {
        return this.permissions.stream().anyMatch(permission -> permission.hasCode(permissionCode));
    }

}
