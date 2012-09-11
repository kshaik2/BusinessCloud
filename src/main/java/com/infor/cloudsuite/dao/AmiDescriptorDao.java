package com.infor.cloudsuite.dao;

import com.infor.cloudsuite.entity.AmiDescriptor;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

public interface AmiDescriptorDao extends ExtJpaRepository<AmiDescriptor, Long> {

	public AmiDescriptor findByName(String name);

}
