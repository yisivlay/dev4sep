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
package com.base.infrastructure.core.security.filter;

import com.base.infrastructure.core.security.data.RequestLog;
import com.base.infrastructure.core.security.service.BasicAuthTenantDetailsService;
import com.base.infrastructure.core.serialization.ToApiJsonSerializer;
import com.base.infrastructure.core.utils.ThreadLocalContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@Profile("basicauth")
public class BasicAuthenticationProcessingFilter extends BasicAuthenticationFilter {

    private static boolean firstRequestProcessed = false;
    private final ToApiJsonSerializer<RequestLog> toApiJsonSerializer;
    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;

    @Autowired
    public BasicAuthenticationProcessingFilter(final AuthenticationManager authenticationManager,
                                               final AuthenticationEntryPoint authenticationEntryPoint,
                                               final ToApiJsonSerializer<RequestLog> toApiJsonSerializer,
                                               final BasicAuthTenantDetailsService basicAuthTenantDetailsService) {
        super(authenticationManager, authenticationEntryPoint);
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        final var task = new StopWatch();
        task.start();
        try {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            final var reqHead = request.getHeader("Access-Control-Request-Headers");
            if (org.springframework.util.StringUtils.hasText(reqHead)) {
                response.addHeader("Access-Control-Allow-Headers", reqHead);
            }

            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
            } else {
                    var tenantRequestHeader = "DEV4Sep-Platform-TenantId";
                    var tenantIdentifier = request.getHeader(tenantRequestHeader);
                    if (StringUtils.isBlank(tenantIdentifier)) {
                        tenantIdentifier = request.getParameter("tenantIdentifier");
                    }
                    if (tenantIdentifier == null) {
                        throw new RuntimeException("No tenant identifier found: Add request header of '"
                                + tenantRequestHeader + "' or add the parameter 'tenantIdentifier' to query string of request URL.");
                    }
                    var pathInfo = request.getRequestURI();
                    boolean isReportRequest = pathInfo != null && pathInfo.contains("report");
                    final var tenant = this.basicAuthTenantDetailsService.loadTenantById(tenantIdentifier, isReportRequest);
                    ThreadLocalContextUtil.setTenant(tenant);
                    var authToken = request.getHeader("Authorization");
                    if (authToken != null && authToken.startsWith("Basic ")) {
                        ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("Basic ", ""));
                    }
                    if (!firstRequestProcessed) {
                        final var baseUrl = request.getRequestURL().toString().replace(request.getPathInfo(), "/");
                        System.setProperty("baseUrl", baseUrl);
                        firstRequestProcessed = true;
                    }
                filterChain.doFilter(request, response);
            }
        } catch (final RuntimeException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "DEV4Sep Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            task.stop();
            var requestLog = RequestLog.from(task, request);
            log.info(this.toApiJsonSerializer.serialize(requestLog));
        }
    }
}
