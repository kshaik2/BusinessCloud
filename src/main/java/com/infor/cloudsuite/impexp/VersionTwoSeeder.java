package com.infor.cloudsuite.impexp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.infor.cloudsuite.dto.DeploymentType;
import com.infor.cloudsuite.dto.export.v2.*;
import com.infor.cloudsuite.dto.export.v3.ExportAmiDescriptorDto;
import com.infor.cloudsuite.dto.export.v3.ExportCompanyDto;
import com.infor.cloudsuite.dto.export.v3.ExportIndustryDto;
import com.infor.cloudsuite.dto.export.v3.ExportProductVersionDto;
import com.infor.cloudsuite.entity.*;

public class VersionTwoSeeder extends AbstractImporter {
	private boolean DEFAULT_OVERWRITE_CONFLICTS=true;
	private final Logger logger=LoggerFactory.getLogger(VersionTwoSeeder.class);
	protected int keyMissingAppend=0;
	private ArrayList<String> opKeyStrings=null;
	private enum OpKeyEnum {
		REGIONS,
		AMI_DESCRIPTORS,
		PRODUCTS,
		PRODUCT_DESCRIPTIONS,
		TRIAL_PRODUCT_CHILDREN,
		TRIAL_ENVIRONMENTS,
		INDUSTRIES,
		COMPANIES,
		USERS,
		AMAZON_CREDENTIALS
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

		setOverwriteConflicts(getBooleanFromImportJSON("overwriteConflicts",DEFAULT_OVERWRITE_CONFLICTS));
		
	}


	private boolean runImport(OpKeyEnum opKey, JSONObject outputValue) {
		logger.info("Preparing to switch on running import for key:"+opKey);
		//TO-DO!
		switch(opKey) {
		case REGIONS: return importRegions(outputValue);
		case AMI_DESCRIPTORS: return importAmiDescriptors(outputValue);
		case PRODUCTS: return importProducts(outputValue);
		case PRODUCT_DESCRIPTIONS: return importProductDescriptions(outputValue);
		case TRIAL_PRODUCT_CHILDREN: return importTrialProductChildren(outputValue);
		case TRIAL_ENVIRONMENTS: return importTrialEnvironments(outputValue); 
		case INDUSTRIES: return importIndustries(outputValue);
		case COMPANIES: return importCompanies(outputValue);
		case USERS: return importUsers(outputValue);
		case AMAZON_CREDENTIALS: return importAmazonCredentials(outputValue);
		default:break;
		}
		return false;
	}


	@Override
	public boolean runImport(String key, JSONObject outputValue) {		
		try {
		return runImport(OpKeyEnum.valueOf(key),outputValue);
		} catch (IllegalArgumentException e) {
            JSONObject obj=new JSONObject();
			addToJsonAndIgnore(obj,"success",false);
			addToJsonAndIgnore(obj,"error",e);
			logger.error("Encountered exception running import for key '"+key+"'",e);
			if (key != null) {
				addToJsonAndIgnore(outputValue,key,obj);
			} else {
				addToJsonAndIgnore(outputValue,"KEY-MISSING-"+keyMissingAppend,obj);
				keyMissingAppend++;
			}
		}

		return false;
	}
	

	protected boolean importCompanies(JSONObject outputValue) {
		Industry other=industryDao.findByName("Other");
		boolean retValue;
		JSONObject jObj=new JSONObject();

		ArrayList<Company> companies=new ArrayList<>();
		tracker.reset().set("startCount(db)",companyDao.count());
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedCompanies");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject compObj=jsonArray.optJSONObject(i);
				if (compObj != null) {
					ExportCompanyDto dto=new ExportCompanyDto(compObj);
					Company inDb=companyDao.findByName(dto.getName());
					Company company;
					
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							company=inDb;
							tracker.increment("modified");
							} else {
								tracker.increment("skipped");
								continue;
							}
						} else {
							company=new Company();
							tracker.increment("added");
						}
					
					if (dto.getIndustryName()!=null) {
						Industry industry=industryDao.findByName(dto.getIndustryName());
						if (industry==null) {
							industry=other;
						}
						company.setIndustry(industry);
					}
					company.setName(dto.getName());
					company.setInforId(dto.getInforId());
					company.setNotes(dto.getNotes());
					companies.add(company);
					
					}
				
			}
			
			companyDao.save(companies);
			companyDao.flush();
			retValue=true;
		} else {
			addToJsonAndIgnore(jObj,"error","exported company array null!");
			retValue=false;
		}
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",companies.size());
		tracker.set("endCount(db)",companyDao.count());
		tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.COMPANIES.name(),jObj);
		
		return retValue;
	}


	protected boolean importAmiDescriptors(JSONObject outputValue) {
		boolean retValue;
		JSONObject jObj=new JSONObject();

		ArrayList<AmiDescriptor> amiDescriptors=new ArrayList<>();
		tracker.reset().set("startCount(db)",amiDescriptorDao.count());
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedAmiDescriptors");
		
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject amiObj=jsonArray.optJSONObject(i);
				if (amiObj != null) {
					ExportAmiDescriptorDto dto=new ExportAmiDescriptorDto(amiObj);
					AmiDescriptor inDb=amiDescriptorDao.findByName(dto.getName());
					AmiDescriptor amiDescriptor;
					
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							amiDescriptor=inDb;
							tracker.increment("modified");
							} else {
								tracker.increment("skipped");
								continue;
							}
						} else {
							amiDescriptor=new AmiDescriptor();
							tracker.increment("added");
						}
					amiDescriptor.setAmi(dto.getAmi());
					amiDescriptor.setAwsKey(dto.getAwsKey());
					amiDescriptor.setAwsSecretKey(dto.getAwsSecretKey());
					amiDescriptor.setDescription(dto.getDescription());
					amiDescriptor.setEipNeeded(dto.getEipNeeded());
					amiDescriptor.setIpAddress(dto.getIpAddress());
					amiDescriptor.setName(dto.getName());
					amiDescriptor.setSize(dto.getSize());
					amiDescriptor.setTagName(dto.getTagName());
					amiDescriptor.setVersion(dto.getVersion());
					amiDescriptors.add(amiDescriptor);
					amiDescriptor.setRegion( regionDao.findById( Long.valueOf(dto.getRegionId())) );
				
				}
			
			
			}
		
			amiDescriptorDao.save(amiDescriptors);
			amiDescriptorDao.flush();
			retValue=true;
		}else {
			addToJsonAndIgnore(jObj,"error","exported ami descriptor array null!");
			retValue=false;
		}
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",amiDescriptors.size());
		tracker.set("endCount(db)",amiDescriptorDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.AMI_DESCRIPTORS.name(),jObj);
		
		return retValue;
	}

	protected boolean importIndustries(JSONObject outputValue) {
		boolean retValue;
		JSONObject jObj=new JSONObject();

		ArrayList<Industry> industries=new ArrayList<>();
		tracker.reset().set("startCount(db)",industryDao.count());
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedIndustries");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject indObj=jsonArray.optJSONObject(i);
				if (indObj != null) {
					
					ExportIndustryDto dto=new ExportIndustryDto(indObj);
					Industry inDb=industryDao.findByName(dto.getName());
					Industry industry;
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							industry=inDb;
							tracker.increment("modified");
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						industry=new Industry();
						tracker.increment("added");
					}
					
					industry.setName(dto.getName());
					industry.setDescription(dto.getDescription());
					
					industries.add(industry);
					
				}
				
			}
			
			industryDao.save(industries);
			industryDao.flush();
			retValue=true;
		}else {
			addToJsonAndIgnore(jObj,"error","exported industry array null!");
			retValue=false;
		}
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",industries.size());
		tracker.set("endCount(db)",industryDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.INDUSTRIES.name(),jObj);
		return retValue;

	}

	protected boolean importAmazonCredentials(JSONObject outputValue) {

		boolean retValue;
		JSONObject jObj=new JSONObject();
		ArrayList<AmazonCredentials> amazonCredentialsList= new ArrayList<>();
		tracker.reset().set("startCount(db)",amazonCredentialsDao.count());
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedAmazonCredentials");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject amCredObj=jsonArray.optJSONObject(i);
				if (amCredObj != null) {
					ExportAmazonCredentialsDto amcredDto =new ExportAmazonCredentialsDto(amCredObj);
					User user=userDao.findByUsername(amcredDto.getUsername());
					
					AmazonCredentials inDb=amazonCredentialsDao.findByUserAndName(user, amcredDto.getName());
					AmazonCredentials amazonCredentials;
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							amazonCredentials=inDb;
							tracker.increment("modified");
							
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						tracker.increment("added");
						amazonCredentials=new AmazonCredentials();
					}
					
					
					amazonCredentials.setUser(user);
					amazonCredentials.setAwsKey(amcredDto.getAwsKey());
					amazonCredentials.setName(amcredDto.getName());
					amazonCredentials.setSecretKey(amcredDto.getSecretKey());
					amazonCredentialsList.add(amazonCredentials);
				}
			}
			
			
			amazonCredentialsDao.save(amazonCredentialsList);
			amazonCredentialsDao.flush();
			retValue=true;
			
		} else {
			addToJsonAndIgnore(jObj,"error","export array of amazon credentials null!");
			retValue=false;
		}
			
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",amazonCredentialsList.size());
		tracker.set("endCount(db)",amazonCredentialsDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.AMAZON_CREDENTIALS.name(),jObj);
		return retValue;
	}

	protected Set<ExportTrialInstanceDto> cleanUserTrialInstances(User user) {
		HashSet<ExportTrialInstanceDto> trialInstanceDtos=new HashSet<>();
		if (user != null) {
			List<TrialInstance> trialInstances =trialInstanceDao.findByUserId(user.getId());
			if (trialInstances != null) {
				for (TrialInstance instance : trialInstances) {
					trialInstanceDtos.add(new ExportTrialInstanceDto(instance));
				}
			}
			
			trialInstanceDao.delete(trialInstances);
			trialInstanceDao.flush();
		}
		
		
		return trialInstanceDtos;
	}
	protected Set<ExportUserProductDto> cleanUserProducts(User user) {
		HashSet<ExportUserProductDto> userProductDtos=new HashSet<>();
		if (user != null) {
			List<UserProduct> userProducts=userProductDao.findByUserId(user.getId());
			if (userProducts != null) {
				for (UserProduct product : userProducts) {
					userProductDtos.add(new ExportUserProductDto(product));
				}
			}
			
			userProductDao.delete(userProducts);
			userProductDao.flush();
			
		}
		return userProductDtos;
	}
	protected Set<ExportUserTrackingDto> cleanUserTrackings(User user) {
	
		HashSet<ExportUserTrackingDto> userTrackings=new HashSet<>();
		if (user != null) {
			for (UserProduct up : user.getUserProducts().values()) {
				userProductDao.delete(up);
				
			}
			userProductDao.flush();
			user.getUserProducts().clear();
			
			user.getRoles().clear();
			
			//removed collections, save first
			userDao.save(user);
			userDao.flush();
			
			//remove amcreds
			amazonCredentialsDao.deleteByUser(user);
			amazonCredentialsDao.flush();
			
			//remove trackings--but add them to our list :)
			List<UserTracking> trackings=userTrackingDao.findByUser(user);
			if (trackings != null) {
				for (UserTracking tracking : trackings) {
					
					userTrackings.add(new ExportUserTrackingDto(tracking)); 
					
				}
			}
			userTrackingDao.delete(trackings);
			userTrackingDao.flush();
			
			
		}
		return userTrackings;
	}
	

	protected boolean importUsers(JSONObject outputValue) {
		tracker.reset().set("startCount(db)",userDao.count());
		boolean retValue;
		JSONObject jObj=new JSONObject();
		ArrayList<User> users=new ArrayList<>();
		JSONArray jsonArray=this.getJSONArrayFromImportJSON("exportedUsers");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject userObj=jsonArray.optJSONObject(i);
				if (userObj != null) {
					ExportUserDto userDto=new ExportUserDto(userObj);
					
					User inDb=userDao.findByUsername(userDto.getUsername());
					User user;
					Set<ExportUserTrackingDto> trackingDtos=null;
					Set<ExportTrialInstanceDto> trialInstanceDtos=null;
					Set<ExportUserProductDto> upDtos=null;
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							tracker.increment("modified(del/add)");
							//must delete/add because "createdAt" is not update-able
							trialInstanceDtos=cleanUserTrialInstances(inDb);
							upDtos=cleanUserProducts(inDb);
							trackingDtos=cleanUserTrackings(inDb);
							
							//should be clear to remove
							userDao.delete(inDb);
							userDao.flush();
							
							user=new User();
							
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						user=new User();
						tracker.increment("added");
					}
					
					user.setActive(userDto.getActive());
					user.setAddress1(userDto.getAddress1());
					user.setAddress2(userDto.getAddress2());
					if (userDto.getCompanyName() != null) {
						Company company=companyDao.findByName(userDto.getCompanyName());
						if (company != null) {
							user.setCompany(company);
						}
					}
					user.setCountry(userDto.getCountry());
					user.setCreatedAt(userDto.getCreatedAt());
					user.setUpdatedAt(userDto.getUpdatedAt());
					user.setFirstName(userDto.getFirstName());
					user.setInforCustomer(userDto.getInforCustomer());
					user.setLanguage(userDto.getLanguage());
					user.setLastName(userDto.getLastName());
					//user.setLoginAttempts(0);
					user.setPassword(userDto.getPassword());
					user.setPhone(userDto.getPhone());
					if (userDto.getRoles()==null || userDto.getRoles().size()==0) {
						logger.error("Empty roles for user:"+userDto.getUsername());
					}
					for (String role : userDto.getRoles()) {
						try {
							Role rl=Role.valueOf(role);
							if (rl==null) {
								logger.error("null role for name '"+role+"'");
							}
							user.getRoles().add(Role.valueOf(role));
						} catch (IllegalArgumentException e) {
                            logger.error("Exception '"+e+"' for role '"+role+"' in user '"+userDto.getUsername());
						}
					}
					
					user.setUsername(userDto.getUsername());
					userDao.save(user);
					userDao.flush();
					users.add(user);
					
					if (trackingDtos != null) {
						ArrayList<UserTracking> trackings=new ArrayList<>();
						for (ExportUserTrackingDto trackDto : trackingDtos) {
							UserTracking track=new UserTracking();
							track.setOtherData(trackDto.getOtherData());
							track.setTargetObject(trackDto.getTargetObject());
							track.setUser(user);
							track.setTimestamp(trackDto.getTimestamp());
							try {
								track.setTrackingType(TrackingType.valueOf(trackDto.getTrackingType()));
							} catch (IllegalArgumentException ignored) {

							}
							trackings.add(track);
						}
					
						userTrackingDao.save(trackings);
						userTrackingDao.flush();
						tracker.addTo("readded-userTrackings",trackings.size());
					}
					
					if (trialInstanceDtos != null) {
						ArrayList<TrialInstance> trialInstances=new ArrayList<>();
						for (ExportTrialInstanceDto dto : trialInstanceDtos) {
							TrialInstance instance= new TrialInstance();
							instance.setCreatedAt(dto.getCreatedAt());
							instance.setDomain(dto.getDomain());
							instance.setEnvironmentId(dto.getEnvironmentId());
							instance.setExpirationDate(dto.getExpirationDate());
							instance.setGuid(dto.getGuid());
							instance.setPassword(dto.getTrialPassword());
							try {
								instance.setProductVersion(productVersionDao.findByProductAndName(productDao.findByShortName(dto.getProductShortName()),dto.getProductVersionName()));
							} catch (Exception e) {
								logger.error("Error retrieving productversion with product.shortName:"+dto.getProductShortName()+", and productVersion.name:"+dto.getProductVersionName(),e);
							}
							
							try {
								instance.setRegion(regionDao.findByShortName(dto.getRegionShortName()));
							} catch (Exception e) {
								logger.error("Error retrieving region with shortName:"+dto.getRegionShortName(),e);
							}
							
							try {
								instance.setType(TrialInstanceType.valueOf(dto.getType()));
							} catch (IllegalArgumentException e) {
                                logger.error("Error setting trial instance type with string value:"+dto.getType(),e);
							}
							instance.setUpdatedAt(dto.getUpdatedAt());
							instance.setUser(user);
							instance.setUsername(dto.getTrialUsername());
							 
							trialInstances.add(instance);
						}
						
						trialInstanceDao.save(trialInstances);
						trialInstanceDao.flush();
						tracker.addTo("readded-trialInstances",trialInstances.size());
					}

					if (upDtos!=null && upDtos.size()>0) {
						userDto.getUserProductDtos().clear();
						userDto.getUserProductDtos().addAll(upDtos);
						logger.info("Using user product data from import ... non-null, non-zero");
						tracker.addTo("imported-userproducts",upDtos.size());
					} 

					for (ExportUserProductDto upDto : userDto.getUserProductDtos()) {
						Product prod=productDao.findByShortName(upDto.getProductShortName());
						UserProductKey upkey=new UserProductKey(user,prod);
						UserProduct upInDb=userProductDao.findById(upkey);
						UserProduct userProduct;
						if (upInDb != null) {
							if (getOverwriteConflicts()) {
								tracker.increment("userProdModified");
								userProduct=upInDb;
							} else {
								tracker.increment("userProdSkipped");
								continue;
							}
						} else {
							userProduct=new UserProduct(user,prod);
							tracker.increment("userProdAdded");
						}
						
						userProduct.setLaunchAvailable(upDto.getLaunchAvailable());
						userProduct.setOwned(upDto.getOwned());
						userProduct.setTrialAvailable(upDto.getTrialAvailable());
						userProductDao.save(userProduct);
						userProductDao.flush();
						user.getUserProducts().put(prod.getId(), userProduct);
					}
					
					
					
					
				}

				
			}
			userDao.save(users);
			userDao.flush();
			retValue=true;
			
		} else {
			addToJsonAndIgnore(jObj,"error","exported users array null!");
			retValue=false;
		}
		
		
		
		tracker.setTotal("expected op total size","modified","added","modified(del/add)");
		tracker.set("list to db size",users.size());
		tracker.set("endCount(db)",userDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.USERS.name(),jObj);
		return retValue;
	}

	protected TrialEnvironment narrowTrialEnvironmentList(List<TrialEnvironment> list,ExportTrialEnvironmentDto envDto) {

		
		logger.info("Starting off with a trial environment list of size:"+list.size());
		logger.info("Narrowing with this envDto:"+LINE+envDto.loggableOutput());
		List<TrialEnvironment> newList=trialEnvironmentDao.findNarrowedByUrl(list, envDto.getUrl());
		if (newList.size()==0) {
			newList=list;
			logger.info("First narrow left zero, setting back to original list for narrowByName");
		} else if (newList.size()==1) {
			return newList.get(0);
		}
		
		
		
		if (newList.size() > 1) {
			newList=trialEnvironmentDao.findNarrowedByUsername(newList,envDto.getUsername());
			if (newList.size()==0) {
				return null;
			}
		
			
		}
		if (newList.size()>1) {
			logger.info("Two narrows, we still have a TrialEnvironment list the size of:"+newList.size()+"--will return first");
			
		}
		
		return newList.get(0);
	}
	
	protected boolean importTrialEnvironments(JSONObject outputValue) {
		tracker.reset().set("startCount(db)",trialEnvironmentDao.count());
		boolean retValue;
		ArrayList<TrialEnvironment> trialEnvironments=new ArrayList<>();
		JSONObject jObj=new JSONObject();
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedTrialEnvironments");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject trenvObj=jsonArray.optJSONObject(i);
				if (trenvObj != null) {
					ExportTrialEnvironmentDto envDto=new ExportTrialEnvironmentDto(trenvObj);
					Product product=productDao.findByShortName(envDto.getProductShortName());
					ProductVersion productVersion=productVersionDao.findByProductAndName(product, envDto.getProductVersionName());
					
					Region region=regionDao.findByShortName(envDto.getRegionShortName());

					logger.trace("Processing the following TrialEnvironment import:\n"+envDto.loggableOutput());
					
					TrialEnvironment inDb;
					List<TrialEnvironment> inDbList=trialEnvironmentDao.findByEnvironmentIdAndProductVersionAndRegion(envDto.getEnvironmentId(), productVersion, region);
					if (inDbList.size()>0) {
						if (inDbList.size()==1) {
							inDb=inDbList.get(0);
						} else {
							inDb=narrowTrialEnvironmentList(inDbList,envDto);
						}
					} else {
						inDb=null;
					}
					
					TrialEnvironment trialEnvironment;
					
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							tracker.increment("modified");
							trialEnvironment=inDb;
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						tracker.increment("added");
						trialEnvironment=new TrialEnvironment();
					}
					
					trialEnvironment.setEnvironmentId(envDto.getEnvironmentId());
					trialEnvironment.setPassword(envDto.getPassword());
					trialEnvironment.setProductVersion(productVersion);
					trialEnvironment.setRegion(region);
					trialEnvironment.setUrl(envDto.getUrl());
					trialEnvironment.setUsername(envDto.getUsername());
					
					trialEnvironments.add(trialEnvironment);
				}
			}
			
			trialEnvironmentDao.save(trialEnvironments);
			trialEnvironmentDao.flush();
			retValue=true;
			
		} else {
			addToJsonAndIgnore(jObj, "error", "export array of trial environments null!");
			retValue=false;
		}
		
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",trialEnvironments.size());
		tracker.set("endCount(db)",trialEnvironmentDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.TRIAL_ENVIRONMENTS.name(),jObj);
		return retValue;
	}

	protected boolean importProductDescriptions(JSONObject outputValue) {
		tracker.reset().set("startCount(db)",productDescriptionDao.count());
		boolean retValue;
		ArrayList<ProductDescription> productDescriptions=new ArrayList<>();
		JSONObject jObj=new JSONObject();
		JSONArray jsonArray=getJSONArrayFromImportJSON("exportedProductDescriptions");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				
				JSONObject prodDescObj=jsonArray.optJSONObject(i);
				if (prodDescObj != null) {
					ExportProductDescriptionDto prodDescDto=new ExportProductDescriptionDto(prodDescObj);
					CSLocale locale=new CSLocale();
					locale.setCountry(prodDescDto.getCsLocaleCountry());
					locale.setLanguage(prodDescDto.getCsLocaleLanguage());
					locale.setVariant(prodDescDto.getCsLocaleVariant());
					ProductDescKey descKey=null;
					try {
						descKey=ProductDescKey.valueOf(prodDescDto.getDescKey());
					} catch (IllegalArgumentException ignored) {

					}
					Product product=productDao.findByShortName(prodDescDto.getProductShortName());
					
					ProductDescription inDb=productDescriptionDao.findByProductAndLocaleAndDescKey(product, locale, descKey);
					ProductDescription pdesc;
					if (inDb != null) {
						if (getOverwriteConflicts()) {
							tracker.increment("modified");
							pdesc=inDb;
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						pdesc=new ProductDescription();
						tracker.increment("added");
					}
					
					
					pdesc.setDescKey(descKey);
					pdesc.setProduct(product);
					pdesc.setLocale(locale);
					pdesc.setText(prodDescDto.getText());
					productDescriptions.add(pdesc);
					
				}
			}
			productDescriptionDao.save(productDescriptions);
			productDescriptionDao.flush();
			retValue=true;
			
		} else {
			addToJsonAndIgnore(jObj, "error", "array of product descriptions null!");
			retValue=false;
		}
		
		
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",productDescriptions.size());
		tracker.set("endCount(db)",productDescriptionDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.PRODUCT_DESCRIPTIONS.name(),jObj);
		return retValue;
	}

	private boolean importProducts(JSONObject outputValue) {
		
		tracker.reset().set("startCount(db)",productDao.count());
		boolean retValue;
		JSONObject jObj=new JSONObject();
		ArrayList<Product> products=new ArrayList<>();
		HashMap<Long,ArrayList<ProductVersion>> versions=new HashMap<>();
		JSONArray jsonArray=this.getJSONArrayFromImportJSON("exportedProducts");
		if (jsonArray!=null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject prodObj=jsonArray.optJSONObject(i);
				if (prodObj!=null) {
					ExportProductDto prodDto=new ExportProductDto(prodObj);
					
					Product inDb=productDao.findByShortName(prodDto.getShortName());
					Product product;
					if (inDb!=null) {
						if (getOverwriteConflicts()) {
							tracker.increment("modified");
							product=inDb;
							
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						tracker.increment("added");
						product=new Product();
					}
					
					product.setShortName(prodDto.getShortName());
					product.setCreatedAt(prodDto.getCreatedAt());
					product.setUpdatedAt(prodDto.getUpdatedAt());
					product.setName(prodDto.getName());
					product.setIeOnly(prodDto.getIeOnly());
	
					product.setTileOrder(prodDto.getTileOrder());
					try {
						product.setTileSize(TileSize.valueOf(prodDto.getTileSize()));
					} catch (IllegalArgumentException ignored) {

					}
					
					product.setDeploymentsAvailable(prodDto.getDeploymentsAvailable());
					product.setTrialsAvailable(prodDto.getTrialsAvailable());
					product.setDisplayName1(prodDto.getDisplayName1());
					product.setDisplayName2(prodDto.getDisplayName2());
					product.setDisplayName3(prodDto.getDisplayName3());
					
					
					ArrayList<ProductVersion> versionList=new ArrayList<>();
					for (ExportProductVersionDto productVersionDto : prodDto.getProductVersions()) {
						
						ProductVersion productVersion;
						ProductVersion versionInDb=null;
						if (productVersionDto.getLookupName() != null) {
							versionInDb=productVersionDao.findByProduct_ShortNameAndName(prodDto.getShortName(),productVersionDto.getLookupName());
						}
						//logger.info("looking for lookupName():"+productVersionDto.getLookupName());
						if (versionInDb ==null) {
							versionInDb=productVersionDao.findByProduct_ShortNameAndName(prodDto.getShortName(), productVersionDto.getName());
							
						} else {
							tracker.increment("productversion_namechange");
						}
						
						if (versionInDb != null) {
							tracker.increment("productversion_modded");
							productVersion=versionInDb;
						} else {
							tracker.increment("productversion_added");
							productVersion=new ProductVersion();
						}
						
						productVersion.setAccessKey(productVersionDto.getAccessKey());
						productVersion.setSecretKey(productVersionDto.getSecretKey());
						productVersion.setIeOnly(productVersionDto.getIeOnly());
						productVersion.setName(productVersionDto.getName());
						productVersion.setDescription(productVersionDto.getDescription());
						if (versionInDb == null) {
							productVersion.setCreatedAt(new Date());
						}
						productVersion.setUpdatedAt(new Date());
						//productVersion.setProduct(product);
						for (String amiDescriptor : productVersionDto.getAmiDescriptors()) {
							productVersion.getAmiDescriptors().add(amiDescriptorDao.findByName(amiDescriptor));
						}
						productVersion.setRegion(regionDao.findById(Long.valueOf(productVersionDto.getRegionId())));
						//productVersionDao.save(productVersion);
						versionList.add(productVersion);
						//product.getProductVersions().add(productVersion);
					}
					
					products.add(product);
					productDao.save(product);
					productDao.flush();
					versions.put(product.getId(),versionList);
					
				}

				
			}
		
			for (Product product : products) {
				ArrayList<ProductVersion> vlist=versions.get(product.getId());
				product.getProductVersions().clear();
				
				for (ProductVersion vers : vlist) {
					vers.setProduct(product);
					productVersionDao.save(vers);
				}
				product.setProductVersions(vlist);

				}
			productDao.save(products);
	
			retValue=true;
			
		} else {
			retValue=false;
			addToJsonAndIgnore(jObj,"error","products array null!");
		}
		
		
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",products.size());
		tracker.set("endCount(db)",productDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.PRODUCTS.name(),jObj);
		return retValue;
		//user product and trial contact need to run to complete this import fully ;)
	}

	private boolean importRegions(JSONObject outputValue) {

		tracker.reset().set("startCount(db)", regionDao.count());
		boolean retValue;
		JSONObject jObj=new JSONObject();
		ArrayList<Region> regions=new ArrayList<>();
		JSONArray jsonArray=this.getJSONArrayFromImportJSON("exportedRegions");
		if (jsonArray != null) {

			for (int i=0; i<jsonArray.length();i++) {
				JSONObject regionObj=jsonArray.optJSONObject(i);
				if (regionObj != null) {
					ExportRegionDto regionDto=new ExportRegionDto(regionObj);
					//Region inDb=regionDao.findByShortName(regionDto.getShortName());
					Region inDb=regionDao.findById(regionDto.getId());
					Region region;
					if (inDb != null) {
						 if (getOverwriteConflicts()) {
							tracker.increment("modified");
							region=inDb;
						 } else {
							 tracker.increment("skipped");
							 continue;
						 }
					} else {
						region =new Region();
						tracker.increment("added");
					}
					
					region.setId(regionDto.getId());
					region.setName(regionDto.getName());
					region.setShortName(regionDto.getShortName());
					region.setCloudAlias(regionDto.getCloudAlias());
					region.setEndPoint(regionDto.getEndPoint());
					region.setRegionType(DeploymentType.valueOf(regionDto.getRegionType()));
					regions.add(region);
					
				}
				
			}
			regionDao.save(regions);
			regionDao.flush();
			retValue=true;
			
			
		} else {
			retValue=false;
			addToJsonAndIgnore(jObj,"error","region array null!");
		}
		
		
		tracker.setTotal("expected op total size","modified","added");
		tracker.set("list to db size",regions.size());
		tracker.set("endCount(db)",regionDao.count());
        tracker.setSuccess(retValue);

		tracker.markJsonObject(jObj);
		addToJsonAndIgnore(outputValue,OpKeyEnum.REGIONS.name(),jObj);
		
		return retValue;
	}

	protected boolean importTrialProductChildren(JSONObject outputValue) {
		tracker.reset().set("startCount(db)",trialProductChildDao.count());
	
		boolean retValue;
		ArrayList<TrialProductChild> trialProductChildren=new ArrayList<>();
		JSONObject jObj=new JSONObject();
		
		JSONArray jsonArray=this.getJSONArrayFromImportJSON("exportedTrialProductChildren");
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length();i++) {
				JSONObject tpcObj=jsonArray.optJSONObject(i);
				if (tpcObj != null) {
					
					ExportTrialProductChildDto tpcDto=new ExportTrialProductChildDto(tpcObj);
						
					Region region=regionDao.findByShortName(tpcDto.getRegionShortName());
					Product parentProduct=productDao.findByShortName(tpcDto.getParentProductShortName());
					Product childProduct=productDao.findByShortName(tpcDto.getChildProductShortName());
					
					ProductVersion parent=productVersionDao.findByProductAndName(parentProduct,tpcDto.getParentProductVersionName());
					ProductVersion child=productVersionDao.findByProductAndName(childProduct,tpcDto.getChildProductVersionName());
					
					TrialProductChild inDb=trialProductChildDao.findByRegionAndParentVersionAndChildVersion(region, parent, child);
					
					TrialProductChild trialProductChild;
					
					if (inDb != null) {
						if(getOverwriteConflicts()) {
							tracker.increment("modified");
							trialProductChild=inDb;
						} else {
							tracker.increment("skipped");
							continue;
						}
					} else {
						trialProductChild=new TrialProductChild();
						tracker.increment("added");
					}
							
					trialProductChild.setRegion(region);
					trialProductChild.setParentVersion(parent);
					trialProductChild.setChildVersion(child);
					
					trialProductChildren.add(trialProductChild);
				}
			}
			trialProductChildDao.save(trialProductChildren);
			trialProductChildDao.flush();
			retValue=true;
			
		} else {
			addToJsonAndIgnore(jObj,"error", "array of trialproductchildren null!");
			retValue=false;
		}
		
		tracker.set("expected op total size", tracker.get("modified")+tracker.get("added"));
		tracker.set("list to db size",trialProductChildren.size());
		tracker.set("endCount(db)",trialProductChildDao.count());	
        tracker.setSuccess(retValue);
		tracker.markJsonObject(jObj);

		addToJsonAndIgnore(outputValue,OpKeyEnum.TRIAL_PRODUCT_CHILDREN.name(),jObj);
				
		return retValue;
	}

}
	
