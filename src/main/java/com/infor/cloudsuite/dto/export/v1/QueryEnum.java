package com.infor.cloudsuite.dto.export.v1;

public enum QueryEnum {


	USER_QUERY("select inforCustomer, awsKey, awsSecretKey, concat(createdAt,' ',@@global.system_time_zone), password, address1, address2, phone,active,awsAccountNumber,companyName,username,country,firstName,lastName,language from User"),
	USER_PRODUCT_QUERY("select u.username,prod.shortName,upr.trialAvailable,upr.launchAvailable from cloudsuite.UserProduct upr JOIN Product prod ON upr.product_id=prod.id JOIN User u ON upr.user_id=u.id WHERE u.username=? "),
	USER_ROLE_QUERY("select u.username,r.roles from User u JOIN user_roles r ON r.User_id=u.id where u.username=?"),
	USER_TRACKING_QUERY("select u.username, ut.otherData, ut.targetObject, ut.trackingType,concat(ut.timestamp,' ',@@global.system_time_zone) from UserTracking ut JOIN User u ON u.id=ut.user_id"),
	TRIAL_INSTANCE_QUERY("select p.shortName, ti.domain, ti.username,ti.guid,ti.environmentId,concat(ti.expirationDate,' ',@@global.system_time_zone),ti.password,u.username,ti.type, ti.url from TrialInstance ti JOIN Product p ON p.id=ti.product_id JOIN User u ON u.id=ti.user_id");/*,
	TRIAL_ENVIRONMENT_QUERY("select "),
	DEPLOYMENT_QUERY("select ");*/
	
	
	
	private String queryString;
	
	QueryEnum(String queryString) {
		this.queryString=queryString;
	}
	
	public String getQueryString() {
		return this.queryString;
	}
}
