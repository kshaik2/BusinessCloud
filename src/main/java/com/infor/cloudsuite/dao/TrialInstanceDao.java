package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.db.ProductCountDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.TrialInstanceType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 10/25/11 11:24 AM
 */
public interface TrialInstanceDao extends ExtJpaRepository<TrialInstance, Long> {

    /**
     * Find TrialInstances attached to a user.
     * @param userId user to find by.
     * @return the List of Trial Instances.
     */
    List<TrialInstance> findByUserId(Long userId);
    
    List<TrialInstance> findByExpirationDateLessThan(Date expirationDate);

    List<TrialInstance> findByProductVersion_Product_ShortNameAndExpirationDateLessThan(String shortName, Date expirationDate);
    
    List<TrialInstance> findByUserIdAndExpirationDateGreaterThan(Long userId, Date expirationDate);
    
    TrialInstance findByProductVersion_Product_ShortNameAndTypeAndDomain(String shortName, TrialInstanceType type, String Domain);
    
    TrialInstance findByGuid(String guid);
    
    @Query(value = "SELECT count(ti.id) FROM TrialInstance ti JOIN ti.productVersion pv WHERE pv.product = ?1 and ti.region = ?2 and ti.expirationDate > ?3")
    Long countByProductVersion_ProductAndRegionAndExpirationDateGreaterThan(Product product, Region region, Date expirationDate);
    
    @Modifying
    @Query("DELETE FROM TrialInstance trial WHERE trial.user = ?1")
    void deleteByUser(User user);

    List<TrialInstance> findByGuidIsNull();
   
    List<TrialInstance> findByUserAndProductVersion_Product(User user, Product product);

    @Query("SELECT distinct instances from TrialInstance instances where instances.url in (SELECT distinct ti.url FROM TrialInstance as ti where ti.domain=?1 AND ti.productVersion.product=?2)")
    List<TrialInstance> findTrialInstancesForMatchingDomainAndProduct(String domainPattern, Product product);
   
    /*
     * Methods just for jUnit tests
     */
    @Query("SELECT instances.guid from TrialInstance as instances")
    List<String> findAllGuids();

    @Query(value="SELECT count(ti.id) from TrialInstance ti WHERE ti.expirationDate > ?1 AND ti.domain IS NULL")
    Long countAllByExpirationDateAfter(Date date);

    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto (ti.productVersion.product.id, count(ti.id)) FROM TrialInstance ti "
    		+"WHERE ti.expirationDate > ?1 AND ti.domain is NULL "
    		+"GROUP BY ti.productVersion.product.id")
    public List<ProductCountDto> countByExpirationDateAfter(Date date);
    
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto (ti.productVersion.product.id, count(ti.id)) FROM TrialInstance ti "
    		+"WHERE ti.createdAt > ?1 and ti.domain is NULL "
    		+"GROUP BY ti.productVersion.product.id")
    public List<ProductCountDto> countByCreatedAtAfter(Date date);

    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto (ti.productVersion.product.id, count(ti.id)) FROM TrialInstance ti "
    		+"WHERE ti.createdAt >= ?1 AND ti.createdAt < ?2 AND ti.domain is NULL "
    		+"GROUP BY ti.productVersion.product.id")
	public List<ProductCountDto> countByCreatedAtInPeriod(Date firstOfMonth, Date firstOfNextMonth);

    @Query("SELECT count(ti.id) FROM TrialInstance ti WHERE ti.createdAt >= ?1 AND ti.createdAt < ?2 AND ti.domain IS NULL")
	public Long countCreatedAtBetween(Date firstOfMonth, Date nextMonth);

    @Query("SELECT count(ti.id) FROM TrialInstance ti WHERE ti.domain IS NULL")
	public Long countAllNonDomain();
    
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto (ti.productVersion.product.id, count(ti.id)) FROM TrialInstance ti "
    		+"WHERE ti.createdAt >= ?1 AND ti.createdAt < ?2 AND ti.domain is NULL AND ti.expirationDate > ?3 "
    		+"GROUP BY ti.productVersion.product.id")
	public List<ProductCountDto> countByCreatedAtInPeriodExpiresAfter(Date firstDate,Date secondDate, Date now);

    @Query(value="SELECT count(ti.id) from TrialInstance ti WHERE ti.expirationDate > ?3 AND ti.createdAt >= ?1 AND ti.createdAt < ?2 AND ti.domain IS NULL")
	public Long countCreatedAtBetweenAndExpiresAfter(Date firstDate, Date secondDate,Date now);

}
