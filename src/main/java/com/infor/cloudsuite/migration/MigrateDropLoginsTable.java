package com.infor.cloudsuite.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.platform.jpa.hibernate.JDBCExecutor;

/**
 * User: bcrow
 * Date: 1/11/12 8:33 AM
 */
@Component
public class MigrateDropLoginsTable extends Migrator {
    private static final Logger logger = LoggerFactory.getLogger(MigrateDropLoginsTable.class);

    @Resource
    private JDBCExecutor jdbcExecutor;

    @Override
    @Transactional
    public void migrate() {
        logger.info("Attempting to drop obsolete Login table.");

        dropUserLogins();
        dropLoginTable();
        dropLoginSequenceTable();


    }

    private void dropLoginSequenceTable() {
        logger.info("Dropping the login sequence table.");
        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement("DROP TABLE Login_SEQ")) {
                    statement.executeUpdate();
                }
            }
        });
    }

    private void dropLoginTable() {
        logger.info("dropping the login table.");
        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE Login DROP FOREIGN KEY FK462FF4944663291")) {
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement("DROP TABLE Login")) {
                    statement.executeUpdate();
                }
            }
        });
    }

    private void dropUserLogins() {
        logger.info("dropping user_login table and foreign keys.");
        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE User_Login DROP foreign key FKE7FCE5F5F15078E2")) {
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE User_Login DROP foreign key FKE7FCE5F544663291")) {
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement("DROP TABLE User_Login")) {
                    statement.executeUpdate();
                }
            }
        });
    }
}
