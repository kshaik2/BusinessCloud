package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialEnvironment;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 10/24/11 2:41 PM
 */
public interface TrialEnvironmentDao extends ExtJpaRepository<TrialEnvironment, Long> {

    /**
     * Find a Trial environments by their product id.
     * @param productVersionId productVersion to get environments for
     * @return list of TrialEnvironments for a product.
     */
    List<TrialEnvironment> findByProductVersionId(Long productVersionId);

    /**
     * Find a Trial environments by their product id and their region.
     * @param productVersion product to get environments for.
     * @param region region to get environments for.
     * @param available available or used.
     * @return list of TrialEnvironments for a product and region.
     */

    @Query(value="SELECT count(te.id) FROM TrialEnvironment te where productVersion = ?1 and region = ?2 and available = ?3")
    Long countByProductVersionAndRegionAndAvailable(ProductVersion productVersion, Region region, Boolean available);

    List<TrialEnvironment> findByProductVersionAndRegionAndAvailable(ProductVersion productVersion, Region region, Boolean available);
    TrialEnvironment findByEnvironmentId(String environmentId);

    @Query("select te from TrialEnvironment te where te IN ?1 AND te.url=?2")
    List<TrialEnvironment> findNarrowedByUrl(List<TrialEnvironment> environments,String url);
    
    @Query("select te from TrialEnvironment te where te IN ?1 AND te.username=?2")
    List<TrialEnvironment> findNarrowedByUsername(List<TrialEnvironment> environments, String username);

	List<TrialEnvironment> findByEnvironmentIdAndProductVersionAndRegion(String environmentId, ProductVersion productVersion, Region region);
	
	List<TrialEnvironment> findByProductVersionAndRegion(ProductVersion productVersion, Region region);

	List<TrialEnvironment> findByProductVersionAndEnvironmentIdAndAvailable(ProductVersion productVersion, String environmentId, Boolean available);

	List<TrialEnvironment> findByProductVersion_Product_Id(Long productId);

	List<TrialEnvironment> findByProductVersion_ProductAndRegionAndAvailable(Product product, Region region, Boolean available);

	@Modifying
	@Query("update TrialEnvironment te SET te.available=true where te.productVersion=?1 AND te.region=?2")
	public void makeEnvironmentsAvailable(ProductVersion productVersion, Region region);
}
