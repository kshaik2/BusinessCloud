package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductDescriptionDao;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.dto.ProductDescriptionDto;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.dto.ProductWithDescriptionDto;
import com.infor.cloudsuite.entity.CSLocale;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductDescKey;
import com.infor.cloudsuite.entity.ProductDescription;
import com.infor.cloudsuite.platform.components.ValidationProvider;
import com.infor.cloudsuite.service.component.ProductServiceComponent;
import com.infor.cloudsuite.service.component.UserServiceComponent;

/**
 * User: bcrow
 * Date: 10/24/11 4:01 PM
 */
@Service
@Path("/products")
public class ProductService {

	@Resource
	private ProductServiceComponent productServiceComponent;
    @Resource
    private ProductDao productDao;
    @Resource
    private ValidationProvider validationService;
    @Resource
    private ProductDescriptionDao productDescriptionDao;
    @Resource
    private UserServiceComponent userServiceComponent;
    
    @GET
    @Path("/getAllProductDescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Secured(StringDefs.ROLE_ADMIN)
    public List<ProductDescriptionDto> getProductDescriptions(){
        return productDescriptionDao.getAllProductDescriptions();
        
    }
    
    @GET
    @Path("/getAllProductsWithDescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(readOnly = true)
    @Secured(StringDefs.ROLE_ADMIN)
    public List<ProductWithDescriptionDto> getAllProductsWithDescriptions() {

    	return productServiceComponent.getAllProductsWithDescriptions();
    }

    /*
    @POST
    @Path("/updateProductsWithDescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Secured(StringDefs.ROLE_ADMIN)
    public List<ProductWithDescriptionDto> updateProductsWithDescriptions(List<ProductWithDescriptionDto> dtos) {

    	return productServiceComponent.updateProductsWithDescriptions(dtos);

    }
    */
    
    @POST
    @Path("/addAllProductDescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Secured(StringDefs.ROLE_ADMIN)
    public Long addAllProductDescriptions(List<ProductDescriptionDto> productDescriptionDtos)
    {
    	long startCount=productDescriptionDao.count();
    	ArrayList<ProductDescription> toAdd=new ArrayList<>();
    	for (ProductDescriptionDto dto : productDescriptionDtos ) {
    		ProductDescription pd=new ProductDescription();
    		pd.setDescKey(ProductDescKey.valueOf(dto.getProductDescKey()));
    		pd.setLocale(new CSLocale(dto.getLocaleLanguage(),dto.getLocaleCountry(),dto.getLocaleVariant()));
    		pd.setProduct(productDao.findById(dto.getProductId()));
    		pd.setText(dto.getText());
    		toAdd.add(pd);
    	}
    	
    	productDescriptionDao.save(toAdd);
    	productDescriptionDao.flush();
    	long endCount=productDescriptionDao.count();
    	return (endCount-startCount);
    	
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public List<ProductUserProductDto> getProducts(){
        return userServiceComponent.getAllProducts();
        
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(propagation = Propagation.REQUIRED)
    @Secured(StringDefs.ROLE_EXTERNAL)
    public Response addProduct(Product product){
        final ContraintViolationDto<Product> violationDto = validationService.validate(product);
        if (violationDto.isHasViolations()) {
            return Response.status(StringDefs.VALIDATION_ERROR_CODE).entity(violationDto).build();
        }
        product.setCreatedAt(new Date());
        productDao.save(product);
        return Response.ok().build();
    }
}
