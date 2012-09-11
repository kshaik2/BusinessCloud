package com.infor.cloudsuite.dto.dashboard;

import java.util.HashMap;
import java.util.Map;

public class DashboardDto {

	private Long usersTotal=0L;
    private Long usersWithAws=0L;
    private Long usersWithInfor24=0L;
    private Map<Long, Long> activeInfor24ByProduct;
    private Map<Long, Long> activeAwsByProduct;
	
	
    private Long activeInfor24=0L;
    private Long activeAws=0L;
    private Long allTimeInfor24=0L;
    private Long allTimeAws=0L;
	
    private DashboardIntervalDto day;
    private DashboardIntervalDto week;
    private DashboardIntervalDto month;

	public Long getUsersTotal() {
		return usersTotal;
	}

	public void setUsersTotal(Long usersTotal) {
		this.usersTotal = usersTotal;
	}

	public Long getUsersWithAws() {
		return usersWithAws;
	}

	public void setUsersWithAws(Long usersWithAws) {
		this.usersWithAws = usersWithAws;
	}

	public Long getUsersWithInfor24() {
		return usersWithInfor24;
	}

	public void setUsersWithInfor24(Long usersWithInfor24) {
		this.usersWithInfor24 = usersWithInfor24;
	}


	public Map<Long, Long> getActiveInfor24ByProduct() {
		if (activeInfor24ByProduct==null) {
			activeInfor24ByProduct=new HashMap<>();
		}
		return activeInfor24ByProduct;
	}

	public void setActiveInfor24ByProduct(Map<Long, Long> activeInfor24ByProduct) {
		this.activeInfor24ByProduct = activeInfor24ByProduct;
	}

	public Long getActiveInfor24() {
		return activeInfor24;
	}

	public void setActiveInfor24(Long activeInfor24) {
		this.activeInfor24 = activeInfor24;
	}

	public DashboardIntervalDto getDay() {
		return day;
	}

	public void setDay(DashboardIntervalDto day) {
		this.day = day;
	}

	public DashboardIntervalDto getWeek() {
		return week;
	}

	public void setWeek(DashboardIntervalDto week) {
		this.week = week;
	}

	public DashboardIntervalDto getMonth() {
		return month;
	}

	public void setMonth(DashboardIntervalDto month) {
		this.month = month;
	}

	public Map<Long, Long> getActiveAwsByProduct() {
		if (activeAwsByProduct == null) {
			activeAwsByProduct=new HashMap<>();
		}
		return activeAwsByProduct;
	}

	public void setActiveAwsByProduct(Map<Long, Long> activeAwsByProduct) {
		this.activeAwsByProduct = activeAwsByProduct;
	}

	public Long getActiveAws() {
		return activeAws;
	}

	public void setActiveAws(Long activeAws) {
		this.activeAws = activeAws;
	}

	public Long getAllTimeInfor24() {
		return allTimeInfor24;
	}

	public void setAllTimeInfor24(Long allTimeInfor24) {
		this.allTimeInfor24 = allTimeInfor24;
	}

	public Long getAllTimeAws() {
		return allTimeAws;
	}

	public void setAllTimeAws(Long allTimeAws) {
		this.allTimeAws = allTimeAws;
	}

}
