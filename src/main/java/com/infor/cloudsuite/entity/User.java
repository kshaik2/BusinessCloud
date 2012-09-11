package com.infor.cloudsuite.entity;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.infor.cloudsuite.service.StringDefs;

/**
 * User: bcrow
 * Date: 9/29/11 12:05 AM
 */

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})})
@Cacheable
public class User extends DatedDataEntity {
    private Long id;
    private String username;
    private String password;
    private Boolean active;
    private String firstName;
    private String lastName;
    private String language;
    private Company company; //this will probably be 1-* later.
    private String address1;
    private String address2;
    private String phone;
    private String country;
    private Boolean inforCustomer = Boolean.FALSE;
    private Boolean inforPartner = Boolean.FALSE;
    private LeadStatus leadStatus = LeadStatus.NONE;

    private Set<Role> roles;
    private Map<Long, UserProduct> userProducts;


    @GenericGenerator(name = "sequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "User_SEQ"),
                    @Parameter(name = "initial_value", value = StringDefs.SEQ_INITIAL_VALUE),
                    @Parameter(name = "optimizer", value = StringDefs.SEQ_OPTIMIZER),
                    @Parameter(name = "increment_size", value = StringDefs.SEQ_INCREMENT)})
    @Id
    @GeneratedValue(generator = "sequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @Column(length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(length = 50)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(length = 50)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(length = 10)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Column(length = 100)
    public String getAddress1() {
        return address1;
    }


    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @Column(length = 100)
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @Column(length = 50)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Column(length = 100)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getInforCustomer() {
        return inforCustomer;
    }

    public void setInforCustomer(Boolean inforCustomer) {
        this.inforCustomer = inforCustomer;
    }


    public Boolean getInforPartner() {
        return inforPartner;
    }

    public void setInforPartner(Boolean inforPartner) {
        this.inforPartner = inforPartner;
    }


    public LeadStatus getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(LeadStatus leadStatus) {
        this.leadStatus = leadStatus;
    }

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "user_roles")
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    public Set<Role> getRoles() {
        if (roles == null) {
            roles = new HashSet<>(1);
        }
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @Transient
    public boolean isAnAdmin() {
        return roleInSet(Role.ADMIN_ROLES, getRoles());
    }

    private boolean roleInSet(EnumSet<Role> roleSet, Collection<Role> roles) {
        for (Role role : roles) {
            if (roleSet.contains(role)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    @Transient
    public boolean isAnInfor24Admin() {
        return roleInSet(Role.INFOR24_ROLES, getRoles());
    }

    @JsonIgnore
    @Transient
    public boolean isASuperAdmin() {
        return getRoles().contains(Role.ROLE_SUPERADMIN);

    }

    @JsonIgnore
    @Transient
    public boolean isSalesRole() {
        return roleInSet(Role.SALES_ROLES, getRoles());
    }

    @JsonIgnore
    @OneToMany(mappedBy = "id.user")
    @MapKeyColumn(name = "product_id")
    public Map<Long, UserProduct> getUserProducts() {
        if (userProducts == null) {
            userProducts = new HashMap<>();
        }
        return userProducts;
    }

    public void setUserProducts(Map<Long, UserProduct> userProducts) {
        this.userProducts = userProducts;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    public Date getCreatedAt() {
        return super.createdAt;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return super.updatedAt;
    }
}
