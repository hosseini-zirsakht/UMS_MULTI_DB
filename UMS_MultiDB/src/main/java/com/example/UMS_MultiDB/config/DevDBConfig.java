package com.example.UMS_MultiDB.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.UMS_MultiDB.repository.dev",
        entityManagerFactoryRef = "devEntityManagerFactory",
        transactionManagerRef = "devTransactionManager"
)
class DevDBConfig {}
