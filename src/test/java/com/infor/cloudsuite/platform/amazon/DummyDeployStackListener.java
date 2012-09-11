package com.infor.cloudsuite.platform.amazon;

import com.infor.cloudsuite.dto.DeploymentStackUpdateDto;

import java.util.ArrayList;


public class DummyDeployStackListener extends DeploymentStackListener{

	private DeploymentStackListener dsl;
	
	public DummyDeployStackListener() {

	}

	public DummyDeployStackListener(DeploymentStackListener dsl) {
		this.dsl=dsl;
	}

	private final ArrayList<DeploymentStackUpdateDto> dtos=new ArrayList<DeploymentStackUpdateDto>();
	
	
	public void logAction(DeploymentStackUpdateDto dto) {
		
		synchronized(dtos) {
			dtos.add(dto);
		}
		
	}
	
	
	public void flush() throws Exception{

		synchronized(dtos) {
			for (DeploymentStackUpdateDto dto : dtos) {
				dsl.logAction(dto);
			}
		dtos.clear();
		}
	}


	public ArrayList<DeploymentStackUpdateDto> getDtos() {
		return dtos;
	}


	public DeploymentStackListener getDsl() {
		return dsl;
	}


	public void setDsl(DeploymentStackListener dsl) {
		this.dsl = dsl;
	}
	
}