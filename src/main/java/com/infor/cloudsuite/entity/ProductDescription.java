package com.infor.cloudsuite.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 3/20/12 3:12 PM
 */
@Entity
@Cacheable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"product_id",
        "language", "country", "variant", "descKey"}))
public class ProductDescription {
    
    private Long id;
    private Product product;
    private CSLocale locale;
    private ProductDescKey descKey;
    private String text;

    @GenericGenerator(name = "sequenceGenerator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "ProductDescription_SEQ"),
            @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
            @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
            @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT) })
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;        
    }

    @ManyToOne
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public CSLocale getLocale() {
        return locale;
    }

    public void setLocale(CSLocale locale) {
        this.locale = locale;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    public ProductDescKey getDescKey() {
        return descKey;
    }

    public void setDescKey(ProductDescKey descKey) {
        this.descKey = descKey;
    }

    @Lob
    @Column(length = 20000)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

