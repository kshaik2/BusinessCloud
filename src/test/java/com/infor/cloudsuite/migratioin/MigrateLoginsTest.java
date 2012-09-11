package com.infor.cloudsuite.migratioin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dao.UserTrackingDao;
import com.infor.cloudsuite.entity.TrackingType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserTracking;
import com.infor.cloudsuite.migration.MigrateLogins;
import com.infor.cloudsuite.platform.jpa.hibernate.JDBCExecutor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * User: bcrow
 * Date: 1/10/12 9:19 AM
 */
public class MigrateLoginsTest extends AbstractTest {
    @Resource
    private JDBCExecutor jdbcExecutor;
    @Resource
    MigrateLogins migrateLogins;
    @Resource
    UserTrackingDao userTrackingDao;
    @Resource
    UserDao userDao;

    @Override
    @Before
    public void before() {
        createLoginTable();
        super.before();
    }

    //if this is done inside the test, transaction information will commit where we usually want a rollback.
    private void createLoginTable() {
        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                connection.createStatement().executeUpdate("CREATE TABLE login ( id numeric(19,0), user_id numeric(19,0), timestamp TIMESTAMP )");
            }
        });
    }

    @Test
    @Transactional
    public void testMigrateLogins() throws Exception {
        
        final User user = userDao.findByUsername(testUserName);
        jdbcExecutor.executeQuery(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                final PreparedStatement statement = connection.prepareStatement("INSERT INTO login VALUES ?,?,?");
                statement.setLong(1, 1L);
                statement.setLong(2, user.getId());
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.execute();
                statement.setLong(1, 2L);
                statement.setLong(2, user.getId());
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.execute();

            }
        });


        migrateLogins.migrate();
        final List<UserTracking> trackings = userTrackingDao.findAll();
        assertNotNull(trackings);
        assertEquals(2, trackings.size());
        for (UserTracking tracking : trackings) {
            assertEquals(user.getId(), tracking.getUser().getId());
            assertEquals(TrackingType.LOGIN, tracking.getTrackingType());
        }
    }
}
