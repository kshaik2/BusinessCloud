package com.infor.cloudsuite.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.TrialInstanceDao;
import com.infor.cloudsuite.dto.ProductDto;
import com.infor.cloudsuite.dto.TrialEnvironmentDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.TrialInstance;

/**
 * User: bcrow
 * Date: 12/2/11 2:18 PM
 */
//TODO Security for the Edge services.
@Path("/edge")
@Service
public class EdgeService {

    @Resource
    private TrialInstanceDao trialInstanceDao;
    @Resource
    private ProductDao productDao;


    @GET
    @Path("/expiredTrials/{productShortName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.SERVICEROLE_BASIC_EDGE)
    public List<TrialEnvironmentDto> getExpiredTrials(@PathParam("productShortName") String productShortName) {
        return getExpiredTrialsUnsecured(productShortName);
    }

    List<TrialEnvironmentDto> getExpiredTrialsUnsecured(String productShortName) {
        List<TrialEnvironmentDto> expriredEnvironments = new ArrayList<>();

        List<TrialInstance> instances = trialInstanceDao.findByProductVersion_Product_ShortNameAndExpirationDateLessThan(productShortName, new Date());
        for (TrialInstance instance : instances) {
            expriredEnvironments.add(new TrialEnvironmentDto(instance.getProductVersion().getProduct().getShortName(),
                    instance.getEnvironmentId(), instance.getUrl(), instance.getRegion().getCloudAlias()));
        }

        return expriredEnvironments;
    }

    @GET
    @Path("/expiredTrials")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.SERVICEROLE_BASIC_EDGE)
    public List<TrialEnvironmentDto> getExpiredTrials() {
        return getExpiredTrialsUnsecured();
    }

    List<TrialEnvironmentDto> getExpiredTrialsUnsecured() {
        List<TrialEnvironmentDto> expriredEnvironments = new ArrayList<>();
        List<TrialInstance> instances = trialInstanceDao.findByExpirationDateLessThan(new Date());
        for (TrialInstance instance : instances) {
            expriredEnvironments.add(new TrialEnvironmentDto(instance.getProductVersion().getProduct().getShortName(), instance.getEnvironmentId(), instance.getUrl(), instance.getRegion().getCloudAlias()));
        }

        return expriredEnvironments;
    }

    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.SERVICEROLE_BASIC_EDGE)
    public List<ProductDto> getProducts() {
        return getProductsUnsecured();
    }

    List<ProductDto> getProductsUnsecured() {
        List<ProductDto> dtos = new ArrayList<>();
        final List<Product> products = productDao.findAll();
        for (Product product : products) {
            dtos.add(new ProductDto(product.getId(), product.getShortName(), product.getName()));
        }

        return dtos;
    }
}
