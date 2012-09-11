package com.infor.cloudsuite.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.DeploymentStackDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductVersionDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.DeploymentType;
import com.infor.cloudsuite.dto.dashboard.DashboardDto;
import com.infor.cloudsuite.dto.dashboard.DashboardIntervalDto;
import com.infor.cloudsuite.dto.dashboard.DashboardProductTotalDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingMonthDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingPeriodDto;
import com.infor.cloudsuite.dto.dashboard.DashboardRollingPeriodRequestDto;
import com.infor.cloudsuite.dto.db.ProductCountDto;
import com.infor.cloudsuite.entity.DeploymentState;
import com.infor.cloudsuite.platform.CSWebApplicationException;

@Path("/dashboard")
@Service
public class DashboardService {
	private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    @Resource
    private ProductDao productDao;
    @Resource
    private UserDao userDao;
	@Resource
    private TrialInstanceDao trialInstanceDao;
	@Resource
    private DeploymentStackDao deploymentStackDao; 
	@Resource
	private ProductVersionDao productVersionDao;
	
	private final static SimpleDateFormat MONTH=new SimpleDateFormat("MMM");
	private final static SimpleDateFormat DB_DATE=new SimpleDateFormat("yyyy-MM-dd");
    @GET
    @Path("/rollingDeployments")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public DashboardRollingDto getRollingList(@QueryParam("months") Integer months) {
    	if (months==null) {
    		months=6;
    	}
    	Date today=new Date();
    	
    	GregorianCalendar calendar=new GregorianCalendar();
    	calendar.setTime(today);
    	//change to first day of month
    	calendar.set(GregorianCalendar.DATE, 1);
    	zeroOutTime(calendar);
    	
    	Stack<Date> stack=new Stack<>();
    	stack.push(calendar.getTime());
    	for (int i=1;i<months;i++) {
    		calendar.add(GregorianCalendar.MONTH, -1);
    		stack.push(calendar.getTime());
    	}
    	
    	
    	DashboardRollingDto dto=new DashboardRollingDto();
    	dto.setMonthCount(months);
    	int index=1;
    	while (!stack.isEmpty()) {
    		Date date=stack.pop();
    		dto.getRollingDeployments().add(getRollingMonth(date,index));
    		index++;
    	}
    	
    	return dto;
    }
    
    private void zeroOutTime(GregorianCalendar calendar) {
    	calendar.set(GregorianCalendar.HOUR,0);
    	calendar.set(GregorianCalendar.MINUTE, 0);
    	calendar.set(GregorianCalendar.SECOND,0);
    	calendar.set(GregorianCalendar.MILLISECOND,0);
    	calendar.set(GregorianCalendar.HOUR_OF_DAY,0);
    }
    
    private Date getOneMonthLater(Date date) {
    	GregorianCalendar calendar=new GregorianCalendar();
    	calendar.setTime(date);
    	calendar.add(GregorianCalendar.MONTH,1);
    	return calendar.getTime();
    }
    
    private HashMap<Long,DashboardProductTotalDto> getPopulatedTotalsMap(List<ProductCountDto> trials, List<ProductCountDto> deploys) {
    	HashMap<Long,DashboardProductTotalDto> prodTotals=new HashMap<>();

        addDashboardTotals(trials, prodTotals, DeploymentType.INFOR24);
        addDashboardTotals(deploys, prodTotals, DeploymentType.AWS);

        return prodTotals;
    }

    private void addDashboardTotals(List<ProductCountDto> productCounts, HashMap<Long, DashboardProductTotalDto> prodTotals, DeploymentType type) {
        for (ProductCountDto prodCount : productCounts) {
            DashboardProductTotalDto theDto=prodTotals.get(prodCount.getProductId());
            if (theDto==null) {
                theDto=new DashboardProductTotalDto();
                theDto.setProductId(prodCount.getProductId());
                prodTotals.put(prodCount.getProductId(), theDto);
            }
            switch (type){
                case INFOR24:
                    theDto.setInfor24(prodCount.getCount());
                    break;
                case AWS:
                    theDto.setAws(prodCount.getCount());
                    break;
                default:
            }
        }
    }

    private DashboardRollingPeriodDto getRollingPeriod(Date firstDate, Date secondDate, FILTER_TYPE type, String periodName) {
    
    	DashboardRollingPeriodDto period=new DashboardRollingPeriodDto();
    	List<ProductCountDto> trials=null;
    	List<ProductCountDto> deploys=null;
    	Long infor24=null;
    	Long aws=null;
    	Date now=new Date();
    	switch (type) {
    	case ALL:
    		trials=trialInstanceDao.countByCreatedAtInPeriod(firstDate, secondDate);
    		deploys=deploymentStackDao.countByCreatedAtInPeriod(firstDate,secondDate);
    		infor24=trialInstanceDao.countCreatedAtBetween(firstDate,secondDate);
    		aws=deploymentStackDao.countCreatedAtBetween(firstDate,secondDate);
    		break;
    	
    	case ACTIVE_ALL:
    		trials=trialInstanceDao.countByCreatedAtInPeriodExpiresAfter(firstDate, secondDate,now);
    		deploys=deploymentStackDao.countByCreatedAtInPeriodAndDeploymentStateIsNot(firstDate,secondDate,DeploymentState.DELETED);
    		infor24=trialInstanceDao.countCreatedAtBetweenAndExpiresAfter(firstDate,secondDate,now);
    		aws=deploymentStackDao.countCreatedAtBetweenAndDeploymentStateIsNot(firstDate,secondDate,DeploymentState.DELETED);
    		
    		break;
    	case ACTIVE_ALL_RUNNING:
    		trials=trialInstanceDao.countByCreatedAtInPeriodExpiresAfter(firstDate, secondDate,now);
    		deploys=deploymentStackDao.countByCreatedAtInPeriodAndDeploymentStateIs(firstDate,secondDate,DeploymentState.AVAILABLE);
    		infor24=trialInstanceDao.countCreatedAtBetweenAndExpiresAfter(firstDate,secondDate,now);
    		aws=deploymentStackDao.countCreatedAtBetweenAndDeploymentStateIs(firstDate,secondDate,DeploymentState.AVAILABLE);
    		
    		break;
    	default:break;
    	}
    	
    	
    	
      	HashMap<Long,DashboardProductTotalDto> prodTotals=getPopulatedTotalsMap(trials,deploys);
    	for (DashboardProductTotalDto prodTotal : prodTotals.values()) {
    		period.getProducts().add(prodTotal);
    	}
    	
    	period.setName(periodName == null ? "Custom period" : periodName);
    	period.setInfor24(infor24);
    	period.setAws(aws);
    	period.setPeriodStart(firstDate);
    	period.setPeriodEnd(secondDate);
    	return period;
    	
    }
    private DashboardRollingMonthDto getRollingMonth(Date firstOfMonth, int index) {
    	DashboardRollingMonthDto month=new DashboardRollingMonthDto();
    	Date nextMonth=getOneMonthLater(firstOfMonth);
    	List<ProductCountDto> trials=trialInstanceDao.countByCreatedAtInPeriod(firstOfMonth,nextMonth);
    	List<ProductCountDto> deploys=deploymentStackDao.countByCreatedAtInPeriod(firstOfMonth,nextMonth);
    	HashMap<Long,DashboardProductTotalDto> prodTotals=this.getPopulatedTotalsMap(trials, deploys);

    	for (DashboardProductTotalDto prodTotal : prodTotals.values()) {
    		month.getProducts().add(prodTotal);
    	}
    	
    	month.setMonth(index);
    	month.setPeriodStart(firstOfMonth);
    	month.setPeriodEnd(nextMonth);
    	month.setName(MONTH.format(firstOfMonth));
    	
    	month.setAws(deploymentStackDao.countCreatedAtBetween(firstOfMonth, nextMonth));
    	month.setInfor24(trialInstanceDao.countCreatedAtBetween(firstOfMonth, nextMonth));
    	
    	return month;
    }
    
    @GET
    @Path("/tabular")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public DashboardDto getAdminDashboard() {
    	
    	DashboardDto dto=new DashboardDto();
    	
    	dto.setUsersTotal(userDao.countByActive(true));
    	dto.setUsersWithInfor24(userDao.countByActiveWithInfor24(true));
    	dto.setUsersWithAws(userDao.countByActiveWithAWS(true));
    	Date now=new Date();
    	dto.setActiveInfor24(trialInstanceDao.countAllByExpirationDateAfter(now));
    	dto.setActiveAws(deploymentStackDao.getCountAllExcept(DeploymentState.DELETED));
    	
    	for (ProductCountDto countDto : trialInstanceDao.countByExpirationDateAfter(now)) {
    		
    		increment(dto.getActiveInfor24ByProduct(),countDto);
    		
    	}
    	
    	for (ProductCountDto countDto : deploymentStackDao.countAllExcept(DeploymentState.DELETED)) {
    		increment(dto.getActiveAwsByProduct(),countDto);
    	}
    	
    	dto.setAllTimeAws(deploymentStackDao.count());
    	dto.setAllTimeInfor24(trialInstanceDao.countAllNonDomain());
    	
    	
    	dto.setDay(getDashboardInterval(1));
    	dto.setWeek(getDashboardInterval(7));
    	dto.setMonth(getDashboardInterval(31));
    	
    	
    	return dto;
    }
    
    private DashboardIntervalDto getDashboardInterval(int days) {
    	
    	Date now=new Date();
    	GregorianCalendar cal=new GregorianCalendar();
    	cal.setTime(now);
    	cal.add(GregorianCalendar.DATE,(days*-1));
    	Date then=cal.getTime();
    	
    	DashboardIntervalDto dto=new DashboardIntervalDto();
    	dto.setDays(days);
    	for (ProductCountDto countDto : getAwsDeploymentCountSince(then)) {
    		increment(dto.getAllDeploymentsByProduct(),countDto);
    		increment(dto.getAwsDeploymentsByProduct(),countDto);
    	}
    	for (ProductCountDto countDto : getInfor24DeploymentCountSince(then)) {
    		increment(dto.getAllDeploymentsByProduct(),countDto);
    		increment(dto.getInfor24DeploymentsByProduct(),countDto);
    	}

    	dto.setNewUsersCount(userDao.countByActiveAndCreatedAt(true,then));
    	dto.setFrom(then);
    	dto.setUntil(now);
    	return dto;
    }
    private void increment(Map<Long,Long> byProduct, ProductCountDto countDto) {
    	Long val=byProduct.get(countDto.getProductId());
    	if (val==null) {
    		val=0L;
    	}
    	val+=countDto.getCount();
    	byProduct.put(countDto.getProductId(), val);
    	
    }
    
    private List<ProductCountDto> getAwsDeploymentCountSince(Date date) {
    
    	return deploymentStackDao.countByCreatedAtAfter(date);
    	
    }
    
    private List<ProductCountDto> getInfor24DeploymentCountSince(Date date) {
    	return trialInstanceDao.countByCreatedAtAfter(date);
    }
    
    @GET
    @Path("/periodOfDeployments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public DashboardRollingPeriodDto getPeriodOfDeployments(@QueryParam("filter") String filter, @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate, @QueryParam("name") String name) {

    	if (filter==null) {
    		filter="ALL";
    	}
    	if (name==null) {
    		name="Unnamed";
    	}
    	
    	
    	FILTER_TYPE filterType;
    	try {
    		filterType=FILTER_TYPE.valueOf(filter.toUpperCase());
    	} catch (IllegalArgumentException e) {
            logger.error("Error encountered when getPeriodOfD",e);
    		throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE,"Error with filter!:"+e);
    	}
    	
    	
    	DashboardRollingPeriodRequestDto dto=new DashboardRollingPeriodRequestDto();
    	if (startDate != null) {
    		dto.setStartDate(startDate);
    	}
    	if (endDate != null) {
    		dto.setEndDate(endDate);
    	}
    	
    	dto.setFilter(filter);
    	dto.setName(name);
    	
    	return this.getRollingPeriod(dto.getStartDate(),dto.getEndDate(),filterType,name);
    	
    }

	private enum FILTER_TYPE {
    	ALL,
    	ACTIVE_ALL,
    	ACTIVE_ALL_RUNNING
    }
}
