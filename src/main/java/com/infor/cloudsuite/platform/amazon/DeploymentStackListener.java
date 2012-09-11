package com.infor.cloudsuite.platform.amazon;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.DeploymentStackLogDao;
import com.infor.cloudsuite.dto.DeploymentStackUpdateDto;
import com.infor.cloudsuite.entity.DeploymentStack;
import com.infor.cloudsuite.entity.DeploymentStackLog;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.entity.DeploymentStatus;

@Component
public class DeploymentStackListener{
	private final Logger logger=LoggerFactory.getLogger(DeploymentStackListener.class);
	@Resource
	private DeploymentStackLogDao logDao;
	@Resource
	private DeploymentStackDao stackDao;
	
	
	public /*Future<Boolean>*/void logAction(DeploymentStackUpdateDto dto) {
		logger.info("Log action called for:"+dto.getAction());
		DeploymentStack stack=null;
		if (dto.getVpcId()!=null && !"".equals(dto.getVpcId().trim())) {
			stack=stackDao.findByVpcId(dto.getVpcId());
		}
		if (stack==null && (dto.getDeploymentStackId() !=null)){
			stack=stackDao.findById(dto.getDeploymentStackId());
		}
		
		Long stackId=-1L;
		if (stack != null) {
			stackId=stack.getId();
		}
		
		DeploymentStackLog log=new DeploymentStackLog();

		log.setVpcId(dto.getVpcId());
		log.setCreatedAt(new Date());
		log.setDeploymentStackId(stackId);
		log.setLogAction(dto.getAction());
		DeploymentState state=dto.getState();
		DeploymentStatus status=dto.getStatus();
		
		log.setState(state);
		log.setStatus(status);
		String message = dto.getMessage();
        if (message.length() > 254){
            message = message.substring(0, 250) + "...";
        }
        log.setMessage(message);
		
		logDao.save(log);
		logDao.flush();
		
		if (stack == null) {
			return; //new AsyncResult<Boolean>(false);
		}

		logger.info("Stack not null, performing action.");
		boolean updated=false;
		switch (dto.getAction()) {
			case UPDATE_ELASTIC_IP: {
				stack.setElasticIp(dto.getMessage());
				stack.setUrl("rdp://"+dto.getMessage());
				updated=true;
				break;
			}
			case SET_VPC_ID: {
				stack.setVpcId(dto.getVpcId());
				updated=true;
				break;
			}
			
			
			default: {
				if (state!= null) {
					stack.setDeploymentState(state);
					updated=true;
				}
				if (status != null) {
					
					stack.setDeploymentStatus(status);
					updated=true;
				}

				break;
			}
		
		}

		if (updated) {
			updateStack(stack);
		}
		
		logger.info("Log action completed for:"+dto.getAction());
		//return new AsyncResult<Boolean>(true);
	}
	
	@Transactional
	void updateStack(DeploymentStack stack) {

		stack.setUpdatedAt(new Date());
		logger.info("Calling stackDao.save()");
		stackDao.save(stack);
		logger.info("Calling stackDao.flush()");
		stackDao.flush();
	}

	public void flush() throws Exception {
		
		//for the children
		
	}
}
