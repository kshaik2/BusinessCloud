package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.Role;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.hibernate.BulkOperator;
import com.infor.cloudsuite.platform.jpa.hibernate.CacheOperator;
import com.infor.cloudsuite.platform.jpa.hibernate.StatisticsOperator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Transactional(propagation = Propagation.REQUIRED)
public class CacheStatisticsServiceTest extends AbstractTest {

    @Resource
    CacheOperator cacheOperator;

    @Resource
    StatisticsOperator statisticsOperator;

    @Resource
    CacheStatisticsService cacheStatisticsService;

    @Resource
    UserDao userDao;
    @Resource
    BulkOperator bulkOperator;

    @Override
    @Before
    public void before() {
        //overriding so that a transaction modifying the User table is not created.
        //While the transaction is in progress the Query cache will continue to believe the User table is dirty
        //executing new queries each time.
        //Anything added with the bulk operator or JDBC executor will not cause this problem
    }

    @Test
    public void doSomeTest() throws Exception {
        //TODO add back if caching is added.
//        //clear statistics.
//        statisticsOperator.clear();
//
//        List<Long> ids = insertUsers();
//
//        for (int i = 0; i < 10; i++) {
//            User user = userDao.findByUsername("user" + i + "@users.com");
//            assertNotNull("Not Found by userName: " + ids.get(i), user);
//            User byId = userDao.findById(ids.get(i));
//            assertNotNull("Not Found by Id: " + ids.get(i), byId);
//        }
//
//        //clear the second level cache
//        cacheOperator.evictAll();
//        //clear the session cache.
//        userDao.clear();
//
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 3; j++) {
//                fetchUserById(ids.get(i), j);
//            }
//        }
//        //clear the second level cache
//        cacheOperator.evictAll();
//        cacheOperator.evictAllQueryCache();
//        //clear the session cache.
//        userDao.clear();
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 3; j++) {
//
//                fetchUserByUsername(i, j);
//            }
//        }
//
//        //Clean up the database.
//        deleteUsers(ids);
    }

    @Transactional
    private void fetchUserByUsername(int idPos, int iter) {

        String loc = String.format(" -- id: %s [iter:%s]", idPos, iter);

        long hits = statisticsOperator.getQueryCacheHits();
        long misses = statisticsOperator.getQueryCacheMisses();
        long puts = statisticsOperator.getQueryCachePuts();
        User fetchuser = userDao.findByUsername("user" + idPos + "@users.com");
        assertNotNull(fetchuser);

        if (iter == 0) {
            assertEquals("hits is equal" + loc, hits, statisticsOperator.getQueryCacheHits());
            assertEquals("misses + 1" + loc, misses + 1, statisticsOperator.getQueryCacheMisses());
            assertEquals("puts + 1" + loc, puts + 1, statisticsOperator.getQueryCachePuts());
        } else {
            assertEquals("hits + 1" + loc, hits + 1, statisticsOperator.getQueryCacheHits());
            assertEquals("misses is equal" + loc, misses, statisticsOperator.getQueryCacheMisses());
            assertEquals("puts is equal" + loc, puts, statisticsOperator.getQueryCachePuts());
        }
        assertTrue("BEFORE detach", cacheOperator.cacheContainsEntity(User.class, fetchuser.getId()));
        assertTrue("AFTER detach", cacheOperator.cacheContainsEntity(User.class, fetchuser.getId()));
    }

    @Transactional
    private void fetchUserById(long id, int iter) {
        //User fetchuser=userDao.findByUsername("user"+i+"@users.com");
        String loc = String.format(" -- id: %s [iter:%s]", id, iter);
        assertEquals("BEFORE find" + loc, iter != 0, cacheOperator.cacheContainsEntity(User.class, id));
        long hits = statisticsOperator.getSecondLevelCacheHitCount();
        long misses = statisticsOperator.getSecondLevelCacheMissCount();
        long puts = statisticsOperator.getSecondLevelCachePutCount();
        User fetchById = userDao.findById(id);
        assertNotNull(fetchById);
        if (iter == 0) {
            assertEquals("hits is equal" + loc, hits, statisticsOperator.getSecondLevelCacheHitCount());
            assertEquals("misses + 1" + loc, misses + 1, statisticsOperator.getSecondLevelCacheMissCount());
            assertEquals("puts + 1" + loc, puts + 1, statisticsOperator.getSecondLevelCachePutCount());
        } else {
            assertEquals("hits + 1" + loc, hits + 1, statisticsOperator.getSecondLevelCacheHitCount());
            assertEquals("misses is equal" + loc, misses, statisticsOperator.getSecondLevelCacheMissCount());
            assertEquals("puts is equal" + loc, puts, statisticsOperator.getSecondLevelCachePutCount());
        }

        assertTrue("BEFORE detach", cacheOperator.cacheContainsEntity(User.class, fetchById.getId()));
        userDao.detach(fetchById);
        assertTrue("AFTER detach", cacheOperator.cacheContainsEntity(User.class, fetchById.getId()));
    }

    @Transactional
    public List<Long> insertUsers() {

        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < 10; i++) {

            User user = createUser("user" + i + "@users.com", "User", "Lname" + i, "bob", Role.ROLE_EXTERNAL);
            bulkOperator.insert(user);
            ids.add(user.getId());
        }
        bulkOperator.flush();

        return ids;
    }

    //need to delete users to clean up the database.
    public void deleteUsers(List<Long> ids) {
        for (Long id : ids) {
            bulkOperator.delete(userDao.getReference(id));
        }
        bulkOperator.flush();
    }
}
