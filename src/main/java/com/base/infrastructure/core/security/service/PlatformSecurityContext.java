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

/**
 * @author YISivlay
 */
public interface PlatformSecurityContext {

    User authenticatedUser();

    /**
     * Convenience method returns null (does not throw an exception) if an
     * authenticated user is not present
     * <p>
     * To be used only in service layer methods that can be triggered via both
     * the API and batch Jobs (which do not have an authenticated user)
     *
     * @return
     */
    User getAuthenticatedUserIfPresent();

    boolean doesPasswordHasToBeRenewed(User currentUser);
}
