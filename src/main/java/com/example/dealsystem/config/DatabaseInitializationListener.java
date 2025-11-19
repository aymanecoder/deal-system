package com.example.dealsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Listener to verify database tables are created after application startup
 */
@Component
@Order(1)
public class DatabaseInitializationListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        logger.info("Verifying database tables exist...");
        
        // Check and create transaction_log table if it doesn't exist
        createTransactionLogTableIfNotExists();
        
        String[] requiredTables = {
            "transaction_log",
            "valid_deal",
            "invalid_deal",
            "accumulative_deal_count"
        };
        
        for (String tableName : requiredTables) {
            try {
                String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
                
                if (count != null && count > 0) {
                    logger.info("✓ Table '{}' exists", tableName);
                } else {
                    logger.error("✗ Table '{}' does NOT exist! Liquibase may have failed.", tableName);
                }
            } catch (Exception e) {
                logger.error("Error checking table '{}': {}", tableName, e.getMessage(), e);
            }
        }
        
        logger.info("Database verification complete.");
    }
    
    private void createTransactionLogTableIfNotExists() {
        try {
            // Check if table exists
            String checkSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'transaction_log'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (count == null || count == 0) {
                logger.warn("transaction_log table does not exist. Creating it now...");
                
                // Create the table
                String createTableSql = """
                    CREATE TABLE transaction_log (
                        id BIGSERIAL PRIMARY KEY,
                        created_at TIMESTAMP,
                        file_name VARCHAR(255) NOT NULL UNIQUE,
                        status VARCHAR(20) NOT NULL,
                        valid_count BIGINT,
                        invalid_count BIGINT,
                        processing_duration_ms BIGINT,
                        started_at TIMESTAMP,
                        completed_at TIMESTAMP,
                        error_message VARCHAR(1000)
                    )
                    """;
                
                jdbcTemplate.execute(createTableSql);
                
                // Create index
                String createIndexSql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_file_name ON transaction_log(file_name)";
                jdbcTemplate.execute(createIndexSql);
                
                logger.info("✓ Successfully created transaction_log table");
            } else {
                logger.debug("transaction_log table already exists");
            }
        } catch (Exception e) {
            logger.error("Failed to create transaction_log table: {}", e.getMessage(), e);
        }
    }
}

