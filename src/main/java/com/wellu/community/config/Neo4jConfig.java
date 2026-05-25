package com.wellu.community.config;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;

@Configuration
public class Neo4jConfig {

    @Bean("neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager(
            Driver driver,
            DatabaseSelectionProvider databaseSelectionProvider,
            TransactionManagerCustomizers transactionManagerCustomizers
    ) {
        Neo4jTransactionManager transactionManager =
                new Neo4jTransactionManager(driver, databaseSelectionProvider);

        transactionManagerCustomizers.customize(transactionManager);
        return transactionManager;
    }
}
