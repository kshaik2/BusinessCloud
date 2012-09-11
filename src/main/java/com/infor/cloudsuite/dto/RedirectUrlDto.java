package com.infor.cloudsuite.dto;

/**
 * User: bcrow
 * Date: 12/28/11 11:05 AM
 */
public class RedirectUrlDto {
    private String redirectUrl;

    public RedirectUrlDto() {
    }

    public RedirectUrlDto(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
