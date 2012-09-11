package com.infor.cloudsuite.service;

/**
 * User: bcrow
 * Date: 10/21/11 10:39 AM
 */
//TODO Rename this class
public interface StringDefs {
    //Session variables
    String VALIDATION_NAME = "validation";
    //Roles
    String ROLE_ADMIN      = "ROLE_ADMIN";
    String ROLE_I24_ADMIN  = "ROLE_I24_ADMIN";
    String ROLE_EXTERNAL   = "ROLE_EXTERNAL";
    String ROLE_SALES      = "ROLE_SALES";
    String ROLE_VALIDATED  = "ROLE_VALIDATED";
    String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";


    //ServiceRoles
    String SERVICEROLE_BASIC_EDGE = "SERVICEROLE_BASIC_EDGE";


    //Path Params
    String VALIDATION_ID  = "validationId";
    String VALIDATION_KEY = "validationKey";
    //Pages
    String ACCOUNT_SETUP_JSP = "accountsetup.jsp";
    String INDEX_JSP         = "index.jsp";
    //Statuses
    String SUCCESS = "Success";
    String FAILURE = "Failure";
    //Error code to return to the front end.
    int VALIDATION_ERROR_CODE = 450;
    int GENERAL_ERROR_CODE = 451;
    //DB Sequencer values
    String SEQ_INITIAL_VALUE = "1000";
    String SEQ_INCREMENT     = "10";
    String SEQ_OPTIMIZER     = "pooled";
    //Time correction to avoid storage issues.
    long TIME_CORRECT = 1000;

    String CLOUDSUITE_EMAIL = "cloudsuitesales@infor.com";
    String NO_REPLY_EMAIL = "no-reply@infor.com";

    String BUSINESSCLOUD_EMAIL = "businesscloud@infor.com";
    String BC_LEADS_EMAIL = "businesscloud-leads@infor.com";
    String BC_SALES_EMAIL = "businesscloud-sales@infor.com";
    String BC_ADMIN_EMAIL = "businesscloud-admin@infor.com";
    String BC_SUPPORT_EMAIL = "businesscloud-support@infor.com";

    String MESSAGE_CONSULT_SUBJECT = "email.consultation.1";
    String MESSAGE_CONSULT_TEMPLATE = "product-consult.ftl";

    String MESSAGE_CONSULT_CONFIRM_SUBJECT = "email.consultation.confirm.1";
    String MESSAGE_CONSULT_CONFIRM_TEMPLATE = "product-consult-confirmation.ftl";

    String MESSAGE_PASSWORD_RESET_SUBJECT="email.resetpassword.1";
    String MESSAGE_PASSWORD_RESET_TEMPLATE="password-reset.ftl";

    String MESSAGE_PASSWORD_CREATE_SUBJECT="email.createpassword.1";
    String MESSAGE_PASSWORD_CREATE_TEMPLATE="password-create.ftl";

    String MESSAGE_ACTIVATION_SUBJECT="email.activation.2";
    String MESSAGE_ACTIVATION_TEMPATE="registration-email.ftl";

    String MESSAGE_TRIAL_SUBJECT="email.trial.1";
    String MESSAGE_TRIAL_TEMPLATE="trial-confirmation.ftl";
    String MESSAGE_TRIAL_PROXY_URL="email.trial.proxy-url";

    String MESSAGE_AWS_TRIAL_REQUEST_SUBJECT="email.aws.trial.request.1";
    String MESSAGE_AWS_TRIAL_REQUEST_TEMPLATE="aws-trial-request.ftl";

    String MESSAGE_TRIAL_REQUEST_SUBJECT="email.trial.request.1";
    String MESSAGE_TRIAL_REQUEST_TEMPLATE="trial-request.ftl";

    String MESSAGE_TRIAL_EXPIRATION_NOTIFICATION_SUBJECT="email.trial.expiration.notification.1"; 
    String MESSAGE_TRIAL_EXPIRATION_NOTIFICATION_TEMPLATE="trial-expiration-notification.ftl";

    String MESSAGE_AWS_TRIAL_REQUEST_CONFIRM_SUBJECT="email.aws.trial.request.confirm.1";
    String MESSAGE_AWS_TRIAL_REQUEST_CONFIRM_TEMPLATE="aws-trial-request-confirmation.ftl";

    String MESSAGE_TRIAL_REQUEST_CONFIRM_SUBJECT="email.trial.request.confirm.1";
    String MESSAGE_TRIAL_REQUEST_CONFIRM_TEMPLATE="trial-request-confirmation.ftl";

    String MESSAGE_TRIAL_REQUEST_STALE_SUBJECT="email.trial.request.stale.1";
    String MESSAGE_TRIAL_REQUEST_STALE_TEMPLATE="trial-request-stale.ftl";

    //0 is the base uri(with a slash) 1 is the guid.
    String MESSAGE_TRIAL_PROXY_URL_DEFAULT="{0}t/?g={1}";

    String MESSAGE_LOW_TRIALS_SUBJECT = "email.low.trials.1";
    String MESSAGE_LOW_TRIALS_TEMPLATE = "trials-low.ftl";

    String MESSAGE_NO_TRIALS_SUBJECT = "email.no.trials.1";
    String MESSAGE_NO_TRIALS_TEMPLATE = "trials-gone.ftl";

    String MESSAGE_EMAIL_STOP_WARNING_SUBJECT = "email.warning.stop.notification.1";
    String MESSAGE_EMAIL_STOP_WARNING_TEMPLATE="deployment-stop.ftl"; 

    int PASSWORD_MIN_LENGTH = 1;
    int TRIAL_LENGTH = 30;
    String KEY_NAME = "KeyName";

    String LANG_REQ_PARAM = "l";
    String SESSION_LOCALE = "locale";    

    String DELETE_USER_NOT_FOUND = "User not found.";
    String DELETE_USER_ADMIN = "Cannot delete a user with an admin role.";

    String INFOR_DOMAIN="infor.com";
    String PRODUCT_DESC_FILENAME_JSON = "ProductAndDescriptions.json";
    String DEFAULT_COUNTRY = "US";
    String DEFAULT_LANGUAGE = "en";

    String DEFAULT_TRIAL_EMAIL_ADDRESS="cloudsuitesupport@infor.com";

}
