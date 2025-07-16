package com.example.UMS_MultiDB.config;

import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.repository.ExcelPersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseRouter {

    private final DataSource mainDataSource;
//    private final DataSource testDataSource;
    private final DataSource devDataSource;
    private final ExcelPersonRepository excelPersonRepository;
    public DatabaseRouter(
            @Qualifier("mainDataSource") DataSource mainDataSource,
            @Qualifier("devDataSource") DataSource devDataSource,
            ExcelPersonRepository excelPersonRepository) {
        this.mainDataSource = mainDataSource;
        this.excelPersonRepository = excelPersonRepository;
        this.devDataSource = devDataSource;
    }

    public DataSource getMainDataSource(Role role){
        return (DataSource) switch (role){
            case ADMIN, REGULAR_USER -> mainDataSource;
            case DEVELOPER -> devDataSource;
            case TEST_USER ->  excelPersonRepository;
        };
    }
}
