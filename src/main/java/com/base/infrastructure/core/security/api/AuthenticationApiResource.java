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
package com.base.infrastructure.core.security.api;

import com.base.administration.role.data.RoleData;
import com.base.administration.role.domain.Role;
import com.base.administration.user.domain.User;
import com.base.infrastructure.core.security.data.AuthenticatedUserData;
import com.base.infrastructure.core.security.service.SpringPlatformSecurityContext;
import com.base.infrastructure.core.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * {@code @author:} YISivlay
 */
@Component
@Profile("basicauth")
@Scope("singleton")
@Path("/authentication")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider authenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
    private final SpringPlatformSecurityContext springPlatformSecurityContext;

    public AuthenticationApiResource(@Qualifier("authenticationProvider") final DaoAuthenticationProvider authenticationProvider,
                                     final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
                                     final SpringPlatformSecurityContext springPlatformSecurityContext) {
        this.authenticationProvider = authenticationProvider;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.springPlatformSecurityContext = springPlatformSecurityContext;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public String authenticate(@QueryParam("username") final String username, @QueryParam("password") final String password) {

        final var authentication = new UsernamePasswordAuthenticationToken(username, password);
        final var authenticationCheck = this.authenticationProvider.authenticate(authentication);

        final Collection<String> permissions = new ArrayList<>();
        var authenticatedUserData = new AuthenticatedUserData().setUsername(username).setPermissions(permissions);

        if (authenticationCheck.isAuthenticated()) {
            final var authorities = new ArrayList<>(authenticationCheck.getAuthorities());
            for (final GrantedAuthority grantedAuthority : authorities) {
                permissions.add(grantedAuthority.getAuthority());
            }
            final var base64EncodedAuthenticationKey = Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            final var principal = (User) authenticationCheck.getPrincipal();
            final Collection<RoleData> roles;
            final var userRoles = principal.getRoles();
            roles = userRoles.stream().map(Role::toData).collect(Collectors.toList());

            final var officeId = principal.getOffice().getId();
            final var officeName = principal.getOffice().getName();

            if (this.springPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
                authenticatedUserData = new AuthenticatedUserData()
                        .setUsername(username)
                        .setUserId(principal.getId())
                        .setBase64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey));
            } else {
                authenticatedUserData = new AuthenticatedUserData()
                        .setUsername(username)
                        .setOfficeId(officeId)
                        .setOfficeName(officeName)
                        .setRoles(roles)
                        .setPermissions(permissions)
                        .setUserId(principal.getId())
                        .setBase64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey));
            }

        }

        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}
