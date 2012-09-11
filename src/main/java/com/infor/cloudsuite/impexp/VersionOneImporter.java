package com.infor.cloudsuite.impexp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.entity.*;


@Component
public class VersionOneImporter extends AbstractImporter {
	

	private static final Logger logger=LoggerFactory.getLogger(VersionOneImporter.class);
	
	private enum OpKeyEnum {
		USERS,
		TRIAL_INSTANCES,
		USER_TRACKINGS
    }
	private ArrayList<String> opKeyList=null;

	private boolean runUserTrackingImport(JSONObject outputValue) {
		
		long trackCountRead=0;
		long trackCountAdded=0;
		long trackStart=userTrackingDao.count();
		boolean retValue=false;
		
		JSONArray arrayOfTrackings=getJSONArrayFromImportJSON("exportedUserTrackings");
		if (arrayOfTrackings != null) {
			for (int i=0; i<arrayOfTrackings.length();i++) {
				JSONObject trackingObj=arrayOfTrackings.optJSONObject(i);
				if (trackingObj != null) {
					
					trackCountRead++;
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
						trackCountAdded++;
					} 
				
				} 
				

			}
			userTrackingDao.flush();
			retValue=true;
			


		} 
		
		JSONObject jobj=new JSONObject();
		addToJsonAndIgnore(jobj,"readCount",trackCountRead);
		addToJsonAndIgnore(jobj,"addedCount",trackCountAdded);
		addToJsonAndIgnore(jobj,"dbdiff",(userTrackingDao.count()-trackStart));
		addToJsonAndIgnore(jobj,"success",retValue);
		addToJsonAndIgnore(outputValue,OpKeyEnum.USER_TRACKINGS.name(),jobj);
		
		return retValue;
	}

	
	private boolean runUserImport(JSONObject outputValue) {
		
		long usersRead=0;
		long userCount=0;
		long usersSkipped=0;
		boolean retValue=false;
		long usersStart=userDao.count();
		
		
		JSONArray arrayOfUsers=getJSONArrayFromImportJSON("exportedUsers");
		if (arrayOfUsers != null) {
			
			for (int i=0; i<arrayOfUsers.length();i++) {
									
				JSONObject userObj=arrayOfUsers.optJSONObject(i);
                String username = null;
				if (userObj!=null) {

                    username = userObj.optString("username");
                    usersRead++;
				}
				
				if (userObj != null && username != null && 
						userDao.findByUsername(username)==null) {
				
					
					User user = new User();
					user.setUsername(username);
					
					user.setFirstName(userObj.optString("firstName"));
					user.setLastName(userObj.optString("lastName"));
					
					user.setAddress1(userObj.optString("address1"));
					user.setAddress2(userObj.optString("address2"));
					user.setPhone(userObj.optString("phone"));
					String companyName=userObj.optString("companyName");
					if (companyName != null) {
						Company company=companyDao.findByName(companyName);
						if (company != null) {
							user.setCompany(company);
						}
					}
					user.setCountry(userObj.optString("country"));
					user.setLanguage(userObj.optString("language"));
					user.setInforCustomer(userObj.optBoolean("inforCustomer"));

					try {
						user.setCreatedAt(SDF.parse(userObj.optString("createdAtString")));
					} catch (ParseException e) {
                        user.setCreatedAt(new Date(userObj.optLong("createdAt")));
					}
					
					user.setPassword(userObj.optString("password"));
					
					user.setActive(userObj.optBoolean("active"));
					JSONArray rolesArray=userObj.optJSONArray("roles");
					if (rolesArray != null) {
						for (int j=0; j<rolesArray.length();j++) {
							String role=rolesArray.optString(j);
							try {
								user.getRoles().add(Role.valueOf(role));
							} catch (IllegalArgumentException e) {
                                logger.error("Error adding role to user '"+role+"'..",e);
							}
						}
					}
					
					
					userDao.save(user);
					userDao.flush(); //needed?
					logger.info("processed user with username '"+user.getUsername()+LINE);
					userCount++;
					//add AWS Key
					String awsKey=userObj.optString("awsKey");
					String awsSecretKey=userObj.optString("awsSecretKey");
					if (awsKey != null && awsSecretKey != null && !"".equals(awsKey.trim()) && !"".equals(awsSecretKey.trim())) {
						AmazonCredentials amcred=new AmazonCredentials();
						amcred.setAwsKey(awsKey);
						amcred.setUser(user);
						amcred.setSecretKey(awsSecretKey);
						amcred.setName("Default(V1)");
						amazonCredentialsDao.save(amcred);
						amazonCredentialsDao.flush();
					}
					
					JSONArray uprods=userObj.optJSONArray("userProductDtos");
					if (uprods != null) {
						for (int j=0; j<uprods.length();j++) {
							JSONObject uprod=uprods.optJSONObject(j);
							if (uprod != null) {
								String productShortName=uprod.optString("productShortName");
								if (productShortName != null && !"".equals(productShortName.trim())) {
									Product prod=productDao.findByShortName(productShortName);
									if (prod != null) {
										UserProduct userProduct=new UserProduct(user,prod);
										userProduct.setLaunchAvailable(uprod.optBoolean("launchAvailable"));
										userProduct.setTrialAvailable(uprod.optBoolean("trialAvailable"));
										userProduct.setOwned(userProduct.getLaunchAvailable());
										
										userProductDao.save(userProduct);
										user.getUserProducts().put(prod.getId(), userProduct);

									}
								}
								
								
							}

						}
						userDao.save(user);
						userDao.flush();
					}
					

				} else {
					usersSkipped++;
				}

				
			}

			retValue=true;
			
		}
		JSONObject jobj=new JSONObject();
		addToJsonAndIgnore(jobj,"readCount",usersRead);
		addToJsonAndIgnore(jobj,"skipCount",usersSkipped);
		addToJsonAndIgnore(jobj,"addCount",userCount);
		addToJsonAndIgnore(jobj,"dbdiff",userDao.count()-usersStart);
		addToJsonAndIgnore(jobj,"success",retValue);
		
		addToJsonAndIgnore(outputValue,OpKeyEnum.USERS.name(),jobj);
		
		
		return retValue;
	}
	
	private ProductVersion latestProductVersion(List<ProductVersion> productVersions) {
		ProductVersion productVersion=null;
		if (productVersions==null) {
			return productVersion;
		}
		for (ProductVersion version : productVersions) {
			if (productVersion==null || productVersion.getCreatedAt().before(version.getCreatedAt())) {
				productVersion=version;
			}
		}
		return productVersion;
	}

	private boolean runTrialInstanceImport(JSONObject outputValue) {
		
		JSONArray arrayOfTrials=getJSONArrayFromImportJSON("exportedTrialInstances");
		long trialsAdded=0;
		long trialsRead=0;
		long trialsStart=trialInstanceDao.count();
		long trialEnvironmentsStart=trialEnvironmentDao.count();
		boolean retValue=false;
		
		Set<TrialEnvironment> listOfEnvirosToRemove=new HashSet<>();
		if (arrayOfTrials != null) {
			
			
			Region SA=regionDao.findByShortName("SA");
			
			for (int i=0; i<arrayOfTrials.length();i++) {
				JSONObject trialObj=arrayOfTrials.optJSONObject(i);
				if (trialObj != null ) {
					trialsRead++;
					TrialInstance trialInstance=new TrialInstance();
					Product product=null;
					ProductVersion productVersion=null;
					try {
						product=productDao.findByShortName(trialObj.optString("productShortName"));
						productVersion=latestProductVersion(product.getProductVersions());
					} catch (Exception e) {
						logger.error("Exception encountered processing Product",e);
					}
					if (product != null) {
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
						String environmentId=trialObj.optString("environmentId");
						
						if (environmentId != null) {
							trialInstance.setEnvironmentId(trialObj.optString("environmentId"));
							List<TrialEnvironment> envs=trialEnvironmentDao.findByEnvironmentIdAndProductVersionAndRegion(environmentId, productVersion, SA);
							if (envs.size()==1) {
								listOfEnvirosToRemove.add(envs.get(0));
							} else if (envs.size()>1) {
								List<TrialEnvironment> narrowed=trialEnvironmentDao.findNarrowedByUrl(envs, trialObj.optString("url"));
								if (narrowed.size()==1) {
									listOfEnvirosToRemove.add(narrowed.get(0));
								} else if (narrowed.size()>1) {
									List<TrialEnvironment> narrowed_more=trialEnvironmentDao.findNarrowedByUsername(narrowed, trialObj.optString("trialUsername"));
									if (narrowed_more.size()==1) {
										listOfEnvirosToRemove.add(narrowed_more.get(0));
									} else if (narrowed_more.size()>1) {
										logger.error("After narrowing by region, product, username and url, we still have >1 trial environments to remove--not removing any for environmentId:"+trialObj.optString("environmentId"));
										
									}
								}
							} else {
								logger.error("After narrowing by region, product, and url, we have NO trial environments to remove--not removing any for environmentId:"+trialObj.optString("environmentId"));
								
							}
							
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
						trialInstance.setRegion(SA);	
						trialInstance.setUrl(trialObj.optString("url"));
						
						trialInstanceDao.save(trialInstance);
						trialInstanceDao.flush();
						
						trialsAdded++;
					}
									
				}
				
				
			}

			trialEnvironmentDao.delete(listOfEnvirosToRemove);
			trialEnvironmentDao.flush();

			retValue=true;
		}
		JSONObject jobj=new JSONObject();
		JSONObject instances=new JSONObject();
		addToJsonAndIgnore(instances,"readCount",trialsRead);
		addToJsonAndIgnore(instances,"addedCount",trialsAdded);
		addToJsonAndIgnore(instances,"dbdiff",trialInstanceDao.count()-trialsStart);
		addToJsonAndIgnore(jobj,"instances",instances);
		
		JSONObject enviros=new JSONObject();
		addToJsonAndIgnore(enviros,"targetedForDelete",listOfEnvirosToRemove.size());
		addToJsonAndIgnore(enviros,"actuallyDeleted",trialEnvironmentsStart-trialEnvironmentDao.count());
		addToJsonAndIgnore(jobj,"environments",enviros);
		
		addToJsonAndIgnore(jobj,"success",retValue);
		addToJsonAndIgnore(outputValue,OpKeyEnum.TRIAL_INSTANCES.name(),jobj);
		
		return retValue;
	}

	@Override
	public boolean runImport(String key, JSONObject outputValue) {
		// TODO Auto-generated method stub
		OpKeyEnum keyEnum=null;
		JSONObject me=new JSONObject();
		logger.info("Running import for key:"+key);
		try {
			keyEnum=OpKeyEnum.valueOf(key);
		} catch (IllegalArgumentException e) {
            addToJsonAndIgnore(me,"except",e);
			logger.error("key value passed not valid for this importer:'"+key+"'!",e);
		}
		if (keyEnum ==null) {
			
			addToJsonAndIgnore(me,"key","null");
			addToJsonAndIgnore(me,"success",false);
			addToJsonAndIgnore(outputValue,key,me);
			return false;
			
		} else {
			switch (keyEnum) {
				case USERS: return runUserImport(outputValue);
				case TRIAL_INSTANCES: return runTrialInstanceImport(outputValue);
				case USER_TRACKINGS: return runUserTrackingImport(outputValue);
				default: break;	
	
			}
		
			addToJsonAndIgnore(outputValue,"error","Should NEVER see this!");
		
		}
		addToJsonAndIgnore(me,"success",false);
		addToJsonAndIgnore(outputValue,key,me);
		
		return false;
	}
	@Override
	public ArrayList<String> getOpKeyList() {
		if (this.opKeyList == null) {
			this.opKeyList=new ArrayList<>();
			for (OpKeyEnum key : OpKeyEnum.values()) {
				this.opKeyList.add(key.name());
			}
		}
		return this.opKeyList;
		
	}




}
