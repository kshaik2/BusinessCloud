package com.infor.cloudsuite.platform.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.provider.token.ExpiredOAuthTokenException;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.InvalidOAuthTokenException;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.transaction.annotation.Transactional;

import com.infor.cloudsuite.dao.TokenStoreDao;
import com.infor.cloudsuite.dao.UserDao;
import com.infor.cloudsuite.entity.TokenStore;

/**
 * User: bcrow
 * Date: 8/3/12 3:47 PM
 */
public class BCTokenServices extends InMemoryProviderTokenServices {
    private static Logger logger = LoggerFactory.getLogger(BCTokenServices.class);

    @Resource
    private TokenStoreDao tokenStoreDao;
    @Resource
    private UserDao userDao;
    @Resource
    private SecurityService securityService;

    @Override
    public OAuthProviderToken createUnauthorizedRequestToken(String consumerKey, String callbackUrl) throws AuthenticationException {
        logger.debug("Create Unathorized Request Token: " + callbackUrl);
        final OAuthProviderToken requestToken = super.createUnauthorizedRequestToken(consumerKey, callbackUrl);
        logger.debug("Unathorized Request Token: " + requestToken.getValue());
        return requestToken;
    }

    @Override
    @Transactional
    public OAuthAccessProviderToken createAccessToken(String requestToken) throws AuthenticationException {
        logger.debug("Create Access Token: " + requestToken);

        OAuthProviderTokenImpl tokenImpl = readToken(requestToken);

        if (tokenImpl == null) {
            throw new InvalidOAuthTokenException("Invalid token: " + requestToken);
        } else if (isExpired(tokenImpl)) {
            removeToken(requestToken);
            onTokenRemoved(tokenImpl);
            throw new ExpiredOAuthTokenException("Expired token.");
        } else if (tokenImpl.isAccessToken()) {
            throw new InvalidOAuthTokenException("Not a request token.");
        } else if (tokenImpl.getUserAuthentication() == null) {
            throw new InvalidOAuthTokenException("Request token has not been authorized.");
        }

        OAuthProviderTokenImpl accessToken;

        OAuthProviderTokenImpl requestTokenImpl = removeToken(requestToken);
        if (requestTokenImpl != null) {
            onTokenRemoved(requestTokenImpl);
        }

        Object authToken = tokenImpl.getUserAuthentication().getPrincipal();
        accessToken = authTokens.get(authToken);
        if (accessToken == null) {
            String tokenValue;
            String secret;
            if (authToken instanceof SecurityUser) {
                logger.debug("Security User Token lookup.");
                final SecurityUser securityUser = (SecurityUser) authToken;
                final TokenStore storedToken = tokenStoreDao.findByUser_IdAndConsumerId(securityUser.getId(), tokenImpl.getConsumerKey());
                if (storedToken != null) {
                    logger.debug("Token found");
                    tokenValue = storedToken.getTokenValue();
                    secret = storedToken.getSecret();
                } else {
                    logger.debug("Token not found, storing a new one.");
                    byte[] secretBytes = new byte[getTokenSecretLengthBytes()];
                    getRandom().nextBytes(secretBytes);
                    secret = new String(Base64.encodeBase64(secretBytes));
                    tokenValue = UUID.randomUUID().toString();
                    TokenStore store = new TokenStore();
                    store.setConsumerId(tokenImpl.getConsumerKey());
                    store.setTokenValue(tokenValue);
                    store.setSecret(secret);
                    store.setUser(userDao.getReference(securityUser.getId()));
                    tokenStoreDao.save(store);
                }
            } else {
                byte[] secretBytes = new byte[getTokenSecretLengthBytes()];
                getRandom().nextBytes(secretBytes);
                secret = new String(Base64.encodeBase64(secretBytes));
                tokenValue = UUID.randomUUID().toString();
            }


            accessToken = new OAuthProviderTokenImpl();
            accessToken.setAccessToken(true);
            accessToken.setConsumerKey(tokenImpl.getConsumerKey());
            accessToken.setUserAuthentication(tokenImpl.getUserAuthentication());
            accessToken.setSecret(secret);
            accessToken.setValue(tokenValue);
            accessToken.setTimestamp(System.currentTimeMillis());
            onTokenCreated(accessToken);
            storeToken(tokenValue, accessToken);
            authTokens.put(authToken, accessToken);
        } else {
            accessToken.setTimestamp(System.currentTimeMillis());
            storeToken(accessToken.getValue(), accessToken);
        }

        logger.debug("Access Token: " + accessToken.getValue());

        return accessToken;
    }

    private final Map<Object, OAuthProviderTokenImpl> authTokens = new HashMap<>();


    @Override
    public void authorizeRequestToken(String requestToken, String verifier, Authentication authentication) throws AuthenticationException {
        logger.debug("Authorize Request Token: " + requestToken, verifier, authentication.getName());

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("User not authenticated");
        }

        super.authorizeRequestToken(requestToken, verifier, authentication);
    }
}
