package com.example.UMS_MultiDB.config;

import com.example.UMS_MultiDB.model.enums.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseRouter {

    private final DataSource mainDataSource;
    private final DataSource testDataSource;
    private final DataSource devDataSource;

    public DatabaseRouter(
            @Qualifier("mainDataSource") DataSource mainDataSource,
            @Qualifier("testDataSource") DataSource testDataSource,
            @Qualifier("devDataSource") DataSource devDataSource) {
        this.mainDataSource = mainDataSource;
        this.testDataSource = testDataSource;
        this.devDataSource = devDataSource;
    }

    public DataSource getMainDataSource(Role role){
        return switch (role){
            case ADMIN, REGULAR_USER -> mainDataSource;
            case DEVELOPER -> devDataSource;
            case TEST_USER ->  testDataSource;
        };
    }
}
