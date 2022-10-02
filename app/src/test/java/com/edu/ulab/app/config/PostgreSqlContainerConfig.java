package com.edu.ulab.app.config;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
@Testcontainers
public class PostgreSqlContainerConfig {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "qwerty";
    private static final String POSTGRESQL_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/mpl_ylab_db";

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(POSTGRESQL_DRIVER_CLASS_NAME);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return proxyListenerDataSource(dataSource);
    }

    private DataSource proxyListenerDataSource(final DataSource dataSource) {
        ChainListener listener = new ChainListener();
        SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
        listener.addListener(loggingListener);
        listener.addListener(new DataSourceQueryCountListener());

        return ProxyDataSourceBuilder
                .create(dataSource)
                .name("DS-Proxy")
                .listener(listener)
                .build();
    }
}
