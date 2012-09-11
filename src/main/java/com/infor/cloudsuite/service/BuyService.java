package com.infor.cloudsuite.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.ConsultRequestDao;
import com.infor.cloudsuite.dao.ProductDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.dto.BuyConsultDto;
import com.infor.cloudsuite.entity.ConsultRequest;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.CSWebApplicationException;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.platform.security.SecurityService;
import com.infor.cloudsuite.platform.security.SecurityUser;

/**
 * User: bcrow
 * Date: 10/28/11 8:59 AM
 */
@Service
@Path("buy")
public class BuyService {
    @Resource
    private ProductDao productDao;
    @Resource
    private SecurityService securityService;
    @Resource
    private TemplateProvider templateProvider;
    @Resource
    private MessageProvider messageProvider;
    @Resource
    private ConsultRequestDao consultRequestDao;
    @Resource
    private UserDao userDao;

    private EmailProvider emailProvider;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(StringDefs.ROLE_EXTERNAL)
    @Transactional
    public BuyConsultDto requestConsult(BuyConsultDto buyConsultDto) {
        final Product product;
        product = getProduct(buyConsultDto);
        
        SecurityUser secUser = securityService.getCurrentUser();
        User currentUserRef = userDao.getReference(secUser.getId());
        saveConsult(currentUserRef, product, new Date(), buyConsultDto.getEmail(), buyConsultDto.getPhone());
        
        sendConsultEmail(buyConsultDto, product, secUser);

        sendConsultConfirmationEmail(product, secUser);
        
        return buyConsultDto;
    }

    private Product getProduct(BuyConsultDto buyConsultDto) {
        Product product;
        if (buyConsultDto.getProductId().equals(-1L))
        {
            product = new Product();
            product.setId(null);
            product.setName("General Information");
            product.setShortName("GeneralConsult");
        } else {
            product = productDao.findOne(buyConsultDto.getProductId());
            if(product == null) {
                throw new CSWebApplicationException(StringDefs.GENERAL_ERROR_CODE, "Product does not exist.");
            }
        }
        return product;
    }

    private void sendConsultConfirmationEmail(Product product, SecurityUser secUser) {
        String consultConfirmBody = templateProvider.processTemplate(StringDefs.MESSAGE_CONSULT_CONFIRM_TEMPLATE, secUser.getLanguage(), null);
        final String confirmSubject = messageProvider.getMessage(StringDefs.MESSAGE_CONSULT_CONFIRM_SUBJECT, secUser.getLanguage(), product.getName());
        emailProvider.sendEmailAsync(secUser.getUsername(), confirmSubject, consultConfirmBody);
    }

    private void sendConsultEmail(BuyConsultDto buyConsultDto, Product product, SecurityUser secUser) {
        Map<String, Object> templateMap = new HashMap<>(3);
        templateMap.put("product", product);
        templateMap.put("dto", buyConsultDto);
        templateMap.put("secUser", secUser);
        String prodCosultBody = templateProvider.processTemplate(StringDefs.MESSAGE_CONSULT_TEMPLATE, Locale.getDefault(),templateMap);

        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_CONSULT_SUBJECT, Locale.getDefault(), product.getName());

        String email = StringDefs.CLOUDSUITE_EMAIL;

        emailProvider.sendEmailAsync(email, subject, prodCosultBody);
    }

    private void saveConsult(User user, Product product, Date date, String email, String phone) {
        ConsultRequest consultRequest = new ConsultRequest();
        consultRequest.setUser(user);
        //Null product ID means a general request.
        consultRequest.setProduct(product.getId() != null ? product : null);
        consultRequest.setEmail(email);
        consultRequest.setPhone(phone);
        consultRequest.setDate(date);
        consultRequestDao.save(consultRequest);
    }

    @Resource
    public void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }
}
