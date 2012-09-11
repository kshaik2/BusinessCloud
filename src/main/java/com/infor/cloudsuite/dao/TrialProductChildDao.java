package com.infor.cloudsuite.dao;

import java.util.List;

import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialProductChild;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 3/23/12 10:30 AM
 */
public interface TrialProductChildDao extends ExtJpaRepository<TrialProductChild, Long> {
    
    public List<TrialProductChild> findByRegionAndParentVersion(Region region, ProductVersion parentVersion);
    public List<TrialProductChild> findByRegionAndChildVersion(Region region, ProductVersion childVersion);
    
    public List<TrialProductChild> findByRegion(Region region);
    public TrialProductChild findByRegionAndParentVersionAndChildVersion(Region region, ProductVersion parentVersion, ProductVersion childVersion);

}
