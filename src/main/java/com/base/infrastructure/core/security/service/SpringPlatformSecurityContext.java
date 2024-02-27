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

import com.base.administration.user.domain.User;
import com.base.infrastructure.core.exception.ResetPasswordException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
public class SpringPlatformSecurityContext implements PlatformSecurityContext {

    @Override
    public User authenticatedUser() {
        User currentUser = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final var auth = context.getAuthentication();
            if (auth != null) {
                currentUser = (User) auth.getPrincipal();
            }
        }
        if (currentUser == null) {
            throw new RuntimeException();
        }
        if (this.doesPasswordHasToBeRenewed(currentUser)) {
            throw new ResetPasswordException(currentUser.getId());
        }

        return currentUser;
    }

    @Override
    public User getAuthenticatedUserIfPresent() {
        return null;
    }

    @Override
    public boolean doesPasswordHasToBeRenewed(User currentUser) {
        return false;
    }
}
