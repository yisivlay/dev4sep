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
package com.base.infrastructure.core.security.data;

import com.base.administration.role.data.RoleData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 * Immutable data object for authentication.
 * <p>
 * {@code @author:} YISivlay
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AuthenticatedUserData {

    @SuppressWarnings("unused")
    private String username;
    @SuppressWarnings("unused")
    private Long userId;
    @SuppressWarnings("unused")
    private String base64EncodedAuthenticationKey;
    @SuppressWarnings("unused")
    private boolean authenticated;
    @SuppressWarnings("unused")
    private Long officeId;
    @SuppressWarnings("unused")
    private String officeName;
    @SuppressWarnings("unused")
    private Long staffId;
    @SuppressWarnings("unused")
    private String staffDisplayName;
    @SuppressWarnings("unused")
    private EnumOptionData organisationalRole;
    @SuppressWarnings("unused")
    private Collection<RoleData> roles;
    @SuppressWarnings("unused")
    private Collection<String> permissions;
    private Collection<Long> clients;
    @SuppressWarnings("unused")
    private boolean shouldRenewPassword;

}
