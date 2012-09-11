package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.IndustryDto;
import com.infor.cloudsuite.entity.Industry;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface IndustryDao extends ExtJpaRepository<Industry, Long>{

	@Query("select new com.infor.cloudsuite.dto.IndustryDto(i.id,i.name,i.description) from Industry i")
	public List<IndustryDto> getAllDtos();
	
	public Industry findByName(String name);
	
}
