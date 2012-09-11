package com.infor.cloudsuite.service.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductDescriptionDao;
import com.infor.cloudsuite.dto.ProductDescriptionDto;
import com.infor.cloudsuite.dto.ProductWithDescriptionDto;
import com.infor.cloudsuite.entity.Product;

@Component
public class ProductServiceComponent {

	@Resource
	private ProductDao productDao;
	@Resource
	private ProductDescriptionDao productDescriptionDao;

	
	/*
    public List<ProductWithDescriptionDto> updateProductsWithDescriptions(List<ProductWithDescriptionDto> dtos) {
    	
    	for (ProductWithDescriptionDto dto : dtos) {
    		Product product;
    		if (dto.getId() != null) {
    			product=productDao.findById(dto.getId());
    			
    		} else {
    			product=productDao.findByShortName(dto.getShortName());
    		}
    		
    		//not there at all?
    		if (product==null) {
    			product=new Product();
    		}
    		
    		product.setAccessKey(dto.getAccessKey());
    		product.getAmis().addAll(dto.getAmis());
    		if (product.getId()==null) {
    			product.setCreatedAt(new Date());
    		}
    		
    		product.setDeploymentsAvailable(dto.getDeploymentsAvailable());
    		product.setIeOnly(dto.getIeOnly());
    		product.setName(dto.getName());
    		product.setSecretKey(dto.getSecretKey());
    		product.setShortName(dto.getShortName());
    		product.setTemplateName(dto.getTemplateName());
    		product.setTileOrder(dto.getTileOrder());
    		
    		try {
    			product.setTileSize(TileSize.valueOf(dto.getTileSize()));
    		} catch (Exception e) {
    			//ignore
    		}
    		
    		product.setTrialsAvailable(dto.getTrialsAvailable());
    		product.setUpdatedAt(new Date());
    		
    		productDao.save(product);
    		productDao.flush();
    		
    		for (Long key : dto.getContactMap().keySet()) {
    			TrialRequestContactDto trcDto=dto.getContactMap().get(key);
    			Region region;
    			if (trcDto.getRegionId() != null) {
    				region=regionDao.findById(trcDto.getRegionId());
    			} else {
    				region=regionDao.findByShortName(trcDto.getRegionShortName());
    			}
    			
    			ProductRegionKey prkey=new ProductRegionKey(product,region);
    			TrialRequestContact contact=trialRequestContactDao.findById(prkey);
    			if (contact ==null) {
    				contact=new TrialRequestContact();
    				contact.setId(prkey);
    			}
    			
    			contact.setContactEmail(trcDto.getContactEmail());
    			product.getContactMap().put(key, contact);
    		}
    		productDao.save(product);
    		productDao.flush();
    		
    		ArrayList<ProductDescription> descriptions= new ArrayList<ProductDescription>();
    		for (ProductDescriptionDto descDto : dto.getProductDescriptionDtos()) {
    			ProductDescription desc=null;
    			if (descDto.getId() != null) {
    				desc=productDescriptionDao.findById(descDto.getId());
    			} 
    			
    			if (desc == null) {
    				desc=new ProductDescription();
    			}
    			desc.setLocale(new CSLocale(descDto.getLocaleLanguage(),descDto.getLocaleCountry(),descDto.getLocaleVariant()));
    			try {
    				desc.setDescKey(ProductDescKey.valueOf(descDto.getProductDescKey()));
    				} catch (Exception e) {
    					//ignore?
    				}
    				
    				
   				if (desc.getProduct()==null) {
    					desc.setProduct(product);
   				}
   				desc.setText(descDto.getText());
   				descriptions.add(desc);
    			
    		}
    		productDescriptionDao.save(descriptions);
    		productDescriptionDao.flush();
    		
    	}
    	
    	productDao.flush();
    	
    	return getAllProductsWithDescriptions();
    }
    */
	
    public List<ProductWithDescriptionDto> getAllProductsWithDescriptions() {
    	ArrayList<ProductWithDescriptionDto> toReturn=new ArrayList<>();

    	for (Product product : productDao.findAll()) {
    		List<ProductDescriptionDto> forThisProd=productDescriptionDao.getAllProductDescriptionsByProductId(product.getId());
    		toReturn.add(new ProductWithDescriptionDto(product,forThisProd,false));
    	}
    	
    	return toReturn;
    }   
}
