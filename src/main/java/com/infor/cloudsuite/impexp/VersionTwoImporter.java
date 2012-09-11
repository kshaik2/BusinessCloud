package com.infor.cloudsuite.impexp;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrackingType;
import com.infor.cloudsuite.entity.TrialEnvironment;
import com.infor.cloudsuite.entity.TrialInstance;
import com.infor.cloudsuite.entity.TrialInstanceType;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.entity.UserTracking;

public class VersionTwoImporter extends VersionTwoSeeder {

	private static final Logger logger=LoggerFactory.getLogger(VersionTwoImporter.class);

	private ArrayList<String> opKeyStrings=null;

	private enum OpKeyEnum {
		USERS,
		USER_TRACKINGS,
		AMAZON_CREDENTIALS,
		TRIAL_ENVIRONMENTS,		
		TRIAL_INSTANCES,
		DEPLOYMENTS
    }
		
	@Override
	public ArrayList<String> getOpKeyList() {
		if (this.opKeyStrings==null) {
			this.opKeyStrings=new ArrayList<>();
			for (OpKeyEnum key : OpKeyEnum.values()) {
				this.opKeyStrings.add(key.name());
			}
		}
		
		return this.opKeyStrings;
	}


	@Override
	public void postInitSetup() {
		
		setOverwriteConflicts(getBooleanFromImportJSON("overwriteConflicts",false));
	}
		
	
	private boolean runUserTrackingImport(JSONObject outputValue) {
		
		tracker.reset().set("startCount(db)",userTrackingDao.count());
		
		boolean success;
		JSONObject jobj=new JSONObject();
		
		JSONArray arrayOfTrackings=getJSONArrayFromImportJSON("exportedUserTrackings");
		if (arrayOfTrackings != null) {
			for (int i=0; i<arrayOfTrackings.length();i++) {
				JSONObject trackingObj=arrayOfTrackings.optJSONObject(i);
				if (trackingObj != null) {
					
					tracker.increment("dtoRead");
					
					User user=userDao.findByUsername(trackingObj.optString("username"));
					if (user != null) {
						UserTracking userTracking=new UserTracking();
						userTracking.setUser(user);
						userTracking.setOtherData(trackingObj.optString("otherData"));
						userTracking.setTargetObject(trackingObj.optLong("targetObject"));
						try {
							TrackingType type=TrackingType.valueOf(trackingObj.optString("trackingType"));
							userTracking.setTrackingType(type);
						} catch (IllegalArgumentException ignored) {

						}
						
						Date timestamp;
						try {
							timestamp=SDF.parse(trackingObj.optString("timestampString"));
						} catch (ParseException e) {
                            try {
								timestamp=new Date(trackingObj.getLong("timestamp"));
							} catch (JSONException e1) {
                                timestamp=new Date(-1L);
							}
						}
						userTracking.setTimestamp(timestamp);
						userTrackingDao.save(userTracking);
						tracker.increment("added");
					} 
				
				} 
				

			}
			userTrackingDao.flush();
			success=true;
			


		} else {
			addToJsonAndIgnore(jobj,"error","Null array for user trackings!");
			success=false;
		}
		
		tracker.set("endCount(db)", userTrackingDao.count());
		
        tracker.setSuccess(success);
		tracker.markJsonObject(jobj);
		
		addToJsonAndIgnore(outputValue,OpKeyEnum.USER_TRACKINGS.name(),jobj);

		return success;
	}
	
	
	private boolean runTrialInstanceImport(JSONObject outputValue) {
		//different from version one import.  
		
		JSONArray arrayOfTrials=getJSONArrayFromImportJSON("exportedTrialInstances");
		long trialsAdded=0;
		long trialsRead=0;
		long trialsStart=trialInstanceDao.count();
		long trialEnvironmentsStart=trialEnvironmentDao.count();
		boolean retValue=false;
		Set<TrialEnvironment> envirosToRemove=new HashSet<>();
		if (arrayOfTrials != null) {
			
//			Region SA=regionDao.findByShortName("SA");
			
			for (int i=0; i<arrayOfTrials.length();i++) {
				JSONObject trialObj=arrayOfTrials.optJSONObject(i);
				if (trialObj != null ) {
					trialsRead++;
					TrialInstance trialInstance=new TrialInstance();
					Region region=null;
					try {
						region=regionDao.findByShortName(trialObj.optString("regionShortName"));
					} catch (Exception e) {
						//ignore
					}
					
					ProductVersion productVersion=null;
					try {
						productVersion=productVersionDao.findByProductAndName(productDao.findByShortName(trialObj.optString("productShortName")),trialObj.optString("productVersionName"));
					} catch (Exception e) {
						//ignore
					}
					String environmentId=trialObj.optString("environmentId");
					
					if (productVersion != null && region != null && environmentId !=null) {
					
						trialInstance.setProductVersion(productVersion);
						Date date;
						try {
							date=SDF.parse(trialObj.optString("createdAtString"));
						} catch (ParseException e) {
                            try {
								date=new Date(trialObj.getLong("createdAt"));
							} catch (JSONException e1) {
                                date=new Date();
							}
							
						}
						trialInstance.setCreatedAt(date);
						try {
							date=SDF.parse(trialObj.optString("updatedAtString"));
						} catch (ParseException e) {
                            try {
								date=new Date(trialObj.getLong("updatedAt"));
							} catch (JSONException e1) {
                                date=new Date();
							}
							
						}
						trialInstance.setUpdatedAt(date);
						
						try {
							date=SDF.parse(trialObj.optString("expirationDateString"));
						} catch (ParseException e) {
                            try {
								date=new Date(trialObj.getLong("expirationDate"));
							} catch (JSONException e1) {
                                date=new Date();
							}
						}
						
						trialInstance.setExpirationDate(date);
						
						trialInstance.setDomain(trialObj.optString("domain"));
						trialInstance.setUsername(trialObj.optString("trialUsername"));
						try {
							User user=userDao.findByUsername(trialObj.optString("username"));
							trialInstance.setUser(user);
						} catch (Exception e) {
							//ignore?
						}
						
						String password=trialObj.optString("trialPassword");
						if (password != null && password.endsWith("\r")) {
							password=password.substring(0,password.length()-1);
						}
						trialInstance.setPassword(password);
						TrialEnvironment toRemove;
						List<TrialEnvironment> toRemoveList=trialEnvironmentDao.findByEnvironmentIdAndProductVersionAndRegion(environmentId, productVersion, region);
						if (toRemoveList.size()==0) {
							toRemove=null;
						} else if (toRemoveList.size()==1) {
							toRemove=toRemoveList.get(0);
						} else {
							toRemove=narrowEnvironmentsToRemove(toRemoveList,trialObj);
						}

							
					
						if (toRemove != null) {
							envirosToRemove.add(toRemove);
						}

						
						TrialInstanceType type;
						try{
							type=TrialInstanceType.valueOf(trialObj.optString("type"));
						} catch (IllegalArgumentException e) {
                            if (trialInstance.getUser() == null) {
								type=TrialInstanceType.DOMAIN;
							} else {
								type=TrialInstanceType.USER;
							}
						}
						
						trialInstance.setType(type);
						trialInstance.setGuid(trialObj.optString("guid"));
						trialInstance.setRegion(region);	
						trialInstance.setUrl(trialObj.optString("url"));
						
						trialInstanceDao.save(trialInstance);
						trialInstanceDao.flush();
						
						trialsAdded++;
					}
									
				}
				
				
			}
			
			trialEnvironmentDao.deleteInBatch(envirosToRemove);
			trialEnvironmentDao.flush();

			retValue=true;
		}
		JSONObject jobj=new JSONObject();
		JSONObject instances=new JSONObject();
		addToJsonAndIgnore(instances,"readCount",trialsRead);
		addToJsonAndIgnore(instances,"addedCount",trialsAdded);
		addToJsonAndIgnore(instances,"dbdiff",trialEnvironmentDao.count()-trialsStart);
		addToJsonAndIgnore(jobj,"instances",instances);
		
		JSONObject enviros=new JSONObject();
		addToJsonAndIgnore(enviros,"targetedForDelete",envirosToRemove.size());
		addToJsonAndIgnore(enviros,"actuallyDeleted",trialEnvironmentsStart-trialEnvironmentDao.count());
		addToJsonAndIgnore(jobj,"environments",enviros);
		
		addToJsonAndIgnore(jobj,"success",retValue);
		addToJsonAndIgnore(outputValue,"trialInstances",jobj);
		
		return retValue;
	}

	private TrialEnvironment narrowEnvironmentsToRemove(List<TrialEnvironment> toRemoveList, JSONObject trialObj) {
		
		//need to re-do this and below method and probably make this class extend the seeder.
		String url=trialObj.optString("url");
		List<TrialEnvironment> newList=null;
		if (url != null) {
			newList=trialEnvironmentDao.findNarrowedByUrl(toRemoveList, url);
			if (newList.size()==0) {
				newList=toRemoveList;
			}
			if (newList.size()==1) {
				return newList.get(0);
			}
			
		}
		
		String username=trialObj.optString("username");
		List<TrialEnvironment> lastList;
		if (username != null) {
			lastList=trialEnvironmentDao.findNarrowedByUsername(newList, username);
			if (lastList.size()==1) {
				return lastList.get(0);
			}
			if (lastList.size() > 1) {
				logger.info("Size of list still >1.  Returning null");

			} else {
				logger.info("Empty from narrowing ... returning null!");
			}
			
			
			
		}
		
		return null;
	}
			


	@SuppressWarnings("UnusedParameters")
    private boolean runDeploymentImport(JSONObject outputValue) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean runImport(String key, JSONObject outputValue) {		
		try {
		return runImport(OpKeyEnum.valueOf(key),outputValue);
		} catch (IllegalArgumentException e) {
            JSONObject obj=new JSONObject();
			addToJsonAndIgnore(obj,"success",false);
			addToJsonAndIgnore(obj,"error",e);
			if (key != null) {
				addToJsonAndIgnore(outputValue,key,obj);
			} else {
				addToJsonAndIgnore(outputValue,"KEY-MISSING-"+keyMissingAppend,obj);
				keyMissingAppend++;
			}
		}

		return false;
	}

	private boolean runImport(OpKeyEnum keyEnum, JSONObject outputValue) {

		JSONObject me=new JSONObject();
		logger.info("Preparing to switch on running import for key:"+keyEnum);
		switch (keyEnum) {
				case USERS: return importUsers(outputValue);
				case AMAZON_CREDENTIALS: return importAmazonCredentials(outputValue);
				case TRIAL_ENVIRONMENTS: return importTrialEnvironments(outputValue);
				//above are parent class implemented, below are in here
				case TRIAL_INSTANCES: return runTrialInstanceImport(outputValue);
				case USER_TRACKINGS: return runUserTrackingImport(outputValue);
				case DEPLOYMENTS: return runDeploymentImport(outputValue);
				default: break;	
	
		}
		
		addToJsonAndIgnore(me,"error","Should NEVER see this!--keyEnum '"+keyEnum+"' not in switch!");
		addToJsonAndIgnore(me,"success",false);
		addToJsonAndIgnore(outputValue,keyEnum.name(),me);
		
		return false;
	}




}
