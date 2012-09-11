package com.infor.cloudsuite.platform.components;

import org.springframework.stereotype.Component;

/**
 * User: bcrow
 * Date: 12/5/11 12:20 PM
 */
@Component
public class SettingsProvider {
    
    private boolean productionMode;
    private boolean trialRecycleInforDomain;
    private boolean trialRecycleAllDomains;
    private boolean excludeInforEmailsFromLeads;
    private int daysUntilValidationCleaned;

	private boolean forceUpdateWithSeedData;
    private String seedFileName;
    private int daysAfterTrialRequestStaleNotification;
    private int scheduleAdvancedEmailWarningTime;
    private int notificationTrialExpirationWarning;

    private String versionOneProductionUsername;
    private String versionOneProductionPassword;
    private String versionOneProductionConnectionString;
    
    public boolean isProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    public boolean isTrialRecycleInforDomain() {
        return trialRecycleInforDomain;
    }

    public void setTrialRecycleInforDomain(boolean trialRecycleInforDomain) {
        this.trialRecycleInforDomain = trialRecycleInforDomain;
    }

    public boolean isTrialRecycleAllDomains() {
        return trialRecycleAllDomains;
    }

    public void setTrialRecycleAllDomains(boolean trialRecycleAllDomains) {
        this.trialRecycleAllDomains = trialRecycleAllDomains;
    }

	public boolean isExcludeInforEmailsFromLeads() {
		return excludeInforEmailsFromLeads;
	}

	public void setExcludeInforEmailsFromLeads(boolean excludeInforEmailsFromLeads) {
		this.excludeInforEmailsFromLeads = excludeInforEmailsFromLeads;
	}

	public int getDaysUntilValidationCleaned() {
		return daysUntilValidationCleaned;
	}

	public void setDaysUntilValidationCleaned(int daysUntilValidationCleaned) {
		this.daysUntilValidationCleaned = daysUntilValidationCleaned;
	}

	public boolean isForceUpdateWithSeedData() {
		return forceUpdateWithSeedData;
	}

	public void setForceUpdateWithSeedData(boolean forceUpdateWithSeedData) {
		this.forceUpdateWithSeedData = forceUpdateWithSeedData;
	}

	public String getSeedFileName() {
		return seedFileName;
	}

	public void setSeedFileName(String seedFileName) {
		this.seedFileName = seedFileName;
	}

	public String getVersionOneProductionUsername() {
		return versionOneProductionUsername;
	}

	public void setVersionOneProductionUsername(String versionOneProductionUsername) {
		this.versionOneProductionUsername = versionOneProductionUsername;
	}

	public String getVersionOneProductionPassword() {
		return versionOneProductionPassword;
	}

	public void setVersionOneProductionPassword(String versionOneProductionPassword) {
		this.versionOneProductionPassword = versionOneProductionPassword;
	}

	public String getVersionOneProductionConnectionString() {
		return versionOneProductionConnectionString;
	}

	public void setVersionOneProductionConnectionString(
			String versionOneProductionConnectionString) {
		this.versionOneProductionConnectionString = versionOneProductionConnectionString;
	}
	
	public int getDaysAfterTrialRequestStaleNotification() {
		return daysAfterTrialRequestStaleNotification;
	}

	public void setDaysAfterTrialRequestStaleNotification(
			int daysAfterTrialRequestStaleNotification) {
		this.daysAfterTrialRequestStaleNotification = daysAfterTrialRequestStaleNotification;
	}

    public int getScheduleAdvancedEmailWarningTime() {
        return scheduleAdvancedEmailWarningTime;
    }

    public void setScheduleAdvancedEmailWarningTime(
            int scheduleAdvancedEmailWarningTime) {
        this.scheduleAdvancedEmailWarningTime = scheduleAdvancedEmailWarningTime;
    }

    public int getNotificationTrialExpirationWarning() {
        return notificationTrialExpirationWarning;
    }

    public void setNotificationTrialExpirationWarning(int notificationExpirationWarning) {
        this.notificationTrialExpirationWarning = notificationExpirationWarning;
    }
	
}
