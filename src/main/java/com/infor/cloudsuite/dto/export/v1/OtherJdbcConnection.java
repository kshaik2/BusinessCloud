package com.infor.cloudsuite.dto.export.v1;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.platform.components.SettingsProvider;

@Component
public class OtherJdbcConnection {

    private final static Logger logger = LoggerFactory.getLogger(OtherJdbcConnection.class);

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error("Failed to load MySql Driver.", e);
        }
    }

    @Resource
    private SettingsProvider settingsProvider;

    Connection mysqlConnection = null;

    public Connection getMysqlConnection() {
        if (mysqlConnection == null) {
            try {
                createConnection();
            } catch (SQLException sqle) {
                logger.error("Error constructing SQL Connection", sqle);
            }
        }

        return mysqlConnection;
    }

    public void setMysqlConnection(Connection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
    }

    private void createConnection() throws SQLException {
        this.mysqlConnection = DriverManager.getConnection(settingsProvider.getVersionOneProductionConnectionString(), settingsProvider.getVersionOneProductionUsername(), settingsProvider.getVersionOneProductionPassword());
        this.mysqlConnection.setReadOnly(true);
        logger.info("((Other))Connection string:" + settingsProvider.getVersionOneProductionConnectionString());
        logger.info("((Other))Username:" + settingsProvider.getVersionOneProductionUsername());

    }

    public ResultSet runQuery(String query) throws SQLException {

        try (Statement statement = getMysqlConnection().createStatement()) {
            return statement.executeQuery(query);
        }
    }

    public ResultSet runQuery(String query, Object... parameters) throws SQLException {
        try (PreparedStatement ps = getMysqlConnection().prepareStatement(query)) {
            int index = 1;
            for (Object param : parameters) {
                ps.setObject(index, param);
                index++;
            }
            return ps.executeQuery();
        }
    }

    public ResultSet runQuery(String query, String... parameters) throws SQLException {
        try (PreparedStatement ps = getMysqlConnection().prepareStatement(query)) {
            int index = 1;
            for (String param : parameters) {
                ps.setString(index, param);
                index++;
            }

            return ps.executeQuery();
        }
    }


    private static final SimpleDateFormat TZ = new SimpleDateFormat("zzz");

}
