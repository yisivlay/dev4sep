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
package com.base.infrastructure.core.boot;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

/**
 * @author YISivlay
 */
public class DataSourceProperties extends PoolProperties {

    @Value("${dev4sep.datasource.port}")
    private volatile @NotNull
    int port;

    @Value("${dev4sep.datasource.host}")
    private volatile @NotNull
    String hostname;

    @Value("${dev4sep.datasource.db}")
    private volatile @NotNull
    String dbName;

    @Value("${dev4sep.datasource.username}")
    private volatile @NotNull
    String username;

    @Value("${dev4sep.datasource.password}")
    private volatile @NotNull
    String password;

    @Value("${dev4sep.datasource.protocol}")
    private volatile @NotNull
    String jdbcProtocol;

    @Value("${dev4sep.datasource.sub_protocol}")
    private volatile @NotNull
    String jdbcSubProtocol;

    public DataSourceProperties(String driverClassName, String protocol, String subProtocol, Integer port) {
        super();
        setDriverClassName(driverClassName);
        this.jdbcProtocol = protocol;
        this.jdbcSubProtocol = subProtocol;
        this.port = port;
        setDefaults();
    }

    protected void setDefaults() {
        setInitialSize(3);
        if (getValidationQuery() == null)
            setValidationQuery("SELECT 1");
        setTestOnBorrow(true);
        setTestOnReturn(true);
        setTestWhileIdle(true);
        setTimeBetweenEvictionRunsMillis(30000);
        setTimeBetweenEvictionRunsMillis(60000);
        setLogAbandoned(true);
        setSuspectTimeout(60);

        setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport");
    }

    @Override
    public String getUrl() {
        var url = super.getUrl();
        if (StringUtils.hasText(url)) {
            throw new IllegalStateException();
        }
        return jdbcProtocol + ":" + jdbcSubProtocol + "://" + getHost() + ":" + getPort() + "/" + getDBName();
    }

    @Override
    public void setUrl(@SuppressWarnings("unused") String url) {
        throw new UnsupportedOperationException("Use setHost/Port/DB() instead of setURL()");
    }

    public String getHost() {
        return hostname;
    }

    public void setHost(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDBName() {
        return dbName;
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

}
