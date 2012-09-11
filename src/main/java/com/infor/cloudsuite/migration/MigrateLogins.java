package com.infor.cloudsuite.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.entity.TrackingType;
import com.infor.cloudsuite.entity.UserTracking;
import com.infor.cloudsuite.platform.jpa.hibernate.JDBCExecutor;

/**
 * Migrate logins from the Login table to the UserTracking Table.
 * User: bcrow
 * Date: 1/10/12 8:42 AM
 */
@Component
public class MigrateLogins extends Migrator {
    private static final Logger logger = LoggerFactory.getLogger(MigrateLogins.class);

    @Resource
    private UserTrackingDao userTrackingDao;
    @Resource
    private UserDao userDao;
    @Resource
    private JDBCExecutor jdbcExecutor;

    @Transactional
    private void doit(Connection connection) throws SQLException {
        logger.info("Attempting to migrate entries from the obsolete login table to usertracking table.");
        List<Long> moved;


        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT login.id, login.user_id, login.timestamp FROM Login as login")) {
            preparedStatement.execute();
            final ResultSet resultSet = preparedStatement.getResultSet();
            moved = new ArrayList<>();
            while (resultSet.next()) {
                final long id = resultSet.getLong(1);
                final long uesrId = resultSet.getLong(2);
                final Timestamp timestamp = resultSet.getTimestamp(3);
                final UserTracking tracking = new UserTracking(userDao.getReference(uesrId), TrackingType.LOGIN);
                tracking.setTimestamp(timestamp);
                userTrackingDao.save(tracking);
                moved.add(id);
            }
        }
        userTrackingDao.flush();

        logger.info("Migrated {} login entries to the usertracking table.", moved.size());
    }

    @Transactional
    public void migrate() {

        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                doit(connection);
            }
        });

    }
}
