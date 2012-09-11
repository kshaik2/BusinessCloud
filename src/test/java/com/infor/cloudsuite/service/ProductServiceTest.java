package com.infor.cloudsuite.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.AbstractTest;
import com.infor.cloudsuite.EntityManagerDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.ProductDescriptionDao;
import com.infor.cloudsuite.dto.ContraintViolationDto;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.dto.ProductVersionDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.service.component.ProductServiceComponent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * User: bcrow
 * Date: 10/25/11 1:04 PM
 */
@Transactional(propagation = Propagation.REQUIRED)
public class ProductServiceTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Resource
    private ProductService productService;
    @Resource
    private EntityManagerDao emService;
    @Resource
    private ProductDescriptionDao productDescriptionDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductServiceComponent productServiceComponent;
    

    @Test
    public void getAllProductsTest() {
        loginTestUser();
        final List<ProductUserProductDto> products = productService.getProducts();
        for(ProductUserProductDto userProduct : products) {
            for(ProductVersionDto version: userProduct.getVersions()) {
                assertNotNull(version.getRegion_id());
            }
        }
        assertFalse("Products is empty", products.isEmpty());
    }

    @Test
    public void testAddProduct() {
        loginTestUser();
        Product product = new Product();
        product.setName("New Product");
        product.setShortName("NP");
        

        long startCount=productDao.count();
        
        productService.addProduct(product);
        emService.flush();

        
        final List<ProductUserProductDto> products  = productService.getProducts();
        assertEquals("Products contains 1 more entry ("+(startCount+1)+" in this case)", (startCount+1), products.size());
        ProductUserProductDto retProd = products.get((int)startCount);

        assertEquals(product.getName(), retProd.getName());
        assertEquals(product.getShortName(), retProd.getShortName());
        assertEquals("Id is correct", product.getId(), retProd.getId());
    }

    @Test
    public void testAddProductFailure() {
        Product product = new Product();
        product.setName("New Product");
        product.setShortName("NP");
        loginTestUser();
        long startCount=productDao.count();

        Response response = productService.addProduct(product);
        emService.flush();
        Long id = product.getId();
        assertEquals("Ok", Response.Status.OK.getStatusCode(), response.getStatus());
 
        final List<ProductUserProductDto> products  = productService.getProducts();
        assertEquals("Products contains 1 more entry ("+(startCount+1)+" in this case)", (startCount+1), products.size());
        ProductUserProductDto retProd = products.get((int)startCount);

        assertEquals(product.getName(), retProd.getName());
        assertEquals(product.getShortName(), retProd.getShortName());
        assertEquals(product.getId(), id, retProd.getId());

        try {
            product = new Product();
            product.setName("Infor10 EAM Enterprise");
            product.setShortName("EAMXX");
            productService.addProduct(product);
            emService.flush();
            fail("can't add two with same long name.");
        } catch (Exception e) {
            //empty
        }

        try {
            product = new Product();
            product.setName("Enterprise xxx");
            product.setShortName("EAM");
            productService.addProduct(product);
            emService.flush();
            fail("can't add two with short name.");
        } catch (Exception e) {
            //empty
        }
    }

    @Test
    public void testValidationErrors() {
        Product product = new Product();
        loginTestUser();
        Response response = productService.addProduct(product);
        assertEquals("Bad validation", StringDefs.VALIDATION_ERROR_CODE, response.getStatus());
        final Object entity = response.getEntity();
        assertNotNull(entity);
        assertTrue(ContraintViolationDto.class.isAssignableFrom(entity.getClass()));
        @SuppressWarnings("unchecked")
        final ContraintViolationDto<Product> violationDto = (ContraintViolationDto<Product>) entity;
        assertTrue(violationDto.isHasViolations());
        final Set<ConstraintViolation<Product>> violations = violationDto.getViolations();
        assertEquals("2 violations", 2, violations.size());
        if (logger.isDebugEnabled()) {
            for (ConstraintViolation<Product> violation : violations) {
                logger.debug("violation.getMessage() = " + violation.getMessage());
                logger.debug("violation.getPropertyPath().toString() = " + violation.getPropertyPath().toString());
            }
        }
    }
    
    @Test
    public void testProductDescription() {
    	loginAdminUser();
    	long inDatabase=productDescriptionDao.count();
    	assertEquals(inDatabase,productService.getProductDescriptions().size());
    
    }

}