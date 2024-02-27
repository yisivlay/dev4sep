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
package com.base.administration.user.domain;

import com.base.administration.role.domain.Role;
import com.base.infrastructure.core.security.domain.PlatformUser;
import com.base.infrastructure.core.security.exception.NoAuthorizationException;
import com.base.organisation.office.domain.Office;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.*;

/**
 * @author YISivlay
 */
@Slf4j
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = "username"),
        @UniqueConstraint(columnNames = "email", name = "email")
})
public class User extends AbstractPersistable<Long> implements PlatformUser {

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name = "password", nullable = false)
    private String password;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address")
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "activated_date")
    private Date activatedDate;

    @Column(name = "activation_key", length = 50)
    private String activationKey;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Column(name = "password_never_expires")
    private boolean passwordNeverExpires;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "is_deleted")
    private boolean deleted;

    protected User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return populateGrantedAuthorities();
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        this.roles.stream()
                .map(Role::getPermissions)
                .forEach(permissions -> permissions
                        .stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                        .forEach(grantedAuthorities::add)
                );
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Office getOffice() {
        return office;
    }

    public void validateHasReadPermission(String permission) {
        final var authorizationMessage = "User has no authority to view " + permission.toLowerCase();
        final var matchPermission = "READ_" + permission.toUpperCase();

        if (!hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission)) {
            return;
        }

        throw new NoAuthorizationException(authorizationMessage);
    }

    public boolean hasNotPermissionForAnyOf(final String... permissionCodes) {
        var hasNotPermission = true;
        for (final var permissionCode : permissionCodes) {
            final var checkPermission = hasPermissionTo(permissionCode);
            if (checkPermission) {
                hasNotPermission = false;
                break;
            }
        }
        return hasNotPermission;
    }

    private boolean hasPermissionTo(final String permissionCode) {
        var hasPermission = hasAllFunctionsPermission();
        if (!hasPermission) {
            if (this.roles.stream().anyMatch(role -> role.hasPermissionTo(permissionCode))) {
                hasPermission = true;
            }
        }
        return hasPermission;
    }

    private boolean hasAllFunctionsPermission() {
        return this.roles.stream().anyMatch(role -> role.hasPermissionTo("ALL_FUNCTIONS"));
    }
}
