package com.infor.cloudsuite.dao;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.db.ProductCountDto;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface DeploymentStackDao extends ExtJpaRepository<DeploymentStack, Long> {

	//public List<DeploymentStack> findByUser(User user);	
	public List<DeploymentStack> findByCreatedByUser(User user);
	public DeploymentStack findByVpcId(String vpcId);
	
	@Query("SELECT stack FROM DeploymentStack stack where stack.user=?1 AND stack.deploymentState NOT IN ?2")
	public List<DeploymentStack> findByUserWithStateNotIn(User user, EnumSet<DeploymentState> states);
	
	@Query("SELECT count(stack.id) FROM DeploymentStack stack WHERE stack.user=?1")
	public Long countByUser(User user);

	@Query("SELECT count(stack.id) from DeploymentStack stack WHERE stack.createdByUser=?1")
	public Long countByCreatedByUser(User createdBy);
	

	@Query("SELECT DISTINCT stack.user FROM DeploymentStack stack")
	public List<User> getUsersWithDeployments();
	
	@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT DISTINCT stack.createdByUser FROM DeploymentStack stack")
	public List<User> getCreatedByUsersWithDeployments();

    @Query("SELECT DISTINCT stack.vpcId FROM DeploymentStack stack WHERE stack.deploymentState != 'DELETED'")
    public List<String> getVpcIdsForDeployments();

    //May be neccessary if number of stacks is too large.
    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("SELECT count(stack.id) FROM DeploymentStack stack where stack.vpcId = ?1")
    public Long getCountByVpcId(String vpcId);
    
 
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto(versions.product.id,count(stack.id)) FROM "+
    		"DeploymentStack stack JOIN stack.deployedProductVersions versions "+
    		"WHERE stack.createdAt > ?1 "+
    		"GROUP BY versions.product.id")
    public List<ProductCountDto> countByCreatedAtAfter(Date date);
    
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto(versions.product.id,count(stack.id)) FROM "+
    		"DeploymentStack stack JOIN stack.deployedProductVersions versions "+
    		"WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2 "+
    		"GROUP BY versions.product.id") 
	public List<ProductCountDto> countByCreatedAtInPeriod(Date firstOfMonth, Date firstOfNextMonth);
    
    @Query("SELECT count(stack.id) from DeploymentStack stack WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2")
	public Long countCreatedAtBetween(Date firstOfMonth, Date nextMonth);
	
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto(versions.product.id,count(stack.id)) "+
    		"FROM DeploymentStack stack JOIN stack.deployedProductVersions versions "+
    		"WHERE stack.deploymentState != ?1 "+
    		"GROUP BY versions.product.id") 
    public List<ProductCountDto> countAllExcept(DeploymentState state);
    
    @Query("SELECT count(stack.id) FROM DeploymentStack stack WHERE stack.deploymentState != ?1 ")
	public Long getCountAllExcept(DeploymentState deleted);
    
    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto(versions.product.id,count(stack.id)) FROM "+
    		"DeploymentStack stack JOIN stack.deployedProductVersions versions "+
    		"WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2 AND stack.deploymentState=?3 "+
    		"GROUP BY versions.product.id") 
	public List<ProductCountDto> countByCreatedAtInPeriodAndDeploymentStateIs(Date firstDate, Date secondDate, DeploymentState state);

    @Query("SELECT new com.infor.cloudsuite.dto.db.ProductCountDto(versions.product.id,count(stack.id)) FROM "+
    		"DeploymentStack stack JOIN stack.deployedProductVersions versions "+
    		"WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2 AND stack.deploymentState != ?3 "+
    		"GROUP BY versions.product.id") 
    public List<ProductCountDto> countByCreatedAtInPeriodAndDeploymentStateIsNot(Date firstDate, Date secondDate, DeploymentState state);
	
    @Query("SELECT count(stack.id) from DeploymentStack stack WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2 AND stack.deploymentState != ?3")
    public Long countCreatedAtBetweenAndDeploymentStateIsNot(Date firstDate,Date secondDate, DeploymentState state);
 
    @Query("SELECT count(stack.id) from DeploymentStack stack WHERE stack.createdAt >= ?1 AND stack.createdAt < ?2 AND stack.deploymentState = ?3")
    public Long countCreatedAtBetweenAndDeploymentStateIs(Date firstDate,Date secondDate, DeploymentState state);
 
	
}
