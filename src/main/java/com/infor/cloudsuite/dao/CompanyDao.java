package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.CompanyDto;
import com.infor.cloudsuite.entity.Company;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * Created with IntelliJ IDEA.
 * User: briancrow
 * Date: 5/23/12
 * Time: 9:16 AM
 */
public interface CompanyDao extends ExtJpaRepository<Company, Long> {
	
	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.CompanyDto" +
			"(c.id,c.inforId,c.name,c.notes, c.industry.id)" +
			"from Company c where c.id=?1 ")
	public CompanyDto getDtoByTableId(Long id);
	
	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.CompanyDto" +
			"(c.id,c.inforId,c.name,c.notes, c.industry.id)" +
			"from Company c where c.inforId=?1 ")
	public CompanyDto getDtoByInforId(String inforId);
	
	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.CompanyDto" +
			"(c.id,c.inforId,c.name,c.notes, c.industry.id)" +
			"from Company c where upper(c.name) LIKE upper(?1) ")
	public List<CompanyDto> getDtosByMatch(String likeString);
	
	@Query("select new com.infor.cloudsuite.dto.CompanyDto" +
			"(c.id,c.inforId,c.name,c.notes, c.industry.id)" +
			"from Company c")
	public List<CompanyDto> getAllDtos();
	
	public Company findByName(String name);

}
