package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.ProductDescriptionDto;
import com.infor.cloudsuite.entity.CSLocale;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductDescKey;
import com.infor.cloudsuite.entity.ProductDescription;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * User: bcrow
 * Date: 3/20/12 3:43 PM
 */
public interface ProductDescriptionDao extends ExtJpaRepository<ProductDescription, Long> {
    
    List<ProductDescription> findByProductAndLocale(Product product, CSLocale locale);
    
    List<ProductDescription> findByProduct(Product product);

    @Query("select new com.infor.cloudsuite.dto.ProductDescriptionDto (pd.id, pd.product.id, pd.descKey, pd.locale, pd.text) from ProductDescription pd")
    List<ProductDescriptionDto> getAllProductDescriptions();
    
    @Query("select count(id) from ProductDescription where product=?1")
    Long countProductDescriptionsByProduct(Product product);
    
    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.ProductDescriptionDto (pd.descKey, pd.locale,pd.text) from ProductDescription pd where pd.product.id=?1")
    List<ProductDescriptionDto> getAllProductDescriptionsWithoutIdsByProductId(Long productId);
 
    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.ProductDescriptionDto (pd.id, pd.product.id, pd.descKey, pd.locale, pd.text) from ProductDescription pd where pd.product.id=?1")
    List<ProductDescriptionDto> getAllProductDescriptionsByProductId(Long productId);
    
    ProductDescription findByProductAndLocaleAndDescKey(Product product, CSLocale locale,ProductDescKey deskkey);
}
