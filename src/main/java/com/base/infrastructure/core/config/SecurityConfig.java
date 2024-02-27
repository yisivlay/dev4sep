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
package com.base.infrastructure.core.config;

import com.base.infrastructure.core.security.filter.BasicAuthenticationProcessingFilter;
import com.base.infrastructure.core.security.service.JpaPlatformUserDetailsService;
import com.base.infrastructure.core.security.service.LegacySupportPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author YISivlay
 */
@Configuration
@EnableMethodSecurity
@Profile("basicauth")
public class SecurityConfig {

    private final ServerProperties serverProperties;
    private final JpaPlatformUserDetailsService userDetailsService;
    private final LegacySupportPasswordEncoder legacySupportPasswordEncoder;

    @Autowired
    public SecurityConfig(final ServerProperties serverProperties,
                          final JpaPlatformUserDetailsService userDetailsService,
                          final LegacySupportPasswordEncoder legacySupportPasswordEncoder) {
        this.serverProperties = serverProperties;
        this.userDetailsService = userDetailsService;
        this.legacySupportPasswordEncoder = legacySupportPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(requestMatcher -> {
                    requestMatcher
                            .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/*/echo").permitAll()
                            .antMatchers(HttpMethod.POST, "/api/*/authentication").permitAll()
                            .antMatchers("/api/**").fullyAuthenticated();
                })
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic((httpBasic) -> httpBasic.authenticationEntryPoint(basicAuthenticationEntryPoint()));
        if (serverProperties.getSsl().isEnabled()) {
            http.requiresChannel(channel -> channel.requestMatchers(requestMatcher()).requiresSecure());
        }
        return http.build();
    }

    private RequestMatcher requestMatcher() {
        return (HttpServletRequest request) -> request.getRequestURI().startsWith("/api/**");
    }

    @Bean
    public FilterRegistrationBean<BasicAuthenticationProcessingFilter> tenantFilterRegistration(BasicAuthenticationProcessingFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(true);
        return registration;
    }

    @Bean(name = "authenticationProvider")
    public DaoAuthenticationProvider authProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(legacySupportPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        var providerManager = new ProviderManager(authProvider());
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        var basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("DEV4Sep Platform API");
        return basicAuthenticationEntryPoint;
    }
}
