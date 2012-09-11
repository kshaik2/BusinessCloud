package com.infor.cloudsuite.entity;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * User: bcrow
 * Date: 3/20/12 3:29 PM
 */
@Embeddable
public class CSLocale {
	@JsonIgnore
	private static final String LS=System.getProperty("line.separator");
	
    private String language;
    private String country;
    private String variant;

    public CSLocale() {
        
    }
    
    public CSLocale(Locale locale) {
        this.language = locale.getLanguage();
        this.country = locale.getCountry();
        this.variant = locale.getVariant();

    }
    

    
    public CSLocale(String language) {
        this(language,"", "");
    }

    public CSLocale(String language, String country) {
        this(language, country, "");
    }

    public CSLocale(String language, String country, String variant) {
        this.language = language==null?"":language;
        this.country = country==null?"":country;
        this.variant = variant==null?"":variant;
    }

    @Transient
    public Locale getLocale() {
        return new Locale(
                this.language==null?"":this.language,
                this.country==null?"":this.country,
                this.variant==null?"":this.variant);
    }
    
    @Column(length = 10)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Column(length = 10)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(length = 10)
    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
    @JsonIgnore
    public StringBuilder debugOutput() {
    	StringBuilder builder=new StringBuilder();
    	builder.append("language:").append(getLanguage()).append(LS);
    	builder.append("country:").append(getCountry()).append(LS);
    	builder.append("variant:").append(getVariant()).append(LS);
    	
    	return builder;
    }
}
