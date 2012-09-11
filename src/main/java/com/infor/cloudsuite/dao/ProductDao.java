package com.infor.cloudsuite.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.infor.cloudsuite.dto.ProductInfoDto;
import com.infor.cloudsuite.dto.ProductUserProductDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.platform.jpa.ExtJpaRepository;

/**
 * Data Access object for the Product table
 * User: bcrow
 * Date: 10/24/11 2:49 PM
 */
public interface ProductDao extends ExtJpaRepository<Product, Long> {

    /**
     * Find a list of UserProducts in a joined and condensed transfer object.
     *
     * @param userId user to search for.
     * @return Processed and returnable list of Users and their UserProducts
     */
    List<ProductUserProductDto> findProductUserProduct(Long userId);

    /**
     * Find a product by its shortName
     *
     * @param shortName the name to search for
     * @return The single found product.
     */
    Product findByShortName(String shortName);
    
    List<Product> findByTrialsAvailable(Boolean trialsAvailable);

    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.ProductUserProductDto" +
            "(prod.id, prod.name, prod.shortName, userProds.owned, prod.trialsAvailable, " +
            " prod.deploymentsAvailable, prod.ieOnly, prod.tileSize, prod.tileOrder, " +
            " userProds.trialAvailable, userProds.launchAvailable, userProds.id, "+
            " prod.displayName1, prod.displayName2, prod.displayName3) " +
            " FROM Product prod " +
            " JOIN prod.userProducts userProds WHERE userProds.id.user.id = ?1 AND userProds.owned = true")
    List<ProductUserProductDto> findOwnedProductUserProductByUserId(Long userId);

    @SuppressWarnings("SpringDataJpaMethodInconsistencyInspection")
    @Query("select new com.infor.cloudsuite.dto.ProductUserProductDto" +
            "(prod.id, prod.name, prod.shortName, userProds.owned, prod.trialsAvailable, "+
            " prod.deploymentsAvailable, prod.ieOnly, prod.tileSize, prod.tileOrder, " +
            " userProds.trialAvailable, userProds.launchAvailable, userProds.id, " +
            " prod.displayName1, prod.displayName2, prod.displayName3) " +
            " FROM Product prod" +
            " JOIN prod.userProducts userProds WHERE userProds.id.user.id = ?1 AND userProds.id.product.id= ?2")
    ProductUserProductDto findProductUserProductByUserIdAndProductId(Long userId, Long productId);

    @Query("select new com.infor.cloudsuite.dto.ProductInfoDto(prod.id, prod.shortName, prod.name, " +
            "prod.trialsAvailable, prod.deploymentsAvailable, prod.tileOrder) " +
            "From Product prod")
    List<ProductInfoDto> findProductInfoDtos();
    
    @Query("select prod from Product prod where prod.id IN (?1)")
    List<Product> getByIdList(List<Long> ids);
}