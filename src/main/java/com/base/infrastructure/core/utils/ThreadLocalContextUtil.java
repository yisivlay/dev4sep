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
package com.base.infrastructure.core.utils;

import com.base.infrastructure.core.tenant.PlatformTenant;
import org.springframework.util.Assert;

/**
 * {@code @author:} YISivlay
 */
public class ThreadLocalContextUtil {

    public static final String CONTEXT_TENANTS = "tenants";

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    private static final ThreadLocal<PlatformTenant> tenantContext = new ThreadLocal<>();

    private static final ThreadLocal<String> authTokenContext = new ThreadLocal<>();

    public static PlatformTenant getTenant() {
        return tenantContext.get();
    }

    public static void setTenant(final PlatformTenant tenant) {
        Assert.notNull(tenant, "tenant cannot be null");
        tenantContext.set(tenant);
    }

    public static void clearTenant() {
        tenantContext.remove();
    }

    public static String getDataSourceContext() {
        return contextHolder.get();
    }

    public static void setDataSourceContext(final String dataSourceContext) {
        contextHolder.set(dataSourceContext);
    }

    public static void clearDataSourceContext() {
        contextHolder.remove();
    }

    public static String getAuthToken() {
        return authTokenContext.get();
    }

    public static void setAuthToken(final String authToken) {
        authTokenContext.set(authToken);
    }
}
