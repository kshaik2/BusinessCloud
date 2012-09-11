package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.AmazonCredentialsDto;
import com.infor.cloudsuite.entity.AmazonCredentials;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

@SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
public interface AmazonCredentialsDao extends ExtJpaRepository<AmazonCredentials,Long> {
	
	
	@Query("SELECT new com.infor.cloudsuite.dto.AmazonCredentialsDto (amCred.id, amCred.user.id," +
			" amCred.name, amCred.awsKey, amCred.secretKey) " +
			" FROM AmazonCredentials amCred where amCred.user=?1")
	public List<AmazonCredentialsDto> getByUser(User user);
	
	@Query("SELECT new com.infor.cloudsuite.dto.AmazonCredentialsDto (amCred.id, amCred.user.id," +
			" amCred.name, amCred.awsKey, amCred.secretKey) " +
			" FROM AmazonCredentials amCred where amCred.user.id=?1")
	public List<AmazonCredentialsDto> getByUserId(Long userId);
	
	@Query("SELECT new com.infor.cloudsuite.dto.AmazonCredentialsDto (amCred.id, amCred.user.id," +
			" amCred.name, amCred.awsKey, amCred.secretKey) " +
			" FROM AmazonCredentials amCred where amCred.awsKey=?1")
	public AmazonCredentialsDto getByAwsKey(String awsKey);
	
	@Query("SELECT new com.infor.cloudsuite.dto.AmazonCredentialsDto (amCred.id, amCred.user.id," +
			" amCred.name, amCred.awsKey, amCred.secretKey) " +
			" FROM AmazonCredentials amCred where amCred.id=?1")
	public AmazonCredentialsDto getById(Long amCredId);
	
	@Query("SELECT new com.infor.cloudsuite.dto.AmazonCredentialsDto (amCred.id, amCred.user.id," +
			" amCred.name, amCred.awsKey, amCred.secretKey) " +
			" FROM AmazonCredentials amCred where amCred.id=?1 AND amCred.user=?2")
	public AmazonCredentialsDto getByIdAndUser(Long amCredId, User user);
	
	public AmazonCredentials findByUserAndId(User user, Long amCredId);
	
	public AmazonCredentials findByUserAndName(User user, String name);
	
	@Modifying
	@Query("DELETE from AmazonCredentials where user=?1 AND id=?2")
	public void deleteByUserAndId(User user, Long amazonCredentialsId);

	@Modifying
	@Query("DELETE from AmazonCredentials where user=?1")
	public void deleteByUser(User user);
}
