package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.RegionDto;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 3/19/12 10:43 AM
 */
public interface RegionDao extends ExtJpaRepository<Region, Long> {
    
    Region findByShortName(String shortName);
    
    @Query("select new com.infor.cloudsuite.dto.RegionDto(reg.id, reg.shortName, reg.name, " +
            "reg.endPoint, reg.cloudAlias, reg.regionType) " +
            "From Region reg")
    List<RegionDto> findRegionDtos();
    
}
