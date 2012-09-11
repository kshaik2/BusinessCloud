package com.infor.cloudsuite.service.component;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dto.TrialDto;
import com.infor.cloudsuite.entity.Product;
import com.infor.cloudsuite.entity.ProductVersion;
import com.infor.cloudsuite.entity.Region;
import com.infor.cloudsuite.entity.TrialRequest;
import com.infor.cloudsuite.entity.User;
import com.infor.cloudsuite.platform.components.EmailProvider;
import com.infor.cloudsuite.platform.components.MessageProvider;
import com.infor.cloudsuite.platform.components.RequestServices;
import com.infor.cloudsuite.platform.components.TemplateProvider;
import com.infor.cloudsuite.service.StringDefs;

@Component
public class TrialEmailComponent {
    @Resource
    private MessageProvider messageProvider;

    @Resource
    private TemplateProvider templateProvider;

    @Resource
    private RequestServices requestServices;

    private EmailProvider emailProvider;

    @Resource
    public void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }


    public void sendTrialsLowEmail(ProductVersion productVersion, long trialsLeft) {
        String address = StringDefs.BC_SUPPORT_EMAIL;
        String subject = messageProvider.getMessage(StringDefs.MESSAGE_LOW_TRIALS_SUBJECT, productVersion.getProduct().getShortName()+":"+productVersion.getName());

        Map<String, Object> data = new HashMap<>(2);
        data.put("productName", productVersion.getProduct().getName()+":"+productVersion.getName());
        data.put("trialsLeft", trialsLeft);

        String body = templateProvider.processTemplate(StringDefs.MESSAGE_LOW_TRIALS_TEMPLATE, data);
        emailProvider.sendEmailAsync(address, subject, body);
    }

    public void sendTrialsGoneEmail(ProductVersion productVersion) {
        String address = StringDefs.BC_SUPPORT_EMAIL;
        String subject = messageProvider.getMessage(StringDefs.MESSAGE_NO_TRIALS_SUBJECT, productVersion.getProduct().getShortName()+":"+productVersion.getName());

        Map<String, Object> data = new HashMap<>(2);
        data.put("productName", productVersion.getProduct().getName()+":"+productVersion.getName());

        String body = templateProvider.processTemplate(StringDefs.MESSAGE_NO_TRIALS_TEMPLATE, data);
        emailProvider.sendEmailAsync(address, subject, body);
    }

    public void sendTrialConfirmationEmail(User user, Locale locale, Product product, TrialDto dto, String proxyUrl) {
        String address = user.getUsername();
        String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_SUBJECT, locale);

        Map<String, Object> data = new HashMap<>(2);
        data.put("product", product);
        data.put("trialDto", dto);
        data.put("proxyUrl", proxyUrl);
        String body = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_TEMPLATE, locale, data);
        emailProvider.sendEmailAsync(address, subject, body);
    }

    public Future<String> sendTrialRequestStaleEmail(TrialRequest staleRequest) {

        String productList = this.productNamesStrungForEmail(staleRequest.getProductVersions());

        HashMap<String, Object> templateMap = new HashMap<>(6);
        templateMap.put("created", staleRequest.getCreatedAt());
        templateMap.put("user", staleRequest.getUser());
        templateMap.put("region", staleRequest.getRegion());
        templateMap.put("productList", productList);
        templateMap.put("comment", staleRequest.getComment());

        final String trialRequestContact = StringDefs.BUSINESSCLOUD_EMAIL;
        final String toAddress;
        if (trialRequestContact != null) {
            toAddress = trialRequestContact;
        } else {
            toAddress = StringDefs.DEFAULT_TRIAL_EMAIL_ADDRESS;
        }
        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_STALE_SUBJECT, productList);
        final String emailBody = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_REQUEST_STALE_TEMPLATE, templateMap);
        return emailProvider.sendEmailAsync(toAddress, subject, emailBody);
    }

    public Future<String> sendTrialExpirationNotificationEmail(String emailAddress, String url, Date expiration, boolean html) {

        HashMap<String, Object> templateMap = new HashMap<>(2);
        templateMap.put("trialLink", url);
        templateMap.put("expiration", expiration);
        
        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_EXPIRATION_NOTIFICATION_SUBJECT, expiration);
        final String emailBody = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_EXPIRATION_NOTIFICATION_TEMPLATE, templateMap);
        return emailProvider.sendEmailAsync(emailAddress, subject, emailBody);
    }
    
    public Future<String> sendTrialRequestEmail(User user, List<ProductVersion> productVersions, Region region, String requestKey, String comment, HttpServletRequest request) {

        HashMap<String, Object> templateMap = new HashMap<>(6);
        templateMap.put("user", user);
        templateMap.put("productList", productNamesStrungForEmail(productVersions));
        templateMap.put("region", region);
        templateMap.put("comment", comment);
        final UriBuilder uriBuilder = requestServices.getContextUriBuilder(request);
        uriBuilder.path("services").path("trialService").path("{approve}").path("{requestKey}");
        URI approvalURI = uriBuilder.build("approveRequest", requestKey);
        URI deleteURI = uriBuilder.build("deleteRequest", requestKey);
        templateMap.put("approveLink", approvalURI.toString());
        templateMap.put("deleteLink", deleteURI);

        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_SUBJECT, this.productNamesStrungForEmail(productVersions));
        final String emailBody = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_REQUEST_TEMPLATE, templateMap);
        return emailProvider.sendEmailAsync(StringDefs.BC_LEADS_EMAIL, subject, emailBody);
    }

    public void sendTrialRequestConfimationEmail(User user, List<ProductVersion> productVersions, Locale locale) {
        String address = user.getUsername();
        String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_CONFIRM_SUBJECT, locale, this.productNamesStrungForEmail(productVersions));

        Map<String, Object> data = new HashMap<>(2);
        data.put("name", user.getFirstName());
        data.put("productList", this.productNamesStrungForEmail(productVersions));
        String body = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_REQUEST_CONFIRM_TEMPLATE, locale, data);
        emailProvider.sendEmailAsync(address, subject, body);
    }


    public Future<String> sendAWSTrialRequestEmail(User user, List<ProductVersion> productVersions,
            Region region, String requestKey, String comment,
            HttpServletRequest request) {
        HashMap<String, Object> templateMap = new HashMap<>(6);
        templateMap.put("user", user);
        templateMap.put("productList", this.productNamesStrungForEmail(productVersions));
        templateMap.put("region", region);
        templateMap.put("comment", comment);
        final UriBuilder uriBuilder = requestServices.getContextUriBuilder(request);
        uriBuilder.path("services").path("trialService").path("{approve}").path("{requestKey}");

        //TO DO--MUST/MIGHT change this to drill down to the specific "View Request"
        //URI approvalURI = uriBuilder.build("approveRequest", requestKey);
        URI siteURI=uriBuilder.path("cloud.jsp").build("viewRequest",requestKey);
        templateMap.put("siteLink", siteURI.toString());

        final String subject = messageProvider.getMessage(StringDefs.MESSAGE_TRIAL_REQUEST_SUBJECT);
        final String emailBody = templateProvider.processTemplate(StringDefs.MESSAGE_TRIAL_REQUEST_TEMPLATE, templateMap);
        return emailProvider.sendEmailAsync(StringDefs.DEFAULT_TRIAL_EMAIL_ADDRESS, subject, emailBody);
    }


    public void sendAWSTrialConfirmationEmail(User user,
            List<ProductVersion> productVersions, Locale language) {

        String address = user.getUsername();
        String subject = messageProvider.getMessage(StringDefs.MESSAGE_AWS_TRIAL_REQUEST_CONFIRM_SUBJECT, language);

        Map<String, Object> data = new HashMap<>(2);
        data.put("name", user.getFirstName());
        data.put("productList", this.productNamesStrungForEmail(productVersions));
        String body = templateProvider.processTemplate(StringDefs.MESSAGE_AWS_TRIAL_REQUEST_CONFIRM_TEMPLATE, language, data);
        emailProvider.sendEmailAsync(address, subject, body);

    }

    public String productNamesStrungForEmail(List<ProductVersion> productVersions) {
        StringBuilder builder=new StringBuilder();
        boolean first=true;
        for (ProductVersion productVersion : productVersions) {
            if (!first) {
                builder.append(',');
            }
            builder.append(productVersion.getProduct().getName().trim()).append(":").append(productVersion.getName());
            first=false;
        }
        return builder.toString();
    }
}
